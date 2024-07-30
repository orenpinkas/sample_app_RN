package com.outbrain.OBSDK.SmartFeed;

import android.app.Activity;
import android.content.Context;
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
import com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders.DefaultHorizontalItemViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.horizontalViewHolders.BrandedCarouselItemViewHolder;
import com.outbrain.OBSDK.Viewability.OBCardView;
import com.outbrain.OBSDK.Viewability.SFViewabilityService;

import java.lang.ref.WeakReference;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class SFHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final WeakReference<OBClickListener> listenerReference;
    private final int customLayoutResourceID;
    private final SFItemData sfItemData;
    private final long SFinitializationTime;
    private final boolean isViewabilityPerListingEnabled;
    private final boolean displaySourceOnOrganicRec;

    SFHorizontalAdapter(
            OBClickListener listener,
            int customLayoutResourceID,
            SFItemData sfItem,
            long SFinitializationTime,
            boolean isViewabilityPerListingEnabled,
            boolean displaySourceOnOrganicRec) {
        this.listenerReference = new WeakReference<>(listener);
        this.customLayoutResourceID = customLayoutResourceID;
        this.sfItemData = sfItem;
        this.SFinitializationTime = SFinitializationTime;
        this.isViewabilityPerListingEnabled = isViewabilityPerListingEnabled;
        this.displaySourceOnOrganicRec = displaySourceOnOrganicRec;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        int resourceId;
        if (this.customLayoutResourceID != 0) {
            resourceId = this.customLayoutResourceID;
        } else if (sfItemData.itemType() == SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM) {
            resourceId = R.layout.outbrain_sfeed_branded_carousel_item;
        } else {
            resourceId = R.layout.outbrain_sfeed_carousel_item;
        }
        v = inflater.inflate(resourceId, parent, false);

        if (sfItemData.itemType() == SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM) {
            return new BrandedCarouselItemViewHolder(v);
        } else {
            return new DefaultHorizontalItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OBRecommendation rec = sfItemData.getOutbrainRecs().get(position);
        Context context;
        CardView cardView;
        SFSingleRecView sfSingleRecView;
        if (sfItemData.itemType() == SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM) {
            BrandedCarouselItemViewHolder newViewHolder = (BrandedCarouselItemViewHolder) holder;

            sfSingleRecView = new SFSingleRecView(
                    newViewHolder.layout,
                    newViewHolder.cardView,
                    newViewHolder.image,
                    newViewHolder.titleTextView
            );

            String ctaText = rec.getCtaText();

            // Check if we have real value for ctaText ("" is the default, empty value)
            if (!"".equals(ctaText)) {
                newViewHolder.ctaTextView.setVisibility(View.VISIBLE);
                newViewHolder.ctaTextView.setText(rec.getCtaText());
            } else { // No CTA text
                newViewHolder.ctaTextView.setVisibility(View.GONE);
            }

            cardView = newViewHolder.cardView;
            context = newViewHolder.layout.getContext();

            WindowManager manager = (WindowManager) context.getApplicationContext().getSystemService(Activity.WINDOW_SERVICE);
            if (manager != null) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(displaymetrics);
                cardView.getLayoutParams().width = ((int) Math.round(displaymetrics.widthPixels * 0.65));
            }

        } else {
            DefaultHorizontalItemViewHolder defaultViewHolder = (DefaultHorizontalItemViewHolder) holder;

            sfSingleRecView = new SFSingleRecView(
                    defaultViewHolder.layout,
                    defaultViewHolder.cardView,
                    defaultViewHolder.image,
                    null,
                    defaultViewHolder.sourceTextView,
                    defaultViewHolder.titleTextView,
                    defaultViewHolder.logoImage,
                    defaultViewHolder.paidLabelTV,
                    null
            );

            cardView = defaultViewHolder.cardView;
            context = defaultViewHolder.layout.getContext();
        }

        SFUtils.onBindSingleRec(
                listenerReference.get(),
                sfSingleRecView,
                rec,
                context,
                sfItemData
        );

        if (this.displaySourceOnOrganicRec && !sfItemData.isCustomUI()) {
            sfSingleRecView.recSourceTV.setVisibility(View.VISIBLE);
        }

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

    @Override
    public int getItemCount() {
        return sfItemData.getOutbrainRecs().size();
    }
}
