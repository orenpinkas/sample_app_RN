package com.sample_app_rn;

import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.outbrain.OBSDK.SFWebView.SFWebViewClickListenerFlutter;
import com.outbrain.OBSDK.SFWebView.SFWebViewEventsListener;
import com.outbrain.OBSDK.SFWebView.SFWebViewWidgetFlutter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SFWebViewWidgetWrapper extends ViewGroup {
    SFWebViewWidgetFlutter widget;
    ReactContext context;

    public SFWebViewWidgetWrapper(ReactContext context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.LTGRAY); // For debugging, remove in productio
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
        String extId = args.getString("extId");
        String extSecondaryId = args.getString("extSecondaryId");
        String pubImpId = args.getString("pubImpId");
        boolean darkmode = false;
        SFWebViewClickListenerFlutter clickListener = new SFWebViewClickListenerFlutter() {
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
            params.putMap("additionalData", convertJsonObjectToWritableMap(additionalData));
            emitEvent("onWidgetEvent", params);
        };
        widget = new SFWebViewWidgetFlutter(getContext(), URL, widgetId, widgetIndex, installationKey, clickListener, eventListener, darkmode, extId, extSecondaryId, pubImpId);
        widget.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    private WritableMap convertJsonObjectToWritableMap(JSONObject jsonObject) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(jsonObject.toString(), new TypeToken<Map<String, Object>>(){}.getType());
        return convertMapToWritableMap(map);
    }
    private WritableMap convertMapToWritableMap(Map<String, Object> map) {
        WritableMap writableMap = Arguments.createMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                writableMap.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                writableMap.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                writableMap.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                writableMap.putString(key, (String) value);
            } else if (value instanceof Map) {
                writableMap.putMap(key, convertMapToWritableMap((Map<String, Object>) value));
            } else if (value instanceof List) {
                writableMap.putArray(key, convertListToWritableArray((List) value));
            } else if (value == null) {
                writableMap.putNull(key);
            }
        }
        return writableMap;
    }

    private WritableArray convertListToWritableArray(List list) {
        WritableArray writableArray = Arguments.createArray();
        for (Object item : list) {
            if (item instanceof Boolean) {
                writableArray.pushBoolean((Boolean) item);
            } else if (item instanceof Integer) {
                writableArray.pushInt((Integer) item);
            } else if (item instanceof Double) {
                writableArray.pushDouble((Double) item);
            } else if (item instanceof String) {
                writableArray.pushString((String) item);
            } else if (item instanceof Map) {
                writableArray.pushMap(convertMapToWritableMap((Map<String, Object>) item));
            } else if (item instanceof List<?>) {
                writableArray.pushArray(convertListToWritableArray((List) item));
            } else if (item == null) {
                writableArray.pushNull();
            }
        }
        return writableArray;
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
