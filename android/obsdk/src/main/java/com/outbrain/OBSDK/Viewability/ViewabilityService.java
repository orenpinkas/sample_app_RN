package com.outbrain.OBSDK.Viewability;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;
import com.outbrain.OBSDK.OBUtils;
import com.outbrain.OBSDK.Utilities.AdsChoicesManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("FieldCanBeLocal")
public class ViewabilityService {

    private static ViewabilityService mInstance = null;

    @SuppressWarnings("FieldCanBeLocal")
    private final String VIEWABLITY_KEY_FOR_REQUEST_ID = "VIEWABLITY_KEY_REQUEST_ID_%s"; // requestId key

    private final String VIEWABLITY_SHARED_PREFS_NAME = "VIEWABLITY_SHARED_PREFS";
    private final String VIEWABLITY_ENABLED_PREFS_KEY = "VIEWABLITY_ENABLED_PREFS_KEY";
    private final String VIEWABLITY_THRESHOLD_PREFS_KEY = "VIEWABLITY_THRESHOLD_PREFS_KEY";

    private OkHttpClient httpClient;
    private WeakReference<Context> contextWeakReference;

    private final HashMap<String, ViewabilityData> viewabilityDataMap = new HashMap<>();

    private ViewabilityService() {

    }

    public static void init(Context appContext) {
        if (mInstance == null) {
            mInstance = new ViewabilityService();
            mInstance.httpClient = OBHttpClient.getClient(appContext);
            mInstance.contextWeakReference = new WeakReference<>(appContext);
        }
    }

    public static ViewabilityService getInstance(){
        if (mInstance == null)
        {
            throw new RuntimeException("ViewabilityService Not initialized, call ViewabilityService.init() before calling getInstance");
        }
        return mInstance;
    }

    public void reportRecsReceived(OBRecommendationsResponse response, long requestStartTime) {
        try {
            _reportRecsReceived(response, requestStartTime);
        }
        catch (Exception e) {
            OBErrorReporting.getInstance().reportErrorToServer("ViewabilityService - reportRecsReceived() - " + e.getLocalizedMessage());
        }
    }

    private void _reportRecsReceived(OBRecommendationsResponse response, long requestStartTime) {
        final Context appContext = appContext();

        if (appContext == null) {
            return; // support for unit test..
        }

        // Update Viewability Settings
        updateViewabilitySettings(response, appContext);
        
        if (! isViewabilityEnabled(appContext)) {
            return;
        }

        ViewabilityData data = createViewabilityDataFromResponse(response , requestStartTime);

        // Save ViewabilityData in viewabilityDataMap
        String keyForRequestId = viewabilityKeyForRequestId(response.getRequest().getReqId());
        viewabilityDataMap.put(keyForRequestId, data);

        // time of processing request
        String timeToProcessRequest = Long.toString((int) (System.currentTimeMillis() - requestStartTime));

        // Send ViewabilityData to Server
        String url = addTmAndOptedOutParams(data.reportServedUrl, timeToProcessRequest, data.optedOut);
        sendViewabilityDataToServer(url);

        AdsChoicesManager.reportViewability(response, appContext);
    }

    public long initializationTimeForReqId(String reqId) {
        String keyForRequestId = viewabilityKeyForRequestId(reqId);
        ViewabilityData data = viewabilityDataMap.get(keyForRequestId);
        if (data != null) {
            return data.requestStartTime;
        }
        return 0;
    }

    public boolean isViewabilityEnabled(Context ctx) {
        SharedPreferences mPrefs = ctx.getApplicationContext().getSharedPreferences(VIEWABLITY_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return  mPrefs.getBoolean(VIEWABLITY_ENABLED_PREFS_KEY, true);
    }

    public int viewabilityThresholdMilliseconds(Context ctx) {
        SharedPreferences mPrefs = ctx.getApplicationContext().getSharedPreferences(VIEWABLITY_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return  mPrefs.getInt(VIEWABLITY_THRESHOLD_PREFS_KEY, 1000); // one second in milliseconds is the default.
    }

    private void sendViewabilityDataToServer(final String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OBSDK", "Error in sendViewabilityDataToServer: " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    String msg = "Error in sendViewabilityDataToServer Unexpected response code: " + response.code() + " url: " + call.request().url().toString();;
                    Log.e("OBSDK", msg);
                    OBErrorReporting.getInstance().reportErrorToServer(msg);
                }

                if (response.body() != null) {
                    response.body().close();
                }
                 Log.i("OBSDK", "ViewabilityService - success reporting for " + url);
            }
        });
    }

    private String addTmAndOptedOutParams(String url, String tm, boolean optedOut) {
        Uri originalUri = Uri.parse(url);
        final Set<String> params = originalUri.getQueryParameterNames();
        final Uri.Builder newUri = originalUri.buildUpon().clearQuery();

        // tm param may exist in the "url" param - we need to replace existing empty value
        for (String param : params) {
            if (param.equals("tm")) {
                continue;
            }
            newUri.appendQueryParameter(param, originalUri.getQueryParameter(param));
        }

        // add "oo" param
        newUri.appendQueryParameter("oo", String.valueOf(optedOut));

        // add TM param
        newUri.appendQueryParameter("tm", tm);

        return newUri.build().toString();
    }

    private void updateViewabilitySettings(OBRecommendationsResponse response, Context appContext) {
        SharedPreferences mPrefs = appContext.getSharedPreferences(VIEWABLITY_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean(VIEWABLITY_ENABLED_PREFS_KEY, response.getSettings().isViewabilityEnabled());
        prefsEditor.putInt(VIEWABLITY_THRESHOLD_PREFS_KEY, response.getSettings().viewabilityThreshold());
        prefsEditor.apply();
    }

    private ViewabilityData createViewabilityDataFromResponse(OBRecommendationsResponse response, long requestStartTime) {
        ViewabilityData data = new ViewabilityData();

        data.rId = response.getRequest().getReqId();
        data.reportServedUrl = response.getSettings().getViewabilityActions().getReportServedUrl();
        data.requestStartTime = requestStartTime;
        data.optedOut = response.getRequest().isOptedOut();

        return data;
    }

    private Context appContext() {
        return this.contextWeakReference.get();
    }

    private String viewabilityKeyForRequestId(String requestId) {
        return String.format(VIEWABLITY_KEY_FOR_REQUEST_ID, requestId);
    }

    private static class ViewabilityData {

        String reportServedUrl;  // report served URL
        String rId; // request ID
        long requestStartTime;
        boolean optedOut;

        public ViewabilityData() { }
    }
}