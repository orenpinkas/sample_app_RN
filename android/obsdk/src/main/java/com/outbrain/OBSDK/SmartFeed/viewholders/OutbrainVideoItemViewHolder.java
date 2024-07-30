package com.outbrain.OBSDK.SmartFeed.viewholders;


import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

public class OutbrainVideoItemViewHolder extends RecyclerView.ViewHolder{

    public final CardView cardView;
    public final WebView webView;
    public final RelativeLayout relativeLayout;
    public final ProgressBar progressBar;

    public OutbrainVideoItemViewHolder(View v) {
        super(v);
        this.cardView = v.findViewById(R.id.cv);
        this.webView = v.findViewById(R.id.webview);
        this.relativeLayout = v.findViewById(R.id.ob_sf_video_item);
        this.progressBar = v.findViewById(R.id.progressbar);
    }
}
