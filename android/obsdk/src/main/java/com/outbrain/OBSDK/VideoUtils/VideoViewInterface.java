package com.outbrain.OBSDK.VideoUtils;

import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

public interface VideoViewInterface {

    WebView getWebView();

    View getWrapperView();

    FrameLayout getFrameLayout();
}
