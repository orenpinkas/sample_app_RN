package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.VideoUtils.VideoViewInterface;

public abstract class OutbarainVideoAbstractViewHolder extends RecyclerView.ViewHolder implements VideoViewInterface {
    public View wrapperView;
    public FrameLayout frameLayout;
    public WebView webView;

    OutbarainVideoAbstractViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public WebView getWebView() {
        return this.webView;
    }

    @Override
    public View getWrapperView() {
        return this.wrapperView;
    }

    @Override
    public FrameLayout getFrameLayout() {
        return this.frameLayout;
    }
}
