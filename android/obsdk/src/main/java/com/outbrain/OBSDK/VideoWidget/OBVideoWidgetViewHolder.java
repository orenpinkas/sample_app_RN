package com.outbrain.OBSDK.VideoWidget;

import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.VideoUtils.VideoViewInterface;

public class OBVideoWidgetViewHolder implements VideoViewInterface {
    public final ImageView recImageView;
    public final ImageView disclosureImageView;
    public final ImageView logoImageView;
    public final TextView recSourceTV;
    public final TextView recTitleTV;
    public final View layout;
    public final TextView itemTitle;
    public final LinearLayout recommendedByLL;
    public final RelativeLayout titleRL;
    public final View wrapperView;
    public final FrameLayout frameLayout;
    public final WebView webView;
    public final TextView paidLabelTV;

    OBVideoWidgetViewHolder(View obVideoWidgetView) {
        layout = obVideoWidgetView;
        recImageView = obVideoWidgetView.findViewById(R.id.ob_rec_image);
        disclosureImageView = obVideoWidgetView.findViewById(R.id.outbrain_rec_disclosure_image_view);
        recTitleTV = obVideoWidgetView.findViewById(R.id.ob_rec_title);
        recSourceTV = obVideoWidgetView.findViewById(R.id.ob_rec_source);
        itemTitle = obVideoWidgetView.findViewById(R.id.ob_title_text_view);
        recommendedByLL = obVideoWidgetView.findViewById(R.id.ob_recommended_by_linear_layout);
        titleRL = obVideoWidgetView.findViewById(R.id.ob_title_relative_layout);
        logoImageView = obVideoWidgetView.findViewById(R.id.outbrain_rec_logo_image_view);
        frameLayout = obVideoWidgetView.findViewById(R.id.video_frame_layout);
        webView = obVideoWidgetView.findViewById(R.id.webview);
        wrapperView = obVideoWidgetView.findViewById(R.id.cv);
        paidLabelTV = obVideoWidgetView.findViewById(R.id.ob_paid_label);
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
