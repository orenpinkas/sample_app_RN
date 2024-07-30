package com.outbrain.OBSDK.SFWebView;

import org.json.JSONObject;

public interface SFWebViewEventsListener {
    /**
     *  @brief Called when the JS widget inside the SFWebViewWidget dispatch widget events (for example: rendered, error, viewability, etc).
     *      it should be implemented only if the publisher would like to manually keep track of widget events.
     *  @param eventName - the name of the event being dispatched
     *  @param additionalData - additional data that comes with the event. For example you'll find there: "widget ID", "widget index" and "timestamp".
     */
    void onWidgetEvent(String eventName, JSONObject additionalData);
}
