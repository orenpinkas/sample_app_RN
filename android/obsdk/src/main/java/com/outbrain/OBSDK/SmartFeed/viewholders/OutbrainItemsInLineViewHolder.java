package com.outbrain.OBSDK.SmartFeed.viewholders;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.SFSingleRecView;


public class OutbrainItemsInLineViewHolder extends OutbarainVideoAbstractViewHolder {

    public final View layout;
    public final RelativeLayout widgetTitleRL;
    public final TextView widgetTitleTV;

    public final SFSingleRecView[] sfRecViews;

    public OutbrainItemsInLineViewHolder(View v, int numberOfRecs, int singleRecResourceID, float defaultMarginInGrid) {
        super(v);
        layout = v;
        sfRecViews = new SFSingleRecView[numberOfRecs];
        widgetTitleRL = v.findViewById(R.id.ob_title_relative_layout);
        widgetTitleTV = v.findViewById(R.id.ob_title_text_view);
        frameLayout = v.findViewById(R.id.video_frame_layout);
        webView = v.findViewById(R.id.webview);
        wrapperView = v.findViewById(R.id.ob_items_in_line_linear_layout);

        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        LinearLayout linearLayout = (LinearLayout) wrapperView;

        // calculate 4.4dp in px
        Resources r = v.getContext().getResources();
        int pxBetweenRecs = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                defaultMarginInGrid,
                r.getDisplayMetrics()
        );

        View recView;
        for (int i = 0; i < numberOfRecs; i++) {
            if (singleRecResourceID != 0) {
                recView = inflater.inflate(singleRecResourceID, linearLayout, false);
            } else {
                recView = inflater.inflate(R.layout.outbrain_sfeed_single_rec, linearLayout, false);
            }
            sfRecViews[i] = new SFSingleRecView(
                    recView,
                    (CardView) recView.findViewById(R.id.outbrain_item_wrapper),
                    (ImageView) recView.findViewById(R.id.ob_rec_image),
                    (ImageView) recView.findViewById(R.id.outbrain_rec_disclosure_image_view),
                    (TextView) recView.findViewById(R.id.ob_rec_source),
                    (TextView) recView.findViewById(R.id.ob_rec_title),
                    (ImageView) recView.findViewById(R.id.outbrain_rec_logo_image_view),
                    (TextView) recView.findViewById(R.id.ob_paid_label),
                    null);

            // set LayoutParams for the recommendation view
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            if (i > 0) {
                params.setMargins(pxBetweenRecs, 0, 0, 0);
            }
            recView.setLayoutParams(params);

            // add recommendation view to the linear layout
            linearLayout.addView(recView);
        }
    }
}
