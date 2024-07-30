package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

/**
 * Created by odedre on 3/6/18.
 */

public class OutbrainCarouselContainerViewHolder extends RecyclerView.ViewHolder {
    public final DiscreteScrollView horizontalScroll;
    public final RelativeLayout widgetTitleRL;
    public final TextView widgetTitleTV;

    public OutbrainCarouselContainerViewHolder(View v) {
        super(v);
        horizontalScroll = v.findViewById(R.id.sfeed_horizontal_scroll_view);
        widgetTitleRL = v.findViewById(R.id.ob_title_relative_layout);
        widgetTitleTV = v.findViewById(R.id.ob_title_text_view);
    }
}