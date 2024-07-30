package com.outbrain.OBSDK.VideoWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.OBClickListener;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.SFItemData;
import com.outbrain.OBSDK.SmartFeed.SFSingleRecView;
import com.outbrain.OBSDK.SmartFeed.SFUtils;
import com.outbrain.OBSDK.VideoUtils.VideoUtils;

import java.lang.ref.WeakReference;

@SuppressWarnings("FieldCanBeLocal")
public class OBVideoWidget implements RecommendationsListener {

    private final String URL;
    private final String widgetID;
    private final WeakReference<OBClickListener> listenerReference;
    private OBVideoWidgetViewHolder obVideoWidgetViewHolder;
    private Context ctx;
    private final String LOG_TAG = "OBVideoWidget";
    private FrameLayout frameLayout;
    private View obVideoWidgetView;
    private OBRecommendationsResponse recommendations = null;

    public OBVideoWidget(String widgetId, String url, OBClickListener listener) {
        this.widgetID = widgetId;
        this.URL = url;
        this.listenerReference = new WeakReference<>(listener);
    }

    public void load(FrameLayout frameLayout){
        this.frameLayout = frameLayout;
        this.ctx = frameLayout.getContext();

        if (recommendations == null) {
            OBRequest request = new OBRequest();
            request.setUrl(URL);
            request.setWidgetId(widgetID);
            Outbrain.fetchRecommendations(request, this);
        } else {
            bindRecommendation();
        }
    }

    @Override
    public void onOutbrainRecommendationsFailure(Exception ex) {
        Log.e(LOG_TAG, "onOutbrainRecommendationsFailure: " + ex.getLocalizedMessage());
    }

    @Override
    public void onOutbrainRecommendationsSuccess(final OBRecommendationsResponse recommendations) {
        this.recommendations = recommendations;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View obVideoWidgetView = inflater.inflate(R.layout.outbrain_video_widget_single_item, frameLayout, false);
        frameLayout.addView(obVideoWidgetView);
        this.obVideoWidgetView = obVideoWidgetView;
        bindRecommendation();
    }

    private void bindRecommendation() {
        this.obVideoWidgetViewHolder = new OBVideoWidgetViewHolder(obVideoWidgetView);

        final OBRecommendation rec = recommendations.get(0);
        final String title = recommendations.getSettings().getWidgetHeaderText();

        final SFSingleRecView singleRecView = new SFSingleRecView(
                obVideoWidgetViewHolder.wrapperView,
                null,
                obVideoWidgetViewHolder.recImageView,
                obVideoWidgetViewHolder.disclosureImageView,
                obVideoWidgetViewHolder.recSourceTV,
                obVideoWidgetViewHolder.recTitleTV,
                obVideoWidgetViewHolder.logoImageView,
                obVideoWidgetViewHolder.paidLabelTV,
                null
        );

        final SFItemData sfItem = new SFItemData(
                rec,
                SFItemData.SFItemType.IN_WIDGET_VIDEO_ITEM,
                title,
                recommendations.getSettings(),
                recommendations.getRequest(),
                true
        );

        if (sfItem.getTitle() == null) {
            obVideoWidgetViewHolder.titleRL.setVisibility(View.GONE);
            return;
        } else {
            obVideoWidgetViewHolder.recTitleTV.setText(sfItem.getTitle());
            obVideoWidgetViewHolder.recommendedByLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerReference.get().userTappedOnAboutOutbrain();
                }
            });
            obVideoWidgetViewHolder.titleRL.setVisibility(View.VISIBLE);
        }

        SFUtils.onBindSingleRec(
                listenerReference.get(),
                singleRecView,
                rec,
                ctx,
                sfItem
        );

        VideoUtils.initVideo(obVideoWidgetViewHolder, listenerReference.get(), sfItem, URL, ctx);
    }
}
