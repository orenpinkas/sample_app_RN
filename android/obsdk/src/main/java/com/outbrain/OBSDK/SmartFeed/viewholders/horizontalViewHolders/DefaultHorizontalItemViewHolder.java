package com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders;


import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

public class DefaultHorizontalItemViewHolder extends RecyclerView.ViewHolder {
    public final RelativeLayout layout;
    public final ImageView image;
    public final TextView titleTextView;
    public final ImageView logoImage;
    public final TextView sourceTextView;
    public final TextView paidLabelTV;
    public final CardView cardView;

    public DefaultHorizontalItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.ob_horizontal_item_layout);
        cardView = itemView.findViewById(R.id.ob_sf_horizontal_item);
        image = itemView.findViewById(R.id.ob_horizontal_item_image);
        titleTextView = itemView.findViewById(R.id.ob_horizontal_item_title);
        logoImage = itemView.findViewById(R.id.outbrain_rec_logo_image_view);
        sourceTextView = itemView.findViewById(R.id.ob_rec_source);
        paidLabelTV = itemView.findViewById(R.id.ob_paid_label);
    }
}
