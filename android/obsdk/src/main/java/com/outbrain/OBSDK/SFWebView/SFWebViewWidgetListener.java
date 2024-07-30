package com.outbrain.OBSDK.SFWebView;

public interface SFWebViewWidgetListener extends SFWebViewClickListener {

    void didChangeHeight(int newHeight);

    void onRecClick(String url);
}
