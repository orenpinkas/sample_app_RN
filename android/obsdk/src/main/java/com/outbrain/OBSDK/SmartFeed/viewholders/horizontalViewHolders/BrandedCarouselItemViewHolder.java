package com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders;


import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

public class BrandedCarouselItemViewHolder extends RecyclerView.ViewHolder {
    public final RelativeLayout layout;
    public final ImageView image;
    public final TextView titleTextView;
    public final CardView cardView;
    public final TextView ctaTextView;

    public BrandedCarouselItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.ob_new_horizontal_item_layout);
        cardView = itemView.findViewById(R.id.ob_sf_new_horizontal_item);
        image = itemView.findViewById(R.id.ob_new_horizontal_item_image);
        titleTextView = itemView.findViewById(R.id.ob_new_horizontal_item_title);
        ctaTextView = itemView.findViewById(R.id.ob_new_horizontal_item_cta_text);
    }
}
