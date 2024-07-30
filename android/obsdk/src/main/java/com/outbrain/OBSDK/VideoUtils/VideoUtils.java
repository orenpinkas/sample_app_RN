package com.outbrain.OBSDK.VideoUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.outbrain.OBSDK.Entities.OBResponseRequest;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.OBClickListener;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.OutbrainService;
import com.outbrain.OBSDK.SmartFeed.SFItemData;
import com.outbrain.OBSDK.Utilities.OBAdvertiserIdFetcher;

import org.json.JSONException;
import org.json.JSONObject;

import static com.outbrain.OBSDK.OBUtils.getAppIdentifier;
import static com.outbrain.OBSDK.OBUtils.getApplicationName;
import static com.outbrain.OBSDK.OBUtils.runOnMainThread;

public class VideoUtils {

    public static void hideVideoItem(final VideoViewInterface videoViewInterface, Context ctx) {
        runOnMainThread(ctx, new Runnable() {
            @Override
            public void run() {
                videoViewInterface.getWebView().setVisibility(View.INVISIBLE);
                videoViewInterface.getFrameLayout().setVisibility(View.INVISIBLE);
                videoViewInterface.getWrapperView().setVisibility(View.VISIBLE);
            }
        });
    }

    public static void showVideoItem(final VideoViewInterface videoViewInterface, Context ctx) {
        runOnMainThread(ctx, new Runnable() {
            @Override
            public void run() {
                videoViewInterface.getWebView().setVisibility(View.VISIBLE);
                videoViewInterface.getFrameLayout().setVisibility(View.VISIBLE);
                videoViewInterface.getWrapperView().setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"}) // this function will run for devices with sdk > 18
    public static void initVideo(
            final VideoViewInterface videoViewInterface,
            final OBClickListener obClickListener,
            final SFItemData sfItem,
            final String articleUrl,
            final Context ctx)
    {
        hideVideoItem(videoViewInterface, ctx);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final JSONObject videoParamsJsonObject = VideoUtils.getVideoParamsJsonObject(sfItem.getResponseRequest(), sfItem.getSettings());
                final String videoUrl = VideoUtils.addQueryParametersToVideoUrl(sfItem.getVideoUrl(), articleUrl, ctx);

                runOnMainThread(ctx, new Runnable() {
                    @Override
                    public void run() {
                        WebView webView = videoViewInterface.getWebView();
                        webView.setFocusable(false);

                        // https://stackoverflow.com/questions/32513157/android-failed-to-execute-play-on-htmlmediaelement-api-can-only-be-initia
                        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setSupportMultipleWindows(true);
                        webView.setVerticalScrollBarEnabled(false);
                        webView.setHorizontalScrollBarEnabled(false);
                        webView.setWebChromeClient(new VideoWebChromeClient(obClickListener));
                        webView.addJavascriptInterface(
                                new WebAppInterface(videoViewInterface, videoParamsJsonObject, ctx),
                                "OBAndroidBridge"
                        );
                        Log.i("VideoUtils", "loadUrl: " + videoUrl);
                        webView.loadUrl(videoUrl);
                    }
                });
            }
        });
    }

    @Nullable
    public static JSONObject getVideoParamsJsonObject(OBResponseRequest request, OBSettings settings) {
        JSONObject videoParamsJsonObject;
        videoParamsJsonObject = new JSONObject();
        try {
            videoParamsJsonObject.put(
                    "request",
                    request.getJSONObject()
            );
            JSONObject settingsJson = settings.getJSONObject();
            videoParamsJsonObject.put(
                    "settings",
                    settingsJson
            );

        } catch (JSONException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return videoParamsJsonObject;
    }

    public static String addQueryParametersToVideoUrl(String videoUrl, final String articleUrl, Context ctx) {
        // is test mode
        OutbrainService outbrainService = OutbrainService.getInstance();
        String isTestMode = outbrainService.isTestMode() ? "true" : "false";
        String appName = getApplicationName(ctx);
        String appBundleId = getAppIdentifier(ctx);
        return Uri.parse(videoUrl)
                .buildUpon()
                .appendQueryParameter("platform", "android")
                .appendQueryParameter("testMode", isTestMode)
                .appendQueryParameter("inApp", "true")
                .appendQueryParameter("deviceAid", getAdId(ctx))
                .appendQueryParameter("appName", appName)
                .appendQueryParameter("appBundle", appBundleId)
                .appendQueryParameter("articleUrl", articleUrl)
                .appendQueryParameter("sdkVersion", Outbrain.SDK_VERSION)
                .build()
                .toString();
    }

    private static String getAdId(Context applicationContext) {
        AdvertisingIdClient.Info adInfo = OBAdvertiserIdFetcher.getAdvertisingIdInfo(applicationContext);
        if (adInfo != null) {
            if (!adInfo.isLimitAdTrackingEnabled()) {
                return adInfo.getId();
            }
            else {
                return "null";
            }
        }
        else {
            // We got Exception from getAdvertisingIdInfo()
            return "na";
        }
    }
}
