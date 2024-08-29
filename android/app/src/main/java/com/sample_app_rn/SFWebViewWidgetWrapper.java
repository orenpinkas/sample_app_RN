package com.sample_app_rn;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewEventsListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetPolling;


@SuppressLint("ViewConstructor")
public class SFWebViewWidgetWrapper extends ViewGroup {
    /**
     * this class has two main functions:
     *  1. connect the React Native native view interface with SFWebViewWidget
     *  2. implement event handling and propagating the events up to RN
    **/

    SFWebViewWidgetPolling widget;
    ReactContext context;

    public SFWebViewWidgetWrapper(ReactContext context) {
        super(context);
        this.context = context;
    }

    public void initialize(ReadableMap args) {
        post(() -> {
            addView(widget);
            requestLayout();
        });
        String widgetId = args.getString("widgetId");
        int widgetIndex = args.getInt("widgetIndex");
        String URL = args.getString("articleUrl");
        String installationKey = args.getString("partnerKey");
        boolean darkMode = args.getBoolean("darkMode");
        String extId = args.getString("extId");
        String extSecondaryId = args.getString("extSecondaryId");
        String pubImpId = args.getString("pubImpId");
        String RN_packageVersion = args.getString("packageVersion");
        Outbrain.register(context, installationKey);
        SFWebViewWidgetPolling.enableReactNativeMode(RN_packageVersion);
        SFWebViewWidgetListener clickListener = new SFWebViewWidgetListener() {
            @Override
            public void didChangeHeight(int newHeight) {
                WritableMap params = Arguments.createMap();
                params.putString("widgetId", widgetId);
                params.putInt("height", newHeight);
                emitEvent("didChangeHeight", params);
            }

            @Override
            public void onRecClick(String url) {
                WritableMap params = Arguments.createMap();
                params.putString("widgetId", widgetId);
                params.putString("url", url);
                emitEvent("onRecClick", params);
            }

            @Override
            public void onOrganicClick(String url) {
                WritableMap params = Arguments.createMap();
                params.putString("widgetId", widgetId);
                params.putString("url", url);
                emitEvent("onOrganicRecClick", params);
            }
        };
        SFWebViewEventsListener eventListener = (eventName, additionalData) -> {
            WritableMap params = Arguments.createMap();
            params.putString("widgetId", widgetId);
            params.putString("eventName", eventName);
            params.putMap("additionalData", Utils.convertJsonObjectToWritableMap(additionalData));
            emitEvent("onWidgetEvent", params);
        };
        widget = new SFWebViewWidgetPolling(context, URL, widgetId, widgetIndex, installationKey, clickListener, eventListener, darkMode, extId, extSecondaryId, pubImpId);
        widget.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    public void emitEvent(String widgetId, ReadableMap params) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(widgetId, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout the child view
        if (widget != null) {
            widget.layout(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (widget != null) {
            widget.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
