package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;
import com.rd.PageIndicatorView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

/**
 * Created by Alon Shprung on 21/5/20.
 */

public class BrandedCarouselContainerViewHolder extends RecyclerView.ViewHolder {
    public final DiscreteScrollView horizontalScroll;
    public final PageIndicatorView pageIndicatorView;
    public final ImageView sourceImage;
    public final TextView titleTV;
    public final TextView sourceTV;

    public BrandedCarouselContainerViewHolder(View v) {
        super(v);
        horizontalScroll = v.findViewById(R.id.sfeed_new_horizontal_scroll_view);
        pageIndicatorView = v.findViewById(R.id.pageIndicatorView);
        sourceImage = v.findViewById(R.id.new_horizontal_image_view);
        titleTV = v.findViewById(R.id.new_horizontal_item_title);
        sourceTV = v.findViewById(R.id.new_horizontal_item_source);
    }
}