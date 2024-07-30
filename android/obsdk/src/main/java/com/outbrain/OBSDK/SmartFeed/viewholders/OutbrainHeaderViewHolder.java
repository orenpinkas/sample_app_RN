package com.outbrain.OBSDK.SmartFeed.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

public class OutbrainHeaderViewHolder extends RecyclerView.ViewHolder {
    public final ImageButton outbrainLogoButton;
    public final TextView textView;

    public OutbrainHeaderViewHolder(View v) {
        super(v);
        this.outbrainLogoButton = v.findViewById(R.id.outbrain_logo_button);
        this.textView = v.findViewById(R.id.outbrain_sponsored_textview);
    }
}
