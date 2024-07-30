package com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;

public class WeeklyHighlightslItemViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout layout;

    public final TextView publishDateText;

    public final ImageView firstRecImage;
    public final TextView firstRecTitleTextView;
    public final CardView firstRecCardView;

    public final ImageView secondRecImage;
    public final TextView secondRecTitleTextView;
    public final CardView secondRecCardView;

    public final ImageView thirdRecImage;
    public final TextView thirdRecTitleTextView;
    public final CardView thirdRecCardView;

    public WeeklyHighlightslItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.ob_weekly_highlights_item_layout);

        publishDateText = itemView.findViewById(R.id.ob_publish_date_text);

        // first rec
        firstRecImage = itemView.findViewById(R.id.ob_rec_one_image);
        firstRecTitleTextView = itemView.findViewById(R.id.ob_rec_one_text);
        firstRecCardView = itemView.findViewById(R.id.ob_rec_one_card_view);

        // second rec
        secondRecImage = itemView.findViewById(R.id.ob_rec_two_image);
        secondRecTitleTextView = itemView.findViewById(R.id.ob_rec_two_text);
        secondRecCardView = itemView.findViewById(R.id.ob_rec_two_card_view);

        // third rec
        thirdRecImage = itemView.findViewById(R.id.ob_rec_three_image);
        thirdRecTitleTextView = itemView.findViewById(R.id.ob_rec_three_text);
        thirdRecCardView = itemView.findViewById(R.id.ob_rec_three_card_view);
    }
}
