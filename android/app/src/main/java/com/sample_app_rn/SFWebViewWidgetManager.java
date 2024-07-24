package com.sample_app_rn;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.outbrain.OBSDK.SFWebView.SFWebViewClickListenerFlutter;
import com.outbrain.OBSDK.SFWebView.SFWebViewEventsListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidget;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetFlutter;

import org.json.JSONObject;

public class SFWebViewWidgetManager extends SimpleViewManager<SFWebViewWidgetFlutter> {
    public static final String REACT_CLASS = "OutbrainWidget";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected SFWebViewWidgetFlutter createViewInstance(ThemedReactContext reactContext) {
        String widgetId = "MB_2";
        String URL = "https://mobile-demo.outbrain.com";
        int widgetIndex = 0;
        String extId = "extId";
        String extSecondaryId = "extSecondaryId";
        String pubImpId = "pubImpId";
        boolean darkmode = false;
        String installationKey = "NANOWDGT01";
        SFWebViewClickListenerFlutter clickListener = new SFWebViewClickListenerFlutter() {
            @Override
            public void didChangeHeight(int newHeight) {

            }

            @Override
            public void onRecClick(String url) {

            }

            @Override
            public void onOrganicClick(String url) {

            }
        };
        SFWebViewEventsListener eventListener = (eventName, additionalData) -> {

        };
        SFWebViewWidgetFlutter view = new SFWebViewWidgetFlutter(reactContext, URL, widgetId, widgetIndex, installationKey, clickListener, eventListener, darkmode, extId, extSecondaryId, pubImpId);
        return view;
    }

}