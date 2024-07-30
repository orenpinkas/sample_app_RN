package com.outbrain.OBSDK.VideoUtils;

import android.graphics.Bitmap;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.outbrain.OBSDK.OBClickListener;

public class VideoWebChromeClient extends WebChromeClient {
    private long lastClickUnixTime = 0;
    private final OBClickListener listenerReference;

    public VideoWebChromeClient(OBClickListener listenerReference) {
        this.listenerReference = listenerReference;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {

        WebView targetWebView = new WebView(view.getContext()); // pass a context
        targetWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                if ((System.currentTimeMillis() / 1000L) - lastClickUnixTime > 2) {
                    lastClickUnixTime = System.currentTimeMillis() / 1000L;
                    listenerReference.userTappedOnVideo(url);
                }

                super.onPageStarted(view, url, favicon);
            }
        });
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(targetWebView);
        resultMsg.sendToTarget();
        return true;
    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        return Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
    }
}
