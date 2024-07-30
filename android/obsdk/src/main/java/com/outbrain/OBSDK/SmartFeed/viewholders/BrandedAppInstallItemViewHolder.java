package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

/**
 * Created by Alon Shprung on 21/5/20.
 */

public class BrandedAppInstallItemViewHolder extends RecyclerView.ViewHolder {
    // header
    public final ImageView sourceImage;
    public final TextView sourceTV;

    // item
    public final LinearLayout layout;
    public final ImageView image;
    public final TextView titleTextView;
    public final CardView cardView;
    public final TextView ctaTextView;

    public BrandedAppInstallItemViewHolder(View v) {
        super(v);
        sourceImage = v.findViewById(R.id.app_install_item_source_image_view);
        sourceTV = v.findViewById(R.id.app_install_item_source);
        layout = v.findViewById(R.id.app_install_header_layout);
        titleTextView = v.findViewById(R.id.app_install_item_title);
        image = v.findViewById(R.id.ob_app_install_item_image);
        cardView = v.findViewById(R.id.ob_sf_app_install_item_card_view);
        ctaTextView = v.findViewById(R.id.ob_app_install_item_cta_text);
    }
}