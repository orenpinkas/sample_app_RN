package com.sample_app_rn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.outbrain.OBSDK.SFWebView.SFWebViewClickListenerFlutter;
import com.outbrain.OBSDK.SFWebView.SFWebViewEventsListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidget;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetFlutter;

import org.json.JSONObject;

import java.util.Map;

public class SFWebViewWidgetManager extends ViewGroupManager<SFWebViewWidgetWrapper> {
    public static final String REACT_CLASS = "OutbrainWidget";
    public static final int COMMAND_CREATE = 1;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected SFWebViewWidgetWrapper createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        return new SFWebViewWidgetWrapper(themedReactContext);
    }

    @ReactProp(name = "widgetId")
    public void setWidgetId(SFWebViewWidgetWrapper widget, String widgetId) {
        widget.widgetId = widgetId;
    }

    @Override
    public void receiveCommand(@NonNull SFWebViewWidgetWrapper root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        int commandIdInt = Integer.parseInt(commandId);

        if (commandIdInt == COMMAND_CREATE) {
            assert args != null;
            String widgetId = args.getString(0);
            root.initialize(widgetId);
        }
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("create", COMMAND_CREATE);
    }

//    @Override
//    protected SFWebViewWidgetFlutter createViewInstance(ThemedReactContext reactContext) {
//        String widgetId = "";
//        String URL = "https://mobile-demo.outbrain.com";
//        int widgetIndex = 0;
//        String extId = "extId";
//        String extSecondaryId = "extSecondaryId";
//        String pubImpId = "pubImpId";
//        boolean darkmode = false;
//        String installationKey = "NANOWDGT01";
//        SFWebViewClickListenerFlutter clickListener = new SFWebViewClickListenerFlutter() {
//            @Override
//            public void didChangeHeight(int newHeight) {
//
//            }
//
//            @Override
//            public void onRecClick(String url) {
//
//            }
//
//            @Override
//            public void onOrganicClick(String url) {
//
//            }
//        };
//        SFWebViewEventsListener eventListener = (eventName, additionalData) -> {
//
//        };
//        SFWebViewWidgetFlutter view = new SFWebViewWidgetFlutter(reactContext, URL, widgetId, widgetIndex, installationKey, clickListener, eventListener, darkmode, extId, extSecondaryId, pubImpId);
//        return view;
//    }

}