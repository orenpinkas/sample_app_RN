package com.outbrain.OBSDK.FetchRecommendations;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBError;
import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.Entities.OBOperation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;
import com.outbrain.OBSDK.OutbrainException;
import com.outbrain.OBSDK.Utilities.RecommendationApvHandler;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;
import com.outbrain.OBSDK.Viewability.ViewabilityService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchRecommendationsHandler implements Runnable {

    private final OBLocalSettings localSettings;
    private final OBRequest obRequest;
    private final RecommendationsListener recommendationsListener;
    private final MultivacListener multivacListener;
    private final boolean isMultivacRequest;
    private final Context applicationContext;
    private final RecommendationsTokenHandler recommendationsTokenHandler;
    private final OkHttpClient httpClient;

    @SuppressWarnings("unused")
    public OBRequest getObRequest() {
        return obRequest;
    }

    public FetchRecommendationsHandler(Context applicationContext, OBRequest request, OBLocalSettings localSettings, RecommendationsListener recommendationsListener, RecommendationsTokenHandler recommendationsTokenHandler) {
        this.obRequest = request;
        this.localSettings = localSettings;
        this.recommendationsListener = recommendationsListener;
        this.multivacListener = null;
        this.isMultivacRequest = false;
        this.applicationContext = applicationContext;
        this.recommendationsTokenHandler = recommendationsTokenHandler;
        this.httpClient = OBHttpClient.getClient(applicationContext);
    }

    public FetchRecommendationsHandler(Context applicationContext, OBRequest request, OBLocalSettings localSettings, MultivacListener multivacListener, RecommendationsTokenHandler recommendationsTokenHandler) {
        this.obRequest = request;
        this.localSettings = localSettings;
        this.multivacListener = multivacListener;
        this.recommendationsListener = null;
        this.isMultivacRequest = true;
        this.applicationContext = applicationContext;
        this.recommendationsTokenHandler = recommendationsTokenHandler;
        this.httpClient = OBHttpClient.getClient(applicationContext);
    }

    @Override
    public void run() {
        executeRequest();
    }

    private void executeRequest() {
        final long startTime = System.currentTimeMillis();
        RecommendationsUrlBuilder recommendationsUrlBuilder = new RecommendationsUrlBuilder(localSettings, recommendationsTokenHandler);
        String url = recommendationsUrlBuilder.getUrl(applicationContext, obRequest);
        Log.i("OBSDK", "calling url: " + url);
        final Request okhttpRequest = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(okhttpRequest).execute();
            if (response.body() == null) {
                notifyRecommendationsFailed("Response body is null, status: " + response.code());
                return;
            }

            String responseString = response.body().string();


            /// Oded Mock
            // responseString = readJsonFromMockFile(R.raw.smart_feed_response_weekly_highlights);
            // responseString = readJsonFromMockFile(R.raw.smart_feed_response_branded_carousel);
            // responseString = readJsonFromMockFile(R.raw.smart_feed_response_app_install);

            if (!response.isSuccessful()) {
                OBError obError = OBRecommendationsParser.parseError(responseString);
                String msg;
                if (obError != null) {
                    msg = obError.status.getContent() + " - details: " + obError.status.getDetails() + " - http status: " + response.code();
                }
                else {
                    msg = "Request failed with status: " + response.code();
                }
                notifyRecommendationsFailed(msg);
                return;
            }

            if (isMultivacRequest) {
                handleMultivacResponse(startTime, responseString);
            }
            else {
                // ODB response
                OBRecommendationsResponse recommendationsResponse = OBRecommendationsParser.parse(responseString, obRequest); //Parse

                // Set Error Report Values
                String sourceId = recommendationsResponse.getRequest().getSourceId();
                String publisherId = recommendationsResponse.getRequest().getPublisherId();
                OBErrorReporting.getInstance().setOdbRequestUrlParamValue(obRequest.getUrl());
                OBErrorReporting.getInstance().setWidgetId(obRequest.getWidgetId());
                OBErrorReporting.getInstance().setPublisherId(publisherId);
                OBErrorReporting.getInstance().setSourceId(sourceId);

                handleODBResponse(startTime, recommendationsResponse);
            }
        }
        catch (Exception ex) {
            Log.e("OBSDK", "Error in FetchRecommendationsHandler: " + ex.getLocalizedMessage());
            OBErrorReporting.getInstance().reportErrorToServer("Error in FetchRecommendationsHandler: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            notifyGeneralException(ex);
        }
    }

    public String readJsonFromMockFile(int rawId) throws Exception {
        InputStream is = this.applicationContext.getResources().openRawResource(rawId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    private void handleMultivacResponse(long startTime, String responseString) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseString);
        final boolean hasMore = jsonResponse.optBoolean("hasMore");
        final int feedIdx = jsonResponse.optInt("feedIdx");
        JSONArray cardsJsonArray = jsonResponse.optJSONArray("cards");
        final ArrayList<OBRecommendationsResponse> cardsResponseList = new ArrayList<>();
        for (int i = 0; i < cardsJsonArray.length(); i++) {
            JSONObject cardJson = cardsJsonArray.getJSONObject(i);
            OBRecommendationsResponse recommendationsResponse = new OBRecommendationsResponse(cardJson.optJSONObject("response"), obRequest);
            cardsResponseList.add(recommendationsResponse);
            recommendationsTokenHandler.setTokenForResponse(new OBOperation(obRequest, recommendationsResponse));
            RecommendationApvHandler.updateAPVCacheForResponse(recommendationsResponse.getSettings(), obRequest);
            ViewabilityService.getInstance().reportRecsReceived(recommendationsResponse, startTime);
        }

        notifySuccessfulMultivac(hasMore, feedIdx, cardsResponseList);
    }

    private void handleODBResponse(long startTime, OBRecommendationsResponse recommendationsResponse ) {
        recommendationsTokenHandler.setTokenForResponse(new OBOperation(obRequest, recommendationsResponse));
        RecommendationApvHandler.updateAPVCacheForResponse(recommendationsResponse.getSettings(), obRequest);
        ViewabilityService.getInstance().reportRecsReceived(recommendationsResponse, startTime);
        notifySuccessfullRecommendations(recommendationsResponse);
    }


    private void notifyRecommendationsFailed(final String detailMessage) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isMultivacRequest) {
                    multivacListener.onMultivacFailure(new OutbrainException(detailMessage));
                }
                else {
                    recommendationsListener.onOutbrainRecommendationsFailure(new OutbrainException(detailMessage));
                }
            }
        });
    }

    private void notifyGeneralException(final Exception ex) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isMultivacRequest) {
                    multivacListener.onMultivacFailure(new OutbrainException(ex));
                }
                else {
                    recommendationsListener.onOutbrainRecommendationsFailure(new OutbrainException(ex));
                }
            }
        });
    }

    private void notifySuccessfullRecommendations(final OBRecommendationsResponse recommendations) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                recommendationsListener.onOutbrainRecommendationsSuccess(recommendations); //Return
            }
        });
    }

    private void notifySuccessfulMultivac(final boolean hasMore, final int feedIdx, final ArrayList<OBRecommendationsResponse> cardsResponseList) {
        if (Looper.getMainLooper() == null) {
            // Unit tests support
            multivacListener.onMultivacSuccess(cardsResponseList, feedIdx, hasMore);
        }
        else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    multivacListener.onMultivacSuccess(cardsResponseList, feedIdx, hasMore);
                }
            });
        }
    }
}
