package com.outbrain.OBSDK.SmartFeed.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.outbrain.OBSDK.R;


/**
 * Created by odedre on 3/6/18.
 */

public class OutbrainSingleItemViewHolder extends OutbarainVideoAbstractViewHolder {
    public final CardView cardView;
    public final ImageView recImageView;
    public final ImageView disclosureImageView;
    public final ImageView logoImageView;
    public final TextView recSourceTV;
    public final TextView recTitleTV;
    public final View layout;
    public final View seperatorLine;
    public final RelativeLayout widgetTitleRL;
    public final TextView widgetTitleTV;
    public final TextView paidLabelTV;

    public OutbrainSingleItemViewHolder(View v) {
        super(v);
        layout = v;
        cardView = v.findViewById(R.id.cv);
        recImageView = v.findViewById(R.id.ob_rec_image);
        disclosureImageView = v.findViewById(R.id.outbrain_rec_disclosure_image_view);
        recTitleTV = v.findViewById(R.id.ob_rec_title);
        recSourceTV = v.findViewById(R.id.ob_rec_source);
        widgetTitleTV = v.findViewById(R.id.ob_title_text_view);
        widgetTitleRL = v.findViewById(R.id.ob_title_relative_layout);
        logoImageView = v.findViewById(R.id.outbrain_rec_logo_image_view);
        frameLayout = v.findViewById(R.id.video_frame_layout);
        webView = v.findViewById(R.id.webview);
        wrapperView = cardView;
        paidLabelTV = v.findViewById(R.id.ob_paid_label);
        seperatorLine = v.findViewById(R.id.outbrain_strip_thumb_seperator_line);
    }
}
