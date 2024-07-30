package com.outbrain.OBSDK.Viewability;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;
import com.outbrain.OBSDK.SFWebView.OutbrainBusProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

@SuppressWarnings("StatementWithEmptyBody")
public class SFViewabilityService {
    private static SFViewabilityService mInstance = null;
    private static String LOG_TAG = "OBSDK";
    private static final String LOG_VIEWABILITY_URL = "https://log.outbrainimg.com/api/loggerBatch/log-viewability";
    private static final String VIEWABLITY_KEY_FOR_REQUEST_ID_POSITION = "VIEWABLITY_KEY_REQUEST_ID_%s_POSITION_%s"; // requestId and position are included in the key


    private Map<String, OBViewData> vKeytoOBViewDataMap; // Viewability Key --> OBViewData

    private Map<String, Boolean> itemAlreadyReportedMap;
    private Map<String, ViewabilityData> itemsToReportMap;

    private Timer reportViewabilityTimer = null;
    private OkHttpClient httpClient;

    private SFViewabilityService() {

    }

    public static void init(Context appContext) {
        if (mInstance == null) {
            mInstance = new SFViewabilityService();
            mInstance.itemAlreadyReportedMap = new HashMap<>();
            mInstance.itemsToReportMap = new HashMap<>();
            mInstance.vKeytoOBViewDataMap = new HashMap<>();
            mInstance.httpClient = OBHttpClient.getClient(appContext);
        }
    }

    public static SFViewabilityService getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("SFViewabilityService Not initialized, call SFViewabilityService.init() before calling getInstance");
        }
        return mInstance;
    }

    public static void registerOBCardView(OBCardView obCardView, String requestId, String position, long initializationTime) {
        try {
            String key = mInstance.getViewabilityKey(requestId, position);
            mInstance.vKeytoOBViewDataMap.put(key, new OBViewData(requestId, position, initializationTime));
            obCardView.setKey(key);
            if (requestId != null && position != null && !mInstance.didAlreadyReportedKey(obCardView.getKey())) {
                // Log.d("OBSDK", "OBCardView " + obCardView.hashCode() + "- registerOBCardView --> trackViewability: " + obCardView.getKey());
                obCardView.trackViewability();
            }
            else {
                // Log.d("OBSDK", "OBCardView " + obCardView.hashCode() + "- registerOBCardView --> SKIP trackViewability: " + obCardView.getKey());
            }
        }
        catch (Exception e) {
            OBErrorReporting.getInstance().reportErrorToServer("SFViewabilityService - registerOBCardView() - " + e.getLocalizedMessage());
        }
    }

    public static void registerViewGroup(ViewGroup viewGroup, OBRecommendation rec) {
        OBFrameLayout obFrameLayout = new OBFrameLayout(viewGroup.getContext());
        obFrameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewGroup.addView(obFrameLayout, 0);

        registerOBFrameLayout(obFrameLayout, rec);
    }

    public static void registerOBFrameLayout(OBFrameLayout obFrameLayout, OBRecommendation rec) {
        try {
            String requestId = rec.getReqID();
            String position = rec.getPosition();
            long initializationTime = ViewabilityService.getInstance().initializationTimeForReqId(requestId);
            String key = mInstance.getViewabilityKey(requestId, position);
            mInstance.vKeytoOBViewDataMap.put(key, new OBViewData(requestId, position, initializationTime));
            obFrameLayout.setKey(key);
            obFrameLayout.setReqId(requestId);
            if (requestId != null && position != null && !mInstance.didAlreadyReportedKey(obFrameLayout.getKey())) {
                Log.i(LOG_TAG, "OBFrameLayout " + obFrameLayout.hashCode() + "- registerOBCardView --> trackViewability: " + obFrameLayout.getKey());
                obFrameLayout.trackViewability();
            }
            else {
                Log.i(LOG_TAG, "OBFrameLayout " + obFrameLayout.hashCode() + "- registerOBCardView --> SKIP trackViewability: " + obFrameLayout.getKey());
            }
        }
        catch (Exception e) {
            OBErrorReporting.getInstance().reportErrorToServer("SFViewabilityService - registerOBFrameLayout() - " + e.getLocalizedMessage());
        }
    }

    private String getViewabilityKey(String requestId, String position) {
        return String.format(VIEWABLITY_KEY_FOR_REQUEST_ID_POSITION, requestId, position);
    }

    public void startReportViewability(int reportingIntervalMillis) {
        if (this.reportViewabilityTimer != null) {
            // already started
            return;
        }
        this.reportViewabilityTimer = new Timer();
        ReportViewabilityTimerTask reportViewabilityTimerTask = new ReportViewabilityTimerTask(this.itemsToReportMap);
        reportViewabilityTimer.schedule(reportViewabilityTimerTask, reportingIntervalMillis, reportingIntervalMillis);
    }

    public void reportViewabilityForOBViewKey(String key) {
        try {
            if (key == null) {
                Log.e(LOG_TAG, "reportViewabilityForOBViewKey - called with null key");
                return;
            }
            Log.i(LOG_TAG, "reportViewabilityForOBViewKey: " + key);
            OBViewData obViewData = mInstance.vKeytoOBViewDataMap.get(key);

            itemAlreadyReportedMap.put(key, true);

            long timeNowMillis = System.currentTimeMillis();
            long timeElapsedMillis = timeNowMillis - obViewData.sfInitializationTime;
            int recPosition = Integer.parseInt(obViewData.position);
            ViewabilityData viewabilityData = new ViewabilityData(
                    obViewData.requestId,
                    recPosition,
                    timeElapsedMillis
            );
            OutbrainBusProvider.ViewabilityFiredEvent viewabilityFiredEvent = new OutbrainBusProvider.ViewabilityFiredEvent(obViewData.requestId, recPosition);
            Log.i(LOG_TAG, "OutbrainBusProvider post viewabilityFiredEvent: reqId: " + viewabilityFiredEvent.getRequestId() + ", position: " + viewabilityFiredEvent.getPosition());
            OutbrainBusProvider.getInstance().post(viewabilityFiredEvent);

            itemsToReportMap.put(key, viewabilityData);
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "received error: " + e.getLocalizedMessage());
            OBErrorReporting.getInstance().reportErrorToServer("SFViewabilityService - reportViewabilityForOBViewKey() - " + e.getLocalizedMessage());
        }
    }

    public boolean didAlreadyReportedKey(String key) {
        if (key == null) {
            return false;
        }
        return itemAlreadyReportedMap.containsKey(key);
    }

    private static class OBViewData {
        final String requestId;
        final String position;
        final long sfInitializationTime;

        public OBViewData(String requestId, String position, long sfInitializationTime) {
            this.requestId = requestId;
            this.position = position;
            this.sfInitializationTime = sfInitializationTime;
        }
    }

    private static class ViewabilityData {
        final String requestId;
        final int position;
        final long timeElapsedMillis;

        public ViewabilityData(String requestId, int position, long timeElapsedMillis) {
            this.requestId = requestId;
            this.position = position;
            this.timeElapsedMillis = timeElapsedMillis;
        }

        private Map<String, Object> toMap() {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("position", this.position);
            dataMap.put("timeElapsed", this.timeElapsedMillis);
            dataMap.put("requestId", this.requestId);
            return dataMap;
        }

        @Override
        public String toString() {
            return "requestId: " + requestId + ", position: " + position + ", timeElapsedMillis: " + timeElapsedMillis;
        }
    }

    private class ReportViewabilityTimerTask extends TimerTask {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private final Map<String, ViewabilityData> itemsToReportMap;

        public ReportViewabilityTimerTask(Map<String, ViewabilityData> itemsToReportMap) {
            this.itemsToReportMap = itemsToReportMap;
        }

        @Override
        public void run() {
            if (!isAppOnForeground()) {
                Log.d(LOG_TAG, "ReportViewabilityTimerTask - app is NOT in foreground - cancel all running timer tasks");
                ViewTimerTask.cancelAllRunningTimerTasks();
            }
            if (this.itemsToReportMap.isEmpty()) {
                return;
            }
            sendListingViewabilityDataToServer();
        }

        public boolean isAppOnForeground() {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
        }

        private void sendListingViewabilityDataToServer() {
            RequestBody body = getRequestBody();

            if (body == null) {
                return;
            }

            Request request = new Request.Builder()
                    .url(LOG_VIEWABILITY_URL)
                    .post(body)
                    .build();

            Log.i(LOG_TAG, "sendListingViewabilityDataToServer - url: " + LOG_VIEWABILITY_URL);


            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOG_TAG, "Error in sendListingViewabilityDataToServer: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        String msg = "Error in sendListingViewabilityDataToServer Unexpected response code: " + response.code() + " request: " + call.request().toString() +  " body: " + bodyToString(call.request());
                        Log.e(LOG_TAG, msg);
                        OBErrorReporting.getInstance().reportErrorToServer(msg);
                    }
                    else {
                        Log.i(LOG_TAG, "sendListingViewabilityDataToServer - success: " + response.code());
                    }
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            });
        }

        private String bodyToString(final Request request){
            try {
                final Request requestCopy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                requestCopy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                return "bodyToString() failed";
            }
        }

        private RequestBody getRequestBody() {
            Set<String> tempKeySet = new HashSet<>(this.itemsToReportMap.keySet());
            JSONArray jsonArray = new JSONArray();
            try {
                for (String key : tempKeySet) {
                    JSONObject jsonObject = new JSONObject();
                    ViewabilityData viewabilityData = this.itemsToReportMap.remove(key);
                    if (viewabilityData == null) {
                        continue;
                    }
                    for (Map.Entry<String, Object> entry : viewabilityData.toMap().entrySet()) {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    }
                    jsonArray.put(jsonObject);
                }
                Log.i(LOG_TAG, "sendListingViewabilityDataToServer getRequestBody: " + jsonArray);
                return RequestBody.create(jsonArray.toString(), JSON);

            } catch (JSONException e) {
                OBErrorReporting.getInstance().reportErrorToServer("SFViewabilityService - getRequestBody() - " + e.getLocalizedMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
}
