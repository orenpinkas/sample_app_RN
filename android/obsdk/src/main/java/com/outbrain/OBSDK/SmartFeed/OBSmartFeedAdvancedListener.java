package com.outbrain.OBSDK.SmartFeed;

import com.outbrain.OBSDK.Entities.OBRecommendation;

import java.util.ArrayList;

public interface OBSmartFeedAdvancedListener {

    /**
     * This method help the app developer to be notified on when new recommendations are being returned by Outbrain server
     * @param recommendations array of recommendations currently returned from Outbrain server.
     * @param widgetId The widget id of the corresponding request (may be one of the sub-widgets for example.
     */
    void onOutbrainRecsReceived(ArrayList<OBRecommendation> recommendations, String widgetId);

    /**
     * If the Smartfeed attemps to play a video within the feed, it may check with the app if there is a video currently playing in the app.
     * The goal is to avoid a collision of 2 videos playing at the same time.
     * @return true if video is currently playing in the app
     */
    boolean isVideoCurrentlyPlaying();

    /**
     * This method is relevant only in case Smartfeed is displayed in the middle of the feed (not at the end)
     * The OBSmartFeed will notify the app developer when the Smartfeed is ready with recs to be displayed so the
     * RecycleView adapter can integrate Outbrain recs within the feed.
     *
     */
    void smartfeedIsReadyWithRecs();
}
