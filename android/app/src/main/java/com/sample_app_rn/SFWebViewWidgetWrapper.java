package com.sample_app_rn;

import android.graphics.Color;
import android.view.ViewGroup;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.outbrain.OBSDK.SFWebView.SFWebViewClickListenerFlutter;
import com.outbrain.OBSDK.SFWebView.SFWebViewEventsListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetFlutter;

public class SFWebViewWidgetWrapper extends ViewGroup {
    SFWebViewWidgetFlutter widget;
    ReactContext context;

    public SFWebViewWidgetWrapper(ReactContext context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.LTGRAY); // For debugging, remove in productio
    }

    public void initialize(String widgetId, int widgetIndex) {
        post(() -> {
            // Rest of your initialization code
            // ...

            addView(widget);
            requestLayout();
        });
        String URL = "https://mobile-demo.outbrain.com";
        String extId = "extId";
        String extSecondaryId = "extSecondaryId";
        String pubImpId = "pubImpId";
        boolean darkmode = false;
        String installationKey = "NANOWDGT01";
        SFWebViewClickListenerFlutter clickListener = new SFWebViewClickListenerFlutter() {
            @Override
            public void didChangeHeight(int newHeight) {
                WritableMap params = Arguments.createMap();
                params.putString("name", "didChangeHeight");
                params.putInt("height", newHeight);
                emitEvent(widgetId, params);
            }

            @Override
            public void onRecClick(String url) {
                WritableMap params = Arguments.createMap();
                params.putString("name", "onRecClick");
                params.putString("url", "");
                emitEvent(widgetId, params);
            }

            @Override
            public void onOrganicClick(String url) {

            }
        };
        SFWebViewEventsListener eventListener = (eventName, additionalData) -> {

        };
        widget = new SFWebViewWidgetFlutter(getContext(), URL, widgetId, widgetIndex, installationKey, clickListener, eventListener, darkmode, extId, extSecondaryId, pubImpId);
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
