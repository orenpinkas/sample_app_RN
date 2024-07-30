package com.outbrain.OBSDK.SFWebView;

public interface SFWebViewHeightDelegate {
    default int bottomPaddingForWidget(String widgetId) {
        return 0;
    }
}
