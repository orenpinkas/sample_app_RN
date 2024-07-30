package com.outbrain.OBSDK.SmartFeed.viewholders;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.outbrain.OBSDK.R;

public class OutbrainReadMoreItemViewHolder extends RecyclerView.ViewHolder {
    public final View readMoreView;

    public OutbrainReadMoreItemViewHolder(View v) {
        super(v);
        this.readMoreView = v.findViewById(R.id.read_more_button);
    }
}
