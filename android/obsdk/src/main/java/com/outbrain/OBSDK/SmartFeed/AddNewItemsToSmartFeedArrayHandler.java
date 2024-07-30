package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.BRANDED_APP_INSTALL;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.GRID_THREE_ITEMS_IN_LINE;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.HORIZONTAL_CAROUSEL;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.IN_WIDGET_VIDEO_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.SINGLE_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.STRIP_THUMBNAIL_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.WEEKLY_UPDATE_ITEM;

@SuppressWarnings("unchecked")
public class AddNewItemsToSmartFeedArrayHandler implements Runnable {

    private final OBRecommendationsResponse recommendations;
    private final boolean shouldUpdateUI;
    private final WeakReference<OBSmartFeedServiceListener> smartFeedServiceListenerWeakReference;
    private final boolean isFirstBatch;

    @SuppressWarnings("FieldCanBeLocal")
    private final String LOG_TAG = "AddSFItemsHandler";

    public AddNewItemsToSmartFeedArrayHandler(Context applicationContext,
                                              OBRecommendationsResponse recommendations,
                                              WeakReference<OBSmartFeedServiceListener> smartFeedServiceListenerWeakReference,
                                              boolean isFirstBatch,
                                              boolean shouldUpdateUI) {
        this.recommendations = recommendations;
        this.smartFeedServiceListenerWeakReference = smartFeedServiceListenerWeakReference;
        this.shouldUpdateUI = shouldUpdateUI;
        this.isFirstBatch = isFirstBatch;
    }

    @Override
    public void run() {
        addNewItemsToSmartFeedArray();
    }

    private void addNewItemsToSmartFeedArray() {
        SFItemData.SFItemType itemType = getItemType(recommendations);

        switch (itemType) {
            case SINGLE_ITEM:
            case STRIP_THUMBNAIL_ITEM:
            case IN_WIDGET_VIDEO_ITEM:
            case BRANDED_APP_INSTALL:
                addNewSingleItemsToSmartFeedArray(recommendations, itemType, isFirstBatch);
                break;
            case GRID_TWO_ITEMS_IN_LINE:
            case GRID_THREE_ITEMS_IN_LINE:
            case GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO:
                addNewItemsInLineToSmartFeedArray(recommendations, itemType, isFirstBatch);
                break;
            case HORIZONTAL_CAROUSEL:
            case BRANDED_CAROUSEL_ITEM:
                addNewHorizontalItemToSmartFeedArray(recommendations, itemType, isFirstBatch);
                break;
            case WEEKLY_UPDATE_ITEM:
                if (isWeeklyHighlightsItemValid(recommendations)) {
                    addNewHorizontalItemToSmartFeedArray(recommendations, itemType, isFirstBatch);
                } else {
                    smartFeedServiceListenerWeakReference.get().notifyNoNewItemsToAdd();
                }
                break;
        }
    }

    private void addNewHorizontalItemToSmartFeedArray(OBRecommendationsResponse recommendations, SFItemData.SFItemType sfItemType, Boolean isFirstBatch) {
        String sfItemTitle = recommendations.getSettings().getWidgetHeaderText();
        if (recommendations.getSettings().isTrendingInCategoryCard() && recommendations.getAll().size() > 0) {
            OBRecommendation rec = recommendations.getAll().get(0);
            sfItemTitle = recommendations.getSettings().getWidgetHeaderText() + ' ' + rec.getCategoryName();
        }

        ArrayList<SFItemData> sfItemsList = new ArrayList<>();
        sfItemsList.add(new SFItemData(
                recommendations.getAll(),
                sfItemType,
                !isFirstBatch && !sfItemTitle.equals("") ? sfItemTitle : null,
                recommendations.getSettings(),
                recommendations.getRequest()
        ));


        notifyListener(sfItemsList);
    }

    private void notifyListener(final ArrayList<SFItemData> sfItemsList) {
        smartFeedServiceListenerWeakReference.get().notifyNewItems(sfItemsList, shouldUpdateUI);
    }

    private void addNewSingleItemsToSmartFeedArray(OBRecommendationsResponse recommendations,
                                                   SFItemData.SFItemType sfItemType,
                                                   boolean isFirstBatch) {
        String sfItemTitle = recommendations.getSettings().getWidgetHeaderText();
        boolean didCreateFirstItem = false;
        ArrayList<SFItemData> sfItemsList = new ArrayList<>();
        int widgetNumOfRecs = recommendations.getAll().size();

        for (OBRecommendation obRecommendation : recommendations.getAll()) {
            sfItemsList.add(new SFItemData(
                    obRecommendation,
                    sfItemType,
                    !isFirstBatch && !didCreateFirstItem && !sfItemTitle.equals("") ? sfItemTitle : null,
                    recommendations.getSettings(),
                    recommendations.getRequest(),
                    Integer.parseInt(obRecommendation.getPosition()) == (widgetNumOfRecs-1)));
            didCreateFirstItem = true;
        }
        notifyListener(sfItemsList);
    }

    private void addNewItemsInLineToSmartFeedArray(OBRecommendationsResponse recommendations,
                                                   SFItemData.SFItemType sfItemType,
                                                   Boolean isFirstBatch) {

        String sfItemTitle = recommendations.getSettings().getWidgetHeaderText();

        int numberOfRecs = recommendations.getAll().size();
        boolean isVideoItem = sfItemType == GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO;
        ArrayList<SFItemData> sfItemsList = new ArrayList<>();

        if (isVideoItem) {
            // If this is a video item and recommendations.getAll().count == 6,
            // we want to insert the video in the middle of the 3 items of 2 recs each.
            // Otherwise, the video item will be the first item.
            int videoItemIndex = numberOfRecs == 6 ? 1 : 0;
            addTwoItemsInLineWithVideoToNewItemsList(
                    sfItemsList,
                    recommendations,
                    sfItemTitle,
                    isFirstBatch,
                    videoItemIndex
            );
        } else {
            addItemsInLineToNewItemsList(
                    sfItemsList,
                    sfItemType,
                    recommendations,
                    sfItemTitle,
                    isFirstBatch
            );
        }
        notifyListener(sfItemsList);
    }

    private void addItemsInLineToNewItemsList(ArrayList<SFItemData> sfItemsList,
                                              SFItemData.SFItemType sfItemType,
                                              OBRecommendationsResponse recommendations,
                                              String sfItemTitle,
                                              Boolean isFirstBatch) {

        ArrayList<OBRecommendation> recList = new ArrayList<>();
        int numberOfItems = sfItemType == GRID_THREE_ITEMS_IN_LINE ? 3 : 2;
        boolean didCreateFirstRow = false;

        for (OBRecommendation obRecommendation : recommendations.getAll()) {
            recList.add(obRecommendation);

            // We want to create a new sfItem every 2/3 items we collect in recList
            if (recList.size() == numberOfItems) {
                String title = !isFirstBatch && !didCreateFirstRow && !sfItemTitle.equals("") ? sfItemTitle : null;
                sfItemsList.add(new SFItemData(
                        (ArrayList<OBRecommendation>) recList.clone(),
                        sfItemType,
                        title,
                        recommendations.getSettings(),
                        recommendations.getRequest()
                ));
                didCreateFirstRow = true;

                recList.clear();
            }
        }

    }

    private void addTwoItemsInLineWithVideoToNewItemsList(ArrayList<SFItemData> sfItemsList,
                                                          OBRecommendationsResponse recommendations,
                                                          String sfItemTitle,
                                                          Boolean isFirstBatch,
                                                          int videoItemIndex) {

        ArrayList<OBRecommendation> recList = new ArrayList<>();
        int itemsAddedCounter = 0;
        SFItemData.SFItemType currItemType;

        for (OBRecommendation obRecommendation : recommendations.getAll()) {
            recList.add(obRecommendation);
            currItemType = itemsAddedCounter == videoItemIndex ?
                    GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO :
                    GRID_TWO_ITEMS_IN_LINE;

            // We want to create a new sfItem every 2 items we collect in recList
            if (recList.size() == 2) {
                sfItemsList.add(new SFItemData(
                        (ArrayList<OBRecommendation>) recList.clone(),
                        currItemType,
                        !isFirstBatch && !sfItemTitle.equals("") ? sfItemTitle : null,
                        recommendations.getSettings(),
                        recommendations.getRequest()
                ));

                recList.clear();
                itemsAddedCounter++;
            }
        }
    }

    private SFItemData.SFItemType getItemType(OBRecommendationsResponse recommendations) {
        String recMode = recommendations.getSettings().getRecMode();
        boolean isVideoItem = recommendations.getRequest().isVideo();
        int numberOfRecs = recommendations.getAll().size();
        switch (recMode) {
            case "sdk_sfd_swipe":
                return HORIZONTAL_CAROUSEL;
            case "sdk_sfd_1_column":
                return isVideoItem && OBSmartFeed.isVideoEligible ? IN_WIDGET_VIDEO_ITEM : SINGLE_ITEM;
            case "sdk_sfd_2_columns":
                return isVideoItem && OBSmartFeed.isVideoEligible ? GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO : GRID_TWO_ITEMS_IN_LINE;
            case "sdk_sfd_3_columns":
                return GRID_THREE_ITEMS_IN_LINE;
            case "sdk_sfd_thumbnails":
                return STRIP_THUMBNAIL_ITEM;
            case "odb_dynamic_ad-carousel":
                return numberOfRecs == 1 ? BRANDED_APP_INSTALL : BRANDED_CAROUSEL_ITEM;
            case "odb_timeline":
                return WEEKLY_UPDATE_ITEM;
        }

        return SINGLE_ITEM;
    }

    boolean isWeeklyHighlightsItemValid(OBRecommendationsResponse recommendations) {
        ArrayList<OBRecommendation> recs = recommendations.getAll();

        if (recs.size() % 3 != 0) {
            Log.e(LOG_TAG, "Weekly highlights recommendations size is not multiplier of 3");
            return false;
        }
        Map<String, Integer> dateToCountOfRecsMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM EEE", Locale.US);

        for (OBRecommendation rec : recs) {
            String formatedDate = format.format(rec.getPublishDate());
            if (dateToCountOfRecsMap.containsKey(formatedDate)) {
                dateToCountOfRecsMap.put(formatedDate, dateToCountOfRecsMap.get(formatedDate) + 1);
            } else {
                dateToCountOfRecsMap.put(formatedDate, 1);
            }
        }

        for (int count : dateToCountOfRecsMap.values()) {
            if (count != 3) {
                Log.e(LOG_TAG, "Weekly highlights item - should be 3 recommendations for each date");
                return false;
            }
        }

        return true;
    }
}
