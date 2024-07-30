package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.SFAutoScrollRecyclerView;

public class WeeklyHighlightsContainerViewHolder extends RecyclerView.ViewHolder {
    public final SFAutoScrollRecyclerView horizontalAutoScroll;
    public final RelativeLayout widgetTitleRL;
    public final TextView widgetTitleTV;

    public WeeklyHighlightsContainerViewHolder(View v) {
        super(v);
        horizontalAutoScroll = v.findViewById(R.id.sfeed_week_highlights_scroll_view);
        widgetTitleRL = v.findViewById(R.id.ob_title_relative_layout);
        widgetTitleTV = v.findViewById(R.id.ob_title_text_view);
    }
}