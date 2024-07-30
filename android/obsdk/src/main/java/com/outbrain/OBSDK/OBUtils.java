package com.outbrain.OBSDK;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.outbrain.OBSDK.FetchRecommendations.OBPlatformRequest;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class OBUtils {
    public static void runOnMainThread(Context context, Runnable runnable) {
        if (context != null) {
            Handler mainHandler = new Handler(context.getMainLooper());
            mainHandler.post(runnable);
        }
    }

    public static String getUrlFromOBRequest(OBRequest request) {
        boolean isPlatfromRequest = request instanceof OBPlatformRequest;
        if (isPlatfromRequest) {
            OBPlatformRequest platformRequest = (OBPlatformRequest)request;
            return platformRequest.getBundleUrl() != null ? platformRequest.getBundleUrl() : platformRequest.getPortalUrl();
        }
        else {
            return request.getUrl();
        }
    }

    public static String getAppIdentifier(Context applicationContext) {
        return applicationContext.getPackageName();
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        if (stringId == 0 && applicationInfo.nonLocalizedLabel == null) {
            return applicationInfo.name;
        }
        return stringId == 0 ?
                applicationInfo.nonLocalizedLabel.toString() :
                context.getString(stringId);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getOSVersion() {
        String androidVersion = "";
        try {
            androidVersion = URLEncoder.encode(String.valueOf(Build.VERSION.SDK_INT), "utf-8");
        } catch (UnsupportedEncodingException ignored) {

        }

        return androidVersion;
    }
}
