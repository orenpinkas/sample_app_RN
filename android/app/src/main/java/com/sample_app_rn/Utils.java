package com.sample_app_rn;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class Utils {
    public static WritableMap convertJsonObjectToWritableMap(JSONObject jsonObject) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(jsonObject.toString(), new TypeToken<Map<String, Object>>(){}.getType());
        return convertMapToWritableMap(map);
    }
    private static WritableMap convertMapToWritableMap(Map<String, Object> map) {
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

    private static WritableArray convertListToWritableArray(List list) {
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
}
