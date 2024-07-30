package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OBSmartFeedService {

    private final ExecutorService newItemsQueueManager;
    private final WeakReference<OBSmartFeedServiceListener> smartFeedServiceListenerWeakReference;
    @SuppressWarnings("FieldCanBeLocal")
    private final String LOG_TAG = "OBSmartFeedService";

    public OBSmartFeedService(OBSmartFeedServiceListener OBSmartFeedServiceListener) {
        this.newItemsQueueManager = Executors.newSingleThreadExecutor();
        this.smartFeedServiceListenerWeakReference = new WeakReference<>(OBSmartFeedServiceListener);
    }

    public void addNewItemsToSmartFeedArray(Context applicationContext, OBRecommendationsResponse recommendations, boolean shouldUpdateUI, boolean isFirstBatch) {
        AddNewItemsToSmartFeedArrayHandler addNewItemsToSmartFeedArrayHandler = new AddNewItemsToSmartFeedArrayHandler(applicationContext, recommendations, this.smartFeedServiceListenerWeakReference, isFirstBatch, shouldUpdateUI);
        this.newItemsQueueManager.submit(addNewItemsToSmartFeedArrayHandler);
        Log.i(LOG_TAG, "add new items with rec mode: " + recommendations.getSettings().getRecMode());
    }
}
