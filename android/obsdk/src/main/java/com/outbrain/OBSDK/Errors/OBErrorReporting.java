package com.outbrain.OBSDK.Errors;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import com.outbrain.OBSDK.Outbrain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OBErrorReporting {
    private static OBErrorReporting mInstance = null;

    private static final String ERROR_REPORTING_URL = "https://widgetmonitor.outbrain.com/WidgetErrorMonitor/api/report";
    private static final String SDK_ERROR_NAME = "ANDROID_SDK_ERROR";

    private String sourceId;
    private String publisherId;
    private String widgetId;
    private String odbRequestUrlParamValue;

    private String partnerKey;
    private String appVersion;
    private OkHttpClient httpClient;

    private OBErrorReporting() {

    }

    public static OBErrorReporting getInstance(){
        if (mInstance == null)
        {
            throw new RuntimeException("OBErrorReporting Not initialized, call OBErrorReporting.init() before calling getInstance");
        }
        return mInstance;
    }


    public static void init(Context appContext, String partnerKey) {
        if (mInstance == null) {
            mInstance = new OBErrorReporting();
            mInstance.httpClient = new OkHttpClient.Builder().build();
            mInstance.partnerKey = partnerKey;

            mInstance.appVersion = "";

            try {
                PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
                mInstance.appVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException ignored) {

            }
        }
    }

    public void reportErrorToServer(String errorMessage) {
        final String url = prepareErrorReportingUrlForMsg(errorMessage);
        Log.i("OBSDK", "reportErrorToServer URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OBSDK", "Error in reportErrorToServer(): " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    Log.e("OBSDK", "Error in OBErrorReporting Unexpected response code: " + response.code());
                }

                if (response.body() != null) {
                    response.body().close();
                }
                Log.i("OBSDK", "OBErrorReporting - success reporting for " + url);
            }
        });
    }

    private String prepareErrorReportingUrlForMsg(String msg) {
        Uri.Builder builder = Uri.parse(ERROR_REPORTING_URL).buildUpon();

        // Event Name for SDK error
        builder.appendQueryParameter("name", SDK_ERROR_NAME);

        JSONObject extraParams = new JSONObject();

        try {
            // Partner Key
            extraParams.put("partnerKey", (this.partnerKey != null) ? this.partnerKey : "(null)");

            // WidgetId
            extraParams.put("widgetId", this.widgetId);

            // SDK Version
            extraParams.put("sdk_version", Outbrain.SDK_VERSION);

            // Device model
            String androidModel = "";
            try {
                androidModel = URLEncoder.encode(Build.MODEL, "utf-8");
            } catch (UnsupportedEncodingException ignored) {

            }
            extraParams.put("dm", androidModel);

            // App Version
            extraParams.put("app_ver", this.appVersion);

            // OS version
            String androidVersion = "";
            try {
                androidVersion = URLEncoder.encode(String.valueOf(Build.VERSION.SDK_INT), "utf-8");
            } catch (UnsupportedEncodingException ignored) {

            }
            extraParams.put("dosv", androidVersion);

            // Random
            String randomInt =  Integer.toString(new Random().nextInt(10000));
            extraParams.put("rand", randomInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Extra Params
        builder.appendQueryParameter("extra",extraParams.toString());

        // SID, PID, URL
        if (this.odbRequestUrlParamValue != null) {
            builder.appendQueryParameter("url", this.odbRequestUrlParamValue);
        }
        if (this.sourceId != null) {
            builder.appendQueryParameter("sId", this.sourceId);
        }
        if (this.publisherId != null) {
            builder.appendQueryParameter("pId", this.publisherId);
        }

        // Error msg
        builder.appendQueryParameter("message", msg != null ? msg : "(null)");

        return builder.build().toString();
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public void setOdbRequestUrlParamValue(String odbRequestUrlParamValue) {
        this.odbRequestUrlParamValue = odbRequestUrlParamValue;
    }
}
