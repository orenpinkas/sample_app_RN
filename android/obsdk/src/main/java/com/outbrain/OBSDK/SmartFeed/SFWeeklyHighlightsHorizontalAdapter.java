package com.outbrain.OBSDK.SmartFeed;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.OBClickListener;
import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.Theme.SFThemeImpl;
import com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders.WeeklyHighlightslItemViewHolder;
import com.outbrain.OBSDK.Viewability.OBCardView;
import com.outbrain.OBSDK.Viewability.SFViewabilityService;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SFWeeklyHighlightsHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OBRecommendation> recommendations;
    private final WeakReference<OBClickListener> listenerReference;
    private final SFItemData sfItemData;
    private final long SFinitializationTime;
    private final boolean isViewabilityPerListingEnabled;

    private static final int TWO_SMALL_RECS_ON_TOP_ITEM_TYPE = 0;
    private static final int TWO_SMALL_RECS_ON_BOTTOM_ITEM_TYPE = 1;

    SFWeeklyHighlightsHorizontalAdapter(
            OBClickListener listener,
            SFItemData sfItem,
            long SFinitializationTime,
            boolean isViewabilityPerListingEnabled) {
        this.listenerReference = new WeakReference<>(listener);
        this.sfItemData = sfItem;
        this.SFinitializationTime = SFinitializationTime;
        this.isViewabilityPerListingEnabled = isViewabilityPerListingEnabled;

        final ArrayList<OBRecommendation> recs = sfItem.getOutbrainRecs();

        // sort by date - highest first
        Collections.sort(recs, new Comparator<OBRecommendation>() {
            public int compare(OBRecommendation o1, OBRecommendation o2) {
                return o2.getPublishDate().compareTo(o1.getPublishDate());
            }
        });

        this.recommendations = recs;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TWO_SMALL_RECS_ON_TOP_ITEM_TYPE;
        } else {
            return TWO_SMALL_RECS_ON_BOTTOM_ITEM_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        int resourceId;
        if (viewType == TWO_SMALL_RECS_ON_BOTTOM_ITEM_TYPE) {
            resourceId = R.layout.outbrain_sfeed_week_highlights_item_one;
        } else {
            resourceId = R.layout.outbrain_sfeed_week_highlights_item_two;
        }

        v = inflater.inflate(resourceId, parent, false);

        return new WeeklyHighlightslItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int firstPositionOfRec =  (position % (recommendations.size() / 3)) * 3;
        final OBRecommendation[] recs = {
                recommendations.get(firstPositionOfRec),
                recommendations.get(firstPositionOfRec + 1),
                recommendations.get(firstPositionOfRec + 2)
        };

        Date publishDate = recs[0].getPublishDate();

        WeeklyHighlightslItemViewHolder viewHolder = (WeeklyHighlightslItemViewHolder) holder;

        viewHolder.layout.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM EEE", Locale.US);
        viewHolder.publishDateText.setText(format.format(publishDate));

        SFSingleRecView[] sfSingleRecViews = {
                new SFSingleRecView(viewHolder.firstRecCardView, viewHolder.firstRecImage, viewHolder.firstRecTitleTextView),
                new SFSingleRecView(viewHolder.secondRecCardView, viewHolder.secondRecImage, viewHolder.secondRecTitleTextView),
                new SFSingleRecView(viewHolder.thirdRecCardView, viewHolder.thirdRecImage, viewHolder.thirdRecTitleTextView)
        };

        CardView[] cardViews = {
                viewHolder.firstRecCardView,
                viewHolder.secondRecCardView,
                viewHolder.thirdRecCardView
        };

        WindowManager manager = (WindowManager) viewHolder.layout.getContext().getApplicationContext().getSystemService(Activity.WINDOW_SERVICE);
        if (manager != null) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(displaymetrics);
            viewHolder.layout.getLayoutParams().width = ((int) Math.round(displaymetrics.widthPixels * 0.7));
        }

        for (int i = 0; i < 3; i++) {
            OBRecommendation rec = recs[i];
            SFSingleRecView sfSingleRecView = sfSingleRecViews[i];
            CardView cardView = cardViews[i];

            SFUtils.onBindSingleRec(
                    listenerReference.get(),
                    sfSingleRecView,
                    rec,
                    viewHolder.layout.getContext(),
                    sfItemData
            );

            // Viewability per listing
            if (this.isViewabilityPerListingEnabled && cardView instanceof OBCardView) {
                SFViewabilityService.registerOBCardView(
                        (OBCardView) cardView,
                        sfItemData.getResponseRequest().getReqId(),
                        rec.getPosition(),
                        this.SFinitializationTime
                );
            }
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
