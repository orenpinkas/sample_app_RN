package com.outbrain.OBSDK.SFWebView;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.outbrain.OBSDK.Errors.OBErrorReporting;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

class SFWebViewJSInterface {

    private static final String LOG_TAG = "SFWebViewWidget";

    private final SFWebViewWidget mSFWebViewWidget;
    private final WeakReference<Context> ctxRef;

    private final String widgetID;

    public SFWebViewJSInterface(SFWebViewWidget webView, Context ctx, String widgetID) {
        this.mSFWebViewWidget = webView;
        this.ctxRef = new WeakReference<>(ctx);
        this.widgetID = widgetID;
    }

    @JavascriptInterface
    public void postMessage(String message) {
        final Context ctx = ctxRef.get();
        try {
            JSONObject result = new JSONObject(message);
            Log.i(LOG_TAG, "postMessage: " + result);
            if (ctx == null) {
                Log.e(LOG_TAG, "postMessage: ctx is null");
                return;
            }
            if (result.has("height")) {
                final String height = result.getString("height");
                float floatValue = Float.parseFloat(height);
                final int heightInt = (int)floatValue;
                Log.i(LOG_TAG, "JavascriptInterface: " + widgetID + ", height: " + height);
                runOnMainThread(ctx, new Runnable() {
                    public void run() {
                        int bottomPadding = 0;
                        if (SFWebViewWidget.getHeightDelegateWeakReference().get() != null) {
                            SFWebViewHeightDelegate delegate = SFWebViewWidget.getHeightDelegateWeakReference().get();
                            bottomPadding = delegate.bottomPaddingForWidget(widgetID);
                            Log.i(LOG_TAG, "Using delegate - bottomPaddingForWidget: " + bottomPadding + ", " + widgetID);
                        }
                        else {
                            bottomPadding = 50;
                        }
                        mSFWebViewWidget.getLayoutParams().height = (int) convertPxToDp(ctx, floatValue + bottomPadding);
                        mSFWebViewWidget.requestLayout();
                    }
                });
                mSFWebViewWidget.finishUpdatingHeight();
                mSFWebViewWidget.notifyRecsReceivedIfNeeded();
                mSFWebViewWidget.notifyHeightChanged(heightInt);
            }
            if (result.has("bridgeParams")) {
                OutbrainBusProvider.BridgeParamsEvent bridgeParamsEvent = new OutbrainBusProvider.BridgeParamsEvent(result.getString("bridgeParams"));
                Log.i(LOG_TAG, "OutbrainBusProvider post bridgeParamsEvent: " + bridgeParamsEvent.getBridgeParams());
                OutbrainBusProvider.getInstance().post(bridgeParamsEvent);
                SFWebViewWidget.globalBridgeParams = bridgeParamsEvent.getBridgeParams();
            }
            if (result.has("url")) {
                String url = result.getString("url");
                String type = result.getString("type");
                if (type.equals("organic-rec") && !result.isNull("orgUrl")) {
                    String orgUrl = result.getString("orgUrl");
                    mSFWebViewWidget.handleClickOnUrl(url, orgUrl);
                } else {
                    mSFWebViewWidget.handleClickOnUrl(url, null);
                }
            }
            if (result.has("event") && result.optJSONObject("event") != null) {
                JSONObject eventData = result.optJSONObject("event");
                String eventName = eventData.optString("name");
                eventName = (eventName != null) ? eventName : "event_name_missing";
                eventData.remove("name");
                this.mSFWebViewWidget.handleWidgetEvent(eventName, eventData);
            }
            if (result.has("errorMsg")) {
                String errorMsg = result.optString("errorMsg");
                errorMsg = "Bridge: " + errorMsg;
                OBErrorReporting.getInstance().reportErrorToServer(errorMsg);
            }
        } catch (JSONException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private float convertPxToDp(Context context, float px) {
        return px * context.getResources().getDisplayMetrics().density;
    }

    private static void runOnMainThread(Context context, Runnable runnable) {
        if (context != null) {
            Handler mainHandler = new Handler(context.getMainLooper());
            mainHandler.post(runnable);
        }
    }
}
