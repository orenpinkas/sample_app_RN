package com.outbrain.OBSDK.VideoUtils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.outbrain.OBSDK.SmartFeed.OBSmartFeed;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import static com.outbrain.OBSDK.OBUtils.runOnMainThread;

@SuppressWarnings("EmptyMethod")
class WebAppInterface {
    private final VideoViewInterface videoViewInterface;
    private final JSONObject videoParamsJsonObject;
    private final WeakReference<Context> ctxRef;

    private final String LOG_TAG = "WebAppInterface";

    /** Instantiate the interface and set the context */
    WebAppInterface(VideoViewInterface videoViewInterface, JSONObject videoParamsJsonObject, Context ctx) {
        EventBus.getDefault().register(this);
        this.videoViewInterface = videoViewInterface;
        this.videoParamsJsonObject = videoParamsJsonObject;
        this.ctxRef = new WeakReference<>(ctx);
    }

    @JavascriptInterface
    public void videoIsReady() {
        Context ctx = ctxRef.get();
        if (ctx != null) {
            VideoUtils.showVideoItem(videoViewInterface, ctx);
        }
    }
    @JavascriptInterface
    public void videoClicked() {

    }
    @JavascriptInterface
    public void videoFinished() {
        Context ctx = ctxRef.get();
        if (ctx != null) {
            VideoUtils.hideVideoItem(videoViewInterface, ctx);
        }
    }

    @JavascriptInterface
    public void pageIsReady() {
        Log.i(LOG_TAG, "pageIsReady");
        String script = "odbData(" + videoParamsJsonObject.toString() + ")";
        runScriptOnWebView(script);
    }

    @JavascriptInterface
    public void sdkLog(String logMsg) {
        Log.i(LOG_TAG, logMsg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveVideoPauseNotification(OBSmartFeed.PauseVideoEvent event) {
        Log.i(LOG_TAG, "receiveVideoPauseNotification");
        if (videoViewInterface.getWebView() == null) {
            Log.i(LOG_TAG, "No video currently playing...");
            return;
        }
        String script = "systemVideoPause()";
        runScriptOnWebView(script);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveOnDetachFromWindowNotification(OBSmartFeed.OnWebViewDetachedFromWindowEvent event) {
        Log.i(LOG_TAG, "receiveOnDetachFromWindowNotification");
        EventBus.getDefault().unregister(this);
    }

    private void runScriptOnWebView(final String script) {
        Log.i(LOG_TAG, "js: " + script);
        Context ctx = ctxRef.get();
        if (ctx == null) {
            return;
        }
        runOnMainThread(ctx, new Runnable() {
            public void run() {
                videoViewInterface.getWebView().evaluateJavascript(script, null);
            }
        });
    }
}
