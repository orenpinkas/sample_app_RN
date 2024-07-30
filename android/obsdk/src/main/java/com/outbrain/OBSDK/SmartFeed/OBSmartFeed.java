package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.outbrain.OBSDK.Entities.OBBrandedItemSettings;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.FetchRecommendations.MultivacListener;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.OutbrainService;
import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.Theme.SFTheme;
import com.outbrain.OBSDK.SmartFeed.Theme.SFThemeImpl;
import com.outbrain.OBSDK.SmartFeed.viewholders.BrandedAppInstallItemViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbarainVideoAbstractViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainHeaderViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainCarouselContainerViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainItemsInLineViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.BrandedCarouselContainerViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainReadMoreItemViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainSingleItemViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainVideoItemViewHolder;
import com.outbrain.OBSDK.SmartFeed.viewholders.WeeklyHighlightsContainerViewHolder;
import com.outbrain.OBSDK.VideoUtils.VideoUtils;
import com.outbrain.OBSDK.Viewability.OBCardView;
import com.outbrain.OBSDK.Viewability.SFViewabilityService;
import com.outbrain.OBSDK.Viewability.ViewabilityService;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.outbrain.OBSDK.OBUtils.runOnMainThread;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.IN_WIDGET_VIDEO_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.SF_BAD_TYPE;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.SF_HEADER;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.SF_READ_MORE_ITEM;
import static com.outbrain.OBSDK.SmartFeed.SFItemData.SFItemType.SINGLE_ITEM;


/**
 * Created by odedre on 1/30/18.
 */

public class OBSmartFeed implements RecommendationsListener, OBSmartFeedServiceListener, MultivacListener {

    public static class PauseVideoEvent { }
    public static class OnWebViewDetachedFromWindowEvent { }

    private static final int SF_READ_MORE_ITEM_VIEW_TYPE = 2999;
    private static final int SF_HEADER_VIEW_TYPE = 3000;
    private static final int SF_SINGLE_ITEM_VIEW_TYPE = 3001;
    private static final int SF_HORIZONTAL_CAROUSEL_VIEW_TYPE = 3002;
    private static final int SF_TWO_ITEMS_IN_LINE_VIEW_TYPE = 3003;
    private static final int SF_THREE_ITEMS_IN_LINE_VIEW_TYPE = 3004;
    private static final int SF_STRIP_THUMBNAIL_TYPE = 3005;
    private static final int SF_VIDEO_ITEM_TYPE = 3006;
    private static final int SF_IN_WIDGET_VIDEO_ITEM_TYPE = 3007;
    private static final int SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE = 3008;
    private static final int SF_DYNAMIC_REC_CAROUSEL_ITEM_VIEW_TYPE = 3009;
    private static final int SF_DYNAMIC_REC_APP_INSTALL_ITEM_VIEW_TYPE = 3010;
    private static final int SF_WEEKLY_HIGHLIGHTS_ITEM_VIEW_TYPE = 3011;

    private int currentViewTypeIdx = 3012; //IMPORTANT = this should contain the highest int (+1) for <SF_TYPE_NAME>_VIEW_TYPE
    private int widgetIndex = 0;
    private String URL;
    private String mainWidgetID;
    private boolean hasMore;
    private boolean receivedMultivacSuccessOnce;
    private int lastCardIdx = 0; // lastCardIdx is the index of the last “child widget” inside the smartfeed
    private int lastIdx = 0; // lastIdx is the index of the last widget on the page (because we can load widgets async)
    private boolean isRTL;
    private String externalID;
    private String pubImpId;

    private final WeakReference<OBSmartFeedListener> listenerReference;
    private WeakReference<OBSmartFeedAdvancedListener> advancedListenerReference;
    private WeakReference<RecyclerView> recyclerViewReference;
    private RecyclerView.OnScrollListener onScrollListener;
    private boolean isLoading = false;
    private final ArrayList<SFItemData> sfItems = new ArrayList<>();
    private final String LOG_TAG = "OBSmartFeed";
    private String feedHeaderTitle;
    private int smartfeedHeaderFontSize = 0;
    private String feedAbTestVal;
    private Map<String,Integer> mapCustomUIWidgetIDToResourceID;
    private Map<SFItemData.SFItemType, Integer> mapCustomUIItemTypeToResourceID;
    private Map<String,Integer> mapCustomUIKeyToCustomViewType;
    private SparseIntArray mapCustomUIViewTypeToResourceID;
    private SparseIntArray mapCustomViewTypeToBaseViewType;
    private int customUIForHeaderResourceID = 0;
    private OBSmartFeedService OBSmartFeedService;
    private boolean isSmartfeedWithNoChildren = false;
    private long initializationTime;
    private boolean isViewabilityPerListingEnabled;
    public static final boolean isVideoEligible = true;
    public boolean isInMiddleOfRecycleView;
    public final float defaultMarginInGrid = 4.4f;
    private boolean displaySourceOnOrganicRec;
    private boolean hasWeeklyHighlightsItem = false;

    // Read more module
    private SFReadMoreModuleHelper readMoreModuleHelper;
    private boolean isReadMoreModuleEnabled = false;
    private int customUIForReadMoreViewResourceID = 0;


    public OBSmartFeed(String URL, String widgetID, RecyclerView recyclerView, OBSmartFeedListener listener) {
        commonInit(URL, widgetID, recyclerView);
        this.listenerReference = new WeakReference<>(listener);
    }

    public OBSmartFeed(String URL, String widgetID, RecyclerView recyclerView) {
        commonInit(URL, widgetID, recyclerView);
        OBSmartFeedDefaultListener defaultListener = new OBSmartFeedDefaultListener(
                recyclerView.getContext().getApplicationContext()
        );
        this.listenerReference = new WeakReference<>((OBSmartFeedListener) defaultListener);
    }

    private void commonInit(String URL, String widgetID, RecyclerView recyclerView) {
        this.URL = URL;
        this.mainWidgetID = widgetID;
        this.recyclerViewReference = new WeakReference<>(recyclerView);
        this.mapCustomUIViewTypeToResourceID = new SparseIntArray();
        this.mapCustomViewTypeToBaseViewType = new SparseIntArray();
        this.mapCustomUIWidgetIDToResourceID = new HashMap<>();
        this.mapCustomUIItemTypeToResourceID = new HashMap<>();
        this.mapCustomUIKeyToCustomViewType = new HashMap<>();
        this.OBSmartFeedService = new OBSmartFeedService(this);
        this.initializationTime = System.currentTimeMillis();
        SFUtils.resetImageLoadedMap();
    }

    public void setWidgetIndex(int widgetIndex) {
        this.widgetIndex = widgetIndex;
        this.lastIdx = widgetIndex;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerViewReference = new WeakReference<>(recyclerView);
    }

    public void setAdvancedListener(OBSmartFeedAdvancedListener advancedListener) {
        this.advancedListenerReference = new WeakReference<>(advancedListener);
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public void setPubImpId(String pubImpId) {
        this.pubImpId = pubImpId;
    }

    public void setDisplaySourceOnOrganicRec(boolean displaySourceOnOrganicRec) {
        this.displaySourceOnOrganicRec = displaySourceOnOrganicRec;
        SFUtils.displaySourceOnOrganicRec = displaySourceOnOrganicRec;
    }

    public void setDarkMode(boolean darkMode) {
        int theme = darkMode ? SFThemeImpl.DARK : SFThemeImpl.REGULAR;
        SFThemeImpl.getInstance().setThemeMode(theme);
    }

    public void setTheme(SFTheme sfTheme) {
        SFThemeImpl.getInstance().setTheme(sfTheme);
    }

    public void setReadMoreModule(int firstCollapsedItemPosition) {
        setReadMoreModule(firstCollapsedItemPosition, -1);
    }

    public void setReadMoreModule(int firstCollapsedItemPosition, int fistCollapsedItemBottomOffsetPx) {
        readMoreModuleHelper = new SFReadMoreModuleHelper(recyclerViewReference);
        readMoreModuleHelper.setPublisherStartItemPosition(firstCollapsedItemPosition);
        if (fistCollapsedItemBottomOffsetPx != -1) {
            readMoreModuleHelper.setPublisherStartItemBottomOffsetPx(fistCollapsedItemBottomOffsetPx);
        }
        isReadMoreModuleEnabled = true;
    }

    public void setReadMoreModuleGradientViewHeightPx(int height) {
        if (readMoreModuleHelper != null) {
            readMoreModuleHelper.setGradientViewHeight(height);
        }
    }

    public void addCustomUI(String widgetID, int resourceID) {
        this.mapCustomUIWidgetIDToResourceID.put(widgetID, resourceID);
    }

    public void addCustomUI(SFItemData.SFItemType itemType, int resourceID) {
        if (itemType == SF_HEADER) {
            addHeaderCustomUI(resourceID);
        } else if (itemType == SF_READ_MORE_ITEM) {
            addReadMoreViewCustomUI(resourceID);
        } else if (itemType == SF_BAD_TYPE) {
            Log.e(LOG_TAG, "Error - can not add custom UI for item type SF_BAD_TYPE");
        } else {
            this.mapCustomUIItemTypeToResourceID.put(itemType, resourceID);
        }
    }

    public void pauseVideo() {
        EventBus.getDefault().post(new PauseVideoEvent());
    }

    private void addHeaderCustomUI(int resourceID){
        customUIForHeaderResourceID = resourceID;
    }

    private void addReadMoreViewCustomUI(int resourceID) {
        customUIForReadMoreViewResourceID = resourceID;
    }

    private int getViewTypeOfCustomUI(int resourceID, int baseViewType) throws Exception {
        if (isCustomUIValid(resourceID, baseViewType)) {
            int viewTypeIdx = currentViewTypeIdx++;
            this.mapCustomViewTypeToBaseViewType.put(viewTypeIdx, baseViewType);
            this.mapCustomUIViewTypeToResourceID.put(viewTypeIdx, resourceID);
            return viewTypeIdx;
        } else {
            String widgetID = getWidgetIDOfResourceID(resourceID);
            throw new Exception("Custom UI for widgetID: " + widgetID + " is invalid. " +
                    "It looks like the xml layout doesn't match the widgetID");
        }
    }

    private String getWidgetIDOfResourceID(int resourceID) {
        String widgetID = "";
        for (String key : mapCustomUIWidgetIDToResourceID.keySet()) {
            if (resourceID == (mapCustomUIWidgetIDToResourceID.get(key))) {
                widgetID = key;
            }
        }
        return widgetID;
    }

    private Boolean isCustomUIValid(int resourceID, int baseViewType) {
        LayoutInflater inflater = LayoutInflater.from(this.recyclerViewReference.get().getContext());
        View v = inflater.inflate(resourceID, this.recyclerViewReference.get(), false);
        switch (baseViewType) {
            case SF_SINGLE_ITEM_VIEW_TYPE:
                if (v.findViewById(R.id.ob_sf_single_item) != null) {
                    return true;
                }
                break;
            case SF_HORIZONTAL_CAROUSEL_VIEW_TYPE:
                if (v.findViewById(R.id.ob_sf_horizontal_item) != null) {
                    return true;
                }
                break;
            case SF_STRIP_THUMBNAIL_TYPE:
                if (v.findViewById(R.id.ob_sf_strip_thumbnail_item) != null) {
                    return true;
                }
                break;
            case SF_TWO_ITEMS_IN_LINE_VIEW_TYPE:
            case SF_THREE_ITEMS_IN_LINE_VIEW_TYPE:
            case SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE:
                if (v.findViewById(R.id.outbrain_item_wrapper) != null) {
                    return true;
                }
                break;
            case SF_READ_MORE_ITEM_VIEW_TYPE:
                if (v.findViewById(R.id.read_more_button) != null) {
                    return true;
                }
        }
        return false;
    }

    public void start() {
        final RecyclerView recyclerView = this.recyclerViewReference.get();
        if (recyclerView == null) {
            Log.e(LOG_TAG, "start() - recyclerView is null");
            return;
        }

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        this.onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                recyclerView.getLayoutManager();

                int pastVisiblesItems, visibleItemCount, totalItemCount;

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = (layoutManager).findFirstVisibleItemPosition();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= (totalItemCount - 2)) {
                            isLoading = true;
                            //Do pagination.. i.e. fetch new data
                            fetchMoreRecommendations();
                        }
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(this.onScrollListener);
        isLoading = true;
        fetchMoreRecommendations();
    }

    public void fetchMoreRecommendations() {
        if (this.isSmartfeedWithNoChildren) {
            return;
        }

        if (this.sfItems.size() == 0) { // first load, main widget
            OBRequest request = createParentRequest(mainWidgetID);
            Outbrain.fetchRecommendations(request, this);
        } else { // load 1 chunk of the sub-widgets (feed content) list
            if (this.receivedMultivacSuccessOnce && !this.hasMore) {
                Log.e(LOG_TAG, "fetchMoreRecommendations was called but hasMore is false");
                return;
            }
            fetchMoreRecommendationsWithMultivac();
        }
    }

    private void fetchMoreRecommendationsWithMultivac() {
        OBRequest request = createMultivacRequest(mainWidgetID);
        OutbrainService.getInstance().fetchMultivac(request, this);
    }

    private OBRequest createParentRequest(String widgetID) {
        OBRequest request = new OBRequest();
        request.setUrl(URL);
        request.setWidgetId(widgetID);
        request.setWidgetIndex(widgetIndex);
        if (this.externalID != null) {
            request.setExternalID(this.externalID);
        }
        if (this.pubImpId != null) {
            request.setPubImpId(this.pubImpId);
        }
        return request;
    }

    private OBRequest createMultivacRequest(String widgetID) {
        OBRequest request = new OBRequest();
        request.setUrl(URL);
        request.setWidgetId(widgetID);
        request.setWidgetIndex(widgetIndex);
        request.setMultivac(true);
        request.setLastCardIdx(this.lastCardIdx);
        request.setLastIdx(this.lastIdx);
        request.setFab(this.feedAbTestVal);

        if (this.externalID != null) {
            request.setExternalID(this.externalID);
        }
        if (this.pubImpId != null) {
            request.setPubImpId(this.pubImpId);
        }
        return request;
    }

    ////////////////////////////////////////
    // RecommendationsListener implementation
    ////////////////////////////////////////

    @Override
    public void onOutbrainRecommendationsFailure(Exception ex) {
        isLoading = false;
        Log.e(LOG_TAG, "onOutbrainRecommendationsFailure: " + ex.getLocalizedMessage());
        RecyclerView recyclerView = this.recyclerViewReference.get();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(this.onScrollListener);
        }
    }

    @Override
    public void onOutbrainRecommendationsSuccess(OBRecommendationsResponse recommendationsResponse) {
        RecyclerView recyclerView = this.recyclerViewReference.get();
        if (recyclerView == null) {
            return;
        }
        Context ctx = recyclerView.getContext();

        handleRecommendationsResponse(ctx, recommendationsResponse);
    }

    ////////////////////////////////////////
    // MultivacListener implementation
    ////////////////////////////////////////


    @Override
    public void onMultivacSuccess(ArrayList<OBRecommendationsResponse> cardsResponseList, int feedIdx, boolean hasMore) {
        final RecyclerView recyclerView = this.recyclerViewReference.get();
        if (recyclerView == null) {
            return;
        }
        Context ctx = recyclerView.getContext();

        if (!hasMore) {
            recyclerView.removeOnScrollListener(this.onScrollListener);
        }

        if (cardsResponseList.size() == 0) {
            Log.e(LOG_TAG, "onMultivacSuccess: received cardsResponseList with size = 0 ");
            this.isLoading = false;
            return;
        }

        this.receivedMultivacSuccessOnce = true;
        this.hasMore = hasMore;
        this.lastCardIdx += cardsResponseList.size();
        this.lastIdx += cardsResponseList.size();

        for (int i = 0; i < cardsResponseList.size(); i++) {
            OBRecommendationsResponse recommendationsResponse = cardsResponseList.get(i);
            handleRecommendationsResponse(ctx, recommendationsResponse);
        }
    }

    @Override
    public void onMultivacFailure(Exception ex) {
        isLoading = false;
        Log.e(LOG_TAG, "onMultivacFailure: " + ex.getLocalizedMessage());
        RecyclerView recyclerView = this.recyclerViewReference.get();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(this.onScrollListener);
        }
    }

    private void handleRecommendationsResponse(final Context ctx, final OBRecommendationsResponse recommendationsResponse) {
        if (recommendationsResponse.getAll().size() == 0) {
            Log.e(LOG_TAG, "onOutbrainRecommendationsFailure: no recs returned from fetchRecommendations(), for widget id: " + recommendationsResponse.getRequest().getWidgetJsId());
            return;
        }
        Log.i(LOG_TAG, "onOutbrainRecommendationsSuccess: received " + recommendationsResponse.getAll().size() + " new recs, for widget id: " + recommendationsResponse.getObRequest().getWidgetId());

        prefetchImages(recommendationsResponse.getAll());
        if (advancedListenerReference != null && advancedListenerReference.get() != null) {
            advancedListenerReference.get().onOutbrainRecsReceived(recommendationsResponse.getAll(), recommendationsResponse.getObRequest().getWidgetId());

        }

        if (recommendationsResponse.getSettings().getRecMode().equals("odb_timeline") && this.hasWeeklyHighlightsItem) {
            // add only one weekly highlights item
            isLoading = false;
            return;
        }

        if (this.sfItems.size() == 0) { // first load
            if (!handleParentWidgetResponse(recommendationsResponse)) {
                // Error - SmartFeed is not supported for widget id
                return;
            }
            OBSmartFeedService.addNewItemsToSmartFeedArray(ctx, recommendationsResponse, this.isSmartfeedWithNoChildren, true);
        } else {
            // not first load, this is the multivac response
            OBSmartFeedService.addNewItemsToSmartFeedArray(ctx, recommendationsResponse, true, false);
        }
    }

    private boolean handleParentWidgetResponse(OBRecommendationsResponse recsResponse) {
        if (recsResponse.getSettings().isSmartFeed()) { // main widget id supports SmartFeed
            List<String> feedContentList = recsResponse.getSettings().getFeedContentList();
            this.isRTL = recsResponse.getSettings().isRTL();
            this.feedHeaderTitle = recsResponse.getSettings().getWidgetHeaderText();
            this.smartfeedHeaderFontSize = recsResponse.getSettings().getSmartfeedHeaderFontSize();
            this.feedAbTestVal = recsResponse.getRequest().getAbTestVal();
            if (feedContentList == null) {
                this.isSmartfeedWithNoChildren = true;
                this.recyclerViewReference.get().removeOnScrollListener(this.onScrollListener);
            }

            this.isViewabilityPerListingEnabled = recsResponse.getSettings().isViewabilityPerListingEnabled();
            if (this.isViewabilityPerListingEnabled) {
                int reportingIntervalMillis = recsResponse.getSettings().getViewabilityPerListingReportingIntervalMillis();
                SFViewabilityService.getInstance().startReportViewability(reportingIntervalMillis);
            }

            // for read more module
            String readMoreText = recsResponse.getSettings().getReadMoreText();
            if (readMoreModuleHelper != null && readMoreText != null) {
                readMoreModuleHelper.setReadMoreText(readMoreText);
            }

            return true;
        } else {
            Log.e(LOG_TAG, "Error - SmartFeed is not supported for Widget ID: " + this.mainWidgetID);
            this.recyclerViewReference.get().removeOnScrollListener(this.onScrollListener);
            return false;
        }
    }

    private void prefetchImages(ArrayList<OBRecommendation> recsList) {
        Context ctx = this.recyclerViewReference.get().getContext();

        // https://stackoverflow.com/a/25084150/583425
        for (OBRecommendation rec : recsList) {
            String imageUrl = (rec.getThumbnail() != null) ? rec.getThumbnail().getUrl() : null;
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).fetch();
            }
        }
    }

    public boolean isPositionBelongToSmartfeed(final int position, final int recycleViewSizeBeforeSmartfeed) {
        return (position >= recycleViewSizeBeforeSmartfeed && position < (recycleViewSizeBeforeSmartfeed + this.getSmartFeedItemCount()));
    }

    public boolean isViewTypeBelongToSmartfeed(final int viewType) {
        return (viewType >= SF_HEADER_VIEW_TYPE && viewType < (currentViewTypeIdx + 20));
    }

    public int getItemViewType(final int position, final int originalRecycleViewSize) {
       try {
           return _getItemViewType(position, originalRecycleViewSize);
       }
       catch (Exception e) {
           OBErrorReporting.getInstance().reportErrorToServer("OBSmartFeed getItemViewType() - " + e.getLocalizedMessage());
       }
       return 0;
    }

    private int _getItemViewType(final int position, final int originalRecycleViewSize) {
        if (isReadMoreModuleEnabled && position <= originalRecycleViewSize + 2) {
            if (position == originalRecycleViewSize) {
                this.readMoreModuleHelper.setReadMoreItemPosition(position);
                return SF_READ_MORE_ITEM_VIEW_TYPE;
            } else if (position == originalRecycleViewSize + 1) {
                return SF_HEADER_VIEW_TYPE;
            }
        } else if (position == originalRecycleViewSize) {
            return SF_HEADER_VIEW_TYPE;
        }
        int sfItemIdx = getSFItemIdx(position, originalRecycleViewSize);
        SFItemData sfItem = getSmartFeedItems().get(sfItemIdx);
        String widgetID = sfItem.getWidgetID();
        int baseViewType = getViewTypeFromSFItemType(sfItem.itemType());
        boolean isCustomUIForWidgetID = mapCustomUIWidgetIDToResourceID.containsKey(widgetID);
        boolean isCustomUIForItemType = mapCustomUIItemTypeToResourceID.containsKey(sfItem.itemType());
        // Custom UI
        if (isCustomUIForWidgetID || isCustomUIForItemType) {
            try {
                sfItem.setCustomUI(true);
                String customUIMapKey = isCustomUIForWidgetID ?
                        SFUtils.generateCustomUIMapKeyForWidgetID(widgetID, baseViewType) :
                        SFUtils.generateCustomUIMapKeyForSFItemType(sfItem.itemType(), baseViewType);
                int resourceID = isCustomUIForWidgetID ?
                        mapCustomUIWidgetIDToResourceID.get(widgetID) :
                        mapCustomUIItemTypeToResourceID.get(sfItem.itemType());
                int viewType;
                // check that map key is not already in mapCustomUIKeyToCustomViewType
                if (mapCustomUIKeyToCustomViewType.containsKey(customUIMapKey)) {
                    viewType = mapCustomUIKeyToCustomViewType.get(customUIMapKey);
                } else {
                    viewType = getViewTypeOfCustomUI(resourceID, baseViewType);
                    this.mapCustomUIKeyToCustomViewType.put(customUIMapKey, viewType);
                }
                return viewType;
            } catch (Exception e) {
                if (isCustomUIForWidgetID) {
                    mapCustomUIWidgetIDToResourceID.remove(widgetID);
                } else {
                    mapCustomUIItemTypeToResourceID.remove(sfItem.itemType());
                }
                sfItem.setCustomUI(false);
                Log.e(LOG_TAG, e.getMessage());
                OBErrorReporting.getInstance().reportErrorToServer("OBSmartFeed _getItemViewType() - " + e.getLocalizedMessage());
            }
        }
        return baseViewType;
    }

    private int getViewTypeFromSFItemType(SFItemData.SFItemType sfItemType) {
        switch (sfItemType) {
            case SINGLE_ITEM:
                return SF_SINGLE_ITEM_VIEW_TYPE;
            case HORIZONTAL_CAROUSEL:
                return SF_HORIZONTAL_CAROUSEL_VIEW_TYPE;
            case GRID_TWO_ITEMS_IN_LINE:
                return SF_TWO_ITEMS_IN_LINE_VIEW_TYPE;
            case GRID_THREE_ITEMS_IN_LINE:
                return SF_THREE_ITEMS_IN_LINE_VIEW_TYPE;
            case STRIP_THUMBNAIL_ITEM:
                return SF_STRIP_THUMBNAIL_TYPE;
            case VIDEO_ITEM:
                return SF_VIDEO_ITEM_TYPE;
            case IN_WIDGET_VIDEO_ITEM:
                return SF_IN_WIDGET_VIDEO_ITEM_TYPE;
            case GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO:
                return SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE;
            case BRANDED_CAROUSEL_ITEM:
                return SF_DYNAMIC_REC_CAROUSEL_ITEM_VIEW_TYPE;
            case BRANDED_APP_INSTALL:
                return SF_DYNAMIC_REC_APP_INSTALL_ITEM_VIEW_TYPE;
            case WEEKLY_UPDATE_ITEM:
                return SF_WEEKLY_HIGHLIGHTS_ITEM_VIEW_TYPE;
        }
        return SF_SINGLE_ITEM_VIEW_TYPE;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            return _onCreateViewHolder(parent, viewType);
        }
        catch (Exception e) {
            OBErrorReporting.getInstance().reportErrorToServer("OBSmartFeed onCreateViewHolder() - " + e.getLocalizedMessage());
        }
        return null;
    }
    public RecyclerView.ViewHolder _onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Custom Read More Button
        if (this.customUIForReadMoreViewResourceID != 0 && viewType == SF_READ_MORE_ITEM_VIEW_TYPE) {
            View v = inflater.inflate(this.customUIForReadMoreViewResourceID, parent, false);
            return new OutbrainReadMoreItemViewHolder(v);
        }

        // Custom Header
        if (this.customUIForHeaderResourceID != 0 && viewType == SF_HEADER_VIEW_TYPE) {
            View v = inflater.inflate(this.customUIForHeaderResourceID, parent, false);
            return new OutbrainHeaderViewHolder(v);
        }

        // If exists, get the template (default) UI of the Custom UI
        int baseViewType = mapCustomViewTypeToBaseViewType.get(viewType);

        if (baseViewType != 0) {
            // Custom UI
            return getViewHolderForCustomUI(parent, viewType, inflater, baseViewType);
        } else {
            // Default UI
            return getViewHolderForDefaultUI(parent, viewType, inflater);
        }
    }

    private RecyclerView.ViewHolder getViewHolderForDefaultUI(ViewGroup parent, int viewType, LayoutInflater inflater) {
        View v;
        switch (viewType) {
            case SF_SINGLE_ITEM_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_single_item, parent, false);
                break;
            case SF_HORIZONTAL_CAROUSEL_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_carousel_container, parent, false);
                break;
            case SF_TWO_ITEMS_IN_LINE_VIEW_TYPE:
            case SF_THREE_ITEMS_IN_LINE_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_items_in_line, parent, false);
                break;
            case SF_STRIP_THUMBNAIL_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_strip_thumnbnail, parent, false);
                break;
            case SF_HEADER_VIEW_TYPE:
                v = inflater.inflate(this.isRTL ? R.layout.outbrain_sfeed_header_rtl : R.layout.outbrain_sfeed_header, parent, false);
                break;
            case SF_READ_MORE_ITEM_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_read_more_item, parent, false);
                break;
            case SF_VIDEO_ITEM_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_video_item, parent, false);
                break;
            case SF_IN_WIDGET_VIDEO_ITEM_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_single_item_with_video, parent, false);
                break;
            case SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_items_in_line_with_video, parent, false);
                break;
            case SF_DYNAMIC_REC_CAROUSEL_ITEM_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_branded_carousel_container, parent, false);
                break;
            case SF_DYNAMIC_REC_APP_INSTALL_ITEM_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_branded_app_install_item, parent, false);
                break;
            case SF_WEEKLY_HIGHLIGHTS_ITEM_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_week_highlights_container, parent, false);
                break;
            default:
                v = null;
        }
        return getViewHolder(viewType, v, 0);
    }

    private RecyclerView.ViewHolder getViewHolderForCustomUI(ViewGroup parent, int viewType, LayoutInflater inflater, int baseViewType) {
        View v;
        int sourceID = mapCustomUIViewTypeToResourceID.get(viewType);

        switch (baseViewType) {
            // items in line
            case SF_TWO_ITEMS_IN_LINE_VIEW_TYPE:
            case SF_THREE_ITEMS_IN_LINE_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_items_in_line, parent, false);
                return getViewHolder(baseViewType, v, sourceID);
            // items in line with video
            case SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_items_in_line_with_video, parent, false);
                return getViewHolder(baseViewType, v, sourceID);
            // horizontal item
            case SF_HORIZONTAL_CAROUSEL_VIEW_TYPE:
                v = inflater.inflate(R.layout.outbrain_sfeed_carousel_container, parent, false);
                break;
            default:
                v = inflater.inflate(sourceID, parent, false);
                break;
        }

        return getViewHolder(baseViewType, v, 0);
    }

    private RecyclerView.ViewHolder getViewHolder(int viewType, View v, int singleItemSourceID) {
        if (v == null) {
            return null;
        }

        switch (viewType) {
            case SF_SINGLE_ITEM_VIEW_TYPE:
            case SF_STRIP_THUMBNAIL_TYPE:
            case SF_IN_WIDGET_VIDEO_ITEM_TYPE:
                return new OutbrainSingleItemViewHolder(v);
            case SF_HORIZONTAL_CAROUSEL_VIEW_TYPE:
                return new OutbrainCarouselContainerViewHolder(v);
            case SF_TWO_ITEMS_IN_LINE_VIEW_TYPE:
            case SF_TWO_ITEMS_IN_LINE_WITH_VIDEO_VIEW_TYPE:
                return new OutbrainItemsInLineViewHolder(v, 2, singleItemSourceID, this.defaultMarginInGrid);
            case SF_THREE_ITEMS_IN_LINE_VIEW_TYPE:
                return new OutbrainItemsInLineViewHolder(v, 3, singleItemSourceID, this.defaultMarginInGrid);
            case SF_HEADER_VIEW_TYPE:
                return new OutbrainHeaderViewHolder(v);
            case SF_READ_MORE_ITEM_VIEW_TYPE:
                return new OutbrainReadMoreItemViewHolder(v);
            case SF_VIDEO_ITEM_TYPE:
                return new OutbrainVideoItemViewHolder(v);
            case SF_DYNAMIC_REC_CAROUSEL_ITEM_VIEW_TYPE:
                return new BrandedCarouselContainerViewHolder(v);
            case SF_DYNAMIC_REC_APP_INSTALL_ITEM_VIEW_TYPE:
                return new BrandedAppInstallItemViewHolder(v);
            case SF_WEEKLY_HIGHLIGHTS_ITEM_VIEW_TYPE:
                return new WeeklyHighlightsContainerViewHolder(v);
            default:
                OBErrorReporting.getInstance().reportErrorToServer("OBSmartFeed - getViewHolder() - viewType is outside scope of switch-case: " + viewType);
                return null;
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, final int originalRecycleViewSize) {
        try {
            _onBindViewHolder(holder, position, originalRecycleViewSize);
        }
        catch (Exception e) {
            OBErrorReporting.getInstance().reportErrorToServer("OBSmartFeed onBindViewHolder() - " + e.getLocalizedMessage());
        }
    }

    private void _onBindViewHolder(RecyclerView.ViewHolder holder, final int position, final int originalRecycleViewSize) {
        // Read More module
        if (isReadMoreModuleEnabled && position <= originalRecycleViewSize + 1) {
            onBindViewHolderForReadMoreModule(holder, position, originalRecycleViewSize);
            return;
        }
        else if (position < originalRecycleViewSize) { // Publisher view holder
            return;
        }

        holder.itemView.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());

        if (position == originalRecycleViewSize) { // Smartfeed Header
            onBindOutbrainSmartFeedHeader((OutbrainHeaderViewHolder) holder);
            return;
        }

        int sfItemIdx = getSFItemIdx(position, originalRecycleViewSize);
        SFItemData sfItem = getSmartFeedItems().get(sfItemIdx);

        switch (sfItem.itemType()) {
            case SINGLE_ITEM:
            case STRIP_THUMBNAIL_ITEM:
            case IN_WIDGET_VIDEO_ITEM:
                onBindOutbrainSingleItem(sfItem, (OutbrainSingleItemViewHolder) holder);
                break;
            case HORIZONTAL_CAROUSEL:
                onBindCarouselContainerItem(sfItem, (OutbrainCarouselContainerViewHolder) holder);
                break;
            case GRID_TWO_ITEMS_IN_LINE:
            case GRID_THREE_ITEMS_IN_LINE:
            case GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO:
                onBindOutbrainItemsInLine(sfItem, (OutbrainItemsInLineViewHolder) holder);
                break;
            case BRANDED_CAROUSEL_ITEM:
                onBindOutbrainBrandedCarouselItem(sfItem, (BrandedCarouselContainerViewHolder) holder);
                break;
            case BRANDED_APP_INSTALL:
                onBindOutbrainBrandedAppInstallItem(sfItem, (BrandedAppInstallItemViewHolder) holder);
                break;
            case WEEKLY_UPDATE_ITEM:
                onBindOutbrainWeeklyHighlightsItem(sfItem, (WeeklyHighlightsContainerViewHolder) holder);
                break;
        }
    }

    private void onBindViewHolderForReadMoreModule(RecyclerView.ViewHolder holder, final int position, final int originalRecycleViewSize) {
        if (position < originalRecycleViewSize) { // publisher item
            readMoreModuleHelper.onBindPublisherViewHolder(holder, position);
            return;
        }

        if (position == originalRecycleViewSize) { // Read More Button
            readMoreModuleHelper.onBindOutbrainReadMoreItem((OutbrainReadMoreItemViewHolder) holder);
        }
        else if (position == originalRecycleViewSize + 1) { // Smartfeed Header
            onBindOutbrainSmartFeedHeader((OutbrainHeaderViewHolder) holder);
        }
        holder.itemView.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());
    }

    private void onBindOutbrainSmartFeedHeader(OutbrainHeaderViewHolder holder) {
        if (this.isSmartfeedWithNoChildren && this.customUIForHeaderResourceID == 0) {
            // smart feed without children
            holder.outbrainLogoButton.setImageResource(R.drawable.recommendedbylarge);
        }
        holder.textView.setText(this.feedHeaderTitle);
        holder.textView.setTextColor(SFThemeImpl.getInstance().sfHeaderColor());
        if (this.smartfeedHeaderFontSize != 0) {
            holder.textView.setTextSize(this.smartfeedHeaderFontSize);
        }

        holder.outbrainLogoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerReference.get() != null) {
                    listenerReference.get().userTappedOnAboutOutbrain();
                }
            }
        });
    }

    private void onBindOutbrainSingleItem(final SFItemData sfItem, OutbrainSingleItemViewHolder holder) {
        OBRecommendation rec = sfItem.getSingleRec();
        Context ctx = holder.layout.getContext();

        if (holder.widgetTitleRL != null) {
            SFUtils.onBindItemHeader(
                    holder.widgetTitleRL,
                    holder.widgetTitleTV,
                    sfItem,
                    sfItem.isRTL()
            );
        }

        // CTA view is supported for single item with default layout (no custom ui)
        if (sfItem.itemType().equals(SINGLE_ITEM) && !sfItem.isCustomUI() && !"".equals(rec.getCtaText()) && sfItem.getSettings().shouldShowCtaButton()) {
            SFUtils.handleAndBindCtaViewOnSingleRec(holder, rec.getCtaText());
        } else if (holder.cardView.findViewById(R.id.ob_rec_cta_tv) != null) {
            // For reuse view holder - if cta view on screen we want to hide it
            ((TextView) holder.cardView.findViewById(R.id.ob_rec_cta_tv)).setVisibility(View.GONE);
        }

        SFSingleRecView sfSingleRecView = new SFSingleRecView(
                holder.wrapperView,
                holder.cardView,
                holder.recImageView,
                holder.disclosureImageView,
                holder.recSourceTV,
                holder.recTitleTV,
                holder.logoImageView,
                holder.paidLabelTV,
                holder.seperatorLine
        );

        SFUtils.onBindSingleRec(listenerReference.get() , sfSingleRecView, rec, ctx, sfItem);

        // bind video
        if (sfItem.itemType() == IN_WIDGET_VIDEO_ITEM &&
                holder.frameLayout != null &&
                holder.webView != null) {
            onBindOutbrainVideoItem(sfItem, holder, ctx);
        }

        // Viewability per listing
        if (this.isViewabilityPerListingEnabled && holder.wrapperView instanceof OBCardView) {
            SFViewabilityService.registerOBCardView(
                    (OBCardView) holder.wrapperView,
                    sfItem.getResponseRequest().getReqId(),
                    rec.getPosition(),
                    this.initializationTime
            );
        }
    }

    private void onBindCarouselContainerItem(SFItemData sfItem, OutbrainCarouselContainerViewHolder holder) {
        if (holder.widgetTitleRL != null) {
            SFUtils.onBindItemHeader(
                    holder.widgetTitleRL,
                    holder.widgetTitleTV,
                    sfItem,
                    sfItem.isRTL()
            );
        }

        String widgetID = sfItem.getWidgetID();
        int customLayoutResourceID = 0;
        if (mapCustomUIWidgetIDToResourceID.containsKey(widgetID)) {
            customLayoutResourceID = mapCustomUIWidgetIDToResourceID.get(widgetID);
        } else if (mapCustomUIItemTypeToResourceID.containsKey(sfItem.itemType())) {
            customLayoutResourceID = mapCustomUIItemTypeToResourceID.get(sfItem.itemType());
        }

        holder.horizontalScroll.setOrientation(DSVOrientation.HORIZONTAL);
        holder.horizontalScroll.setAdapter(
                new SFHorizontalAdapter(
                        this.listenerReference.get(),
                        customLayoutResourceID,
                        sfItem,
                        this.initializationTime,
                        this.isViewabilityPerListingEnabled,
                        this.displaySourceOnOrganicRec
                )
        );
        holder.horizontalScroll.setItemTransitionTimeMillis(150);

        holder.horizontalScroll.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.95f)
                .build());
    }

    private void onBindOutbrainWeeklyHighlightsItem(SFItemData sfItem, final WeeklyHighlightsContainerViewHolder holder) {
        holder.widgetTitleRL.setVisibility(View.VISIBLE);

        String title = sfItem.getTitle();
        String titleTextColor = sfItem.getTitleTextColor();

        holder.widgetTitleTV.setText(title == null || title.length() < 2 ? "LAST WEEK HIGHLIGHTS" : title);
        holder.widgetTitleTV.setTextColor(Color.parseColor(titleTextColor == null ? "#EF8222" : titleTextColor));

        holder.horizontalAutoScroll.setAdapter(
                new SFWeeklyHighlightsHorizontalAdapter(
                        this.listenerReference.get(),
                        sfItem,
                        this.initializationTime,
                        this.isViewabilityPerListingEnabled
                )
        );

        // scroll to the first item
        int numberOfRecs = sfItem.getOutbrainRecs().size();
        int number = (Integer.MAX_VALUE / numberOfRecs) / 2;
        holder.horizontalAutoScroll.getLayoutManager().scrollToPosition(number * numberOfRecs);
    }

    private void onBindOutbrainBrandedAppInstallItem(SFItemData sfItem, final BrandedAppInstallItemViewHolder holder) {
        OBRecommendation rec = sfItem.getSingleRec();
        Context ctx = holder.layout.getContext();

        holder.itemView.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());
        holder.sourceImage.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());

        OBBrandedItemSettings brandedItemSettings = sfItem.getSettings().getBrandedItemSettings();

        holder.sourceTV.setText(brandedItemSettings.getSponsor());
        holder.sourceTV.setTextColor(SFThemeImpl.getInstance().recTitleTextColor(true));

        Picasso.get().load(brandedItemSettings.getThumbnail().getUrl()).into(holder.sourceImage);

        SFSingleRecView sfSingleRecView = new SFSingleRecView(
                holder.cardView,
                holder.image,
                holder.titleTextView
        );

        SFUtils.onBindSingleRec(listenerReference.get() , sfSingleRecView, rec, ctx, sfItem);

        // set tablet card size
        boolean tabletSize = ctx.getResources().getBoolean(R.bool.obsdk_isTablet);
        if (tabletSize) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(
                    SFUtils.convertDpToPx(ctx, 100), // left
                    SFUtils.convertDpToPx(ctx, 10),  // top
                    SFUtils.convertDpToPx(ctx, 100), // right
                    SFUtils.convertDpToPx(ctx, 12)   // bottom
            );
            holder.cardView.setLayoutParams(params);
        }

        // Viewability per listing
        if (this.isViewabilityPerListingEnabled && holder.cardView instanceof OBCardView) {
            SFViewabilityService.registerOBCardView(
                    (OBCardView) holder.cardView,
                    sfItem.getResponseRequest().getReqId(),
                    rec.getPosition(),
                    this.initializationTime
            );
        }
    }

    private void onBindOutbrainBrandedCarouselItem(SFItemData sfItem, final BrandedCarouselContainerViewHolder holder) {
        OBBrandedItemSettings brandedCarouselSettings = sfItem.getSettings().getBrandedItemSettings();
        holder.itemView.setBackgroundColor(SFThemeImpl.getInstance().primaryColor());
        holder.titleTV.setText(brandedCarouselSettings.getContent());
        holder.sourceTV.setText(brandedCarouselSettings.getSponsor());

        holder.titleTV.setTextColor(SFThemeImpl.getInstance().recTitleTextColor(true));
        holder.sourceTV.setTextColor(SFThemeImpl.getInstance().recTitleTextColor(true));

        Picasso.get().load(brandedCarouselSettings.getThumbnail().getUrl()).into(holder.sourceImage);

        holder.horizontalScroll.setOrientation(DSVOrientation.HORIZONTAL);
        holder.horizontalScroll.setAdapter(
                new SFHorizontalAdapter(
                        this.listenerReference.get(),
                        0,
                        sfItem,
                        this.initializationTime,
                        this.isViewabilityPerListingEnabled,
                        this.displaySourceOnOrganicRec
                )
        );
        holder.horizontalScroll.setItemTransitionTimeMillis(150);

        int numberOfRecs = sfItem.getOutbrainRecs().size();
        holder.pageIndicatorView.setCount(numberOfRecs);

        holder.pageIndicatorView.setSelection(0);

        holder.pageIndicatorView.setSelectedColor(SFThemeImpl.getInstance().pageIndicatorSelectedColor());

        holder.horizontalScroll.addScrollStateChangeListener(new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) { }

            @Override
            public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                holder.pageIndicatorView.setSelection(adapterPosition);
            }

            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
                holder.pageIndicatorView.setSelection(newPosition);
            }
        });
    }

    private void onBindOutbrainItemsInLine(SFItemData sfItem, OutbrainItemsInLineViewHolder holder) {
        final ArrayList<OBRecommendation> recs = sfItem.getOutbrainRecs();
        Context ctx = holder.layout.getContext();
        SFSingleRecView sfSingleRecView;

        if (holder.widgetTitleRL != null) {
            SFUtils.onBindItemHeader(
                    holder.widgetTitleRL,
                    holder.widgetTitleTV,
                    sfItem,
                    sfItem.isRTL()
            );
        }

        for (int i = 0; i < holder.sfRecViews.length; i++) {
            sfSingleRecView = holder.sfRecViews[i];

            SFUtils.onBindSingleRec(listenerReference.get() , sfSingleRecView, recs.get(i), ctx, sfItem);

            if (this.displaySourceOnOrganicRec && !sfItem.isCustomUI()) {
                sfSingleRecView.recSourceTV.setVisibility(View.VISIBLE);
            }

            // Viewability per listing
            if (this.isViewabilityPerListingEnabled && sfSingleRecView.recWrapper instanceof OBCardView) {
                SFViewabilityService.registerOBCardView(
                        (OBCardView) sfSingleRecView.recWrapper,
                        sfItem.getResponseRequest().getReqId(),
                        recs.get(i).getPosition(),
                        this.initializationTime
                );
            }
        }

        // bind video
        if (sfItem.itemType() == GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO &&
                holder.frameLayout != null &&
                holder.webView != null) {
            onBindOutbrainVideoItem(sfItem, holder, ctx);
        }
    }

    private void onBindOutbrainVideoItem(SFItemData sfItem, final OutbarainVideoAbstractViewHolder holder, Context ctx) {
        // check if other video in the article is playing
        if (advancedListenerReference != null &&
                advancedListenerReference.get() != null &&
                advancedListenerReference.get().isVideoCurrentlyPlaying())
        {
            VideoUtils.hideVideoItem(holder, ctx);
            return;
        }

        // frameLayout is the layout that holds the webView
        // we want to check if video is currently playing
        if (holder.frameLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        VideoUtils.initVideo(holder, listenerReference.get(), sfItem, URL, ctx);

    }

    @Override
    public void notifyNewItems(final ArrayList<SFItemData> sfItemsList, final boolean shouldUpdateUI) {
        final RecyclerView recyclerView = recyclerViewReference.get();
        final boolean isFirstResponse = sfItems.size() == 0;
        sfItems.addAll(sfItemsList);

        if (sfItemsList.get(0).itemType() == SFItemData.SFItemType.WEEKLY_UPDATE_ITEM) { // weekly highlights item
            this.hasWeeklyHighlightsItem = true;
        }

        if (isFirstResponse) {
            // this is the response of the parent widget, now we will immediately try to fetch the children
            fetchMoreRecommendations();
        }

        if (this.isInMiddleOfRecycleView && shouldUpdateUI) {
            if (advancedListenerReference != null &&
                    advancedListenerReference.get() != null)
            {
                // If Smartfeed is in the middle of the RecycleView we want to notify the app developer so he can handle the
                // reloading of the adapter in the app level.
                advancedListenerReference.get().smartfeedIsReadyWithRecs();
            }
            else {
                Log.e("OBSDK", "Smartfeed is set with *isInMiddleOfRecycleView* flag but OBSmartFeedAdvancedListener is missing");
            }
            isLoading = false;
            return;
        }

        if (recyclerView != null && shouldUpdateUI) {
            final int totalCountBeforeUpdate = recyclerView.getAdapter().getItemCount();
            runOnMainThread(recyclerView.getContext(), new Runnable() {
                public void run() {
                    if (recyclerView.getAdapter() == null) {
                        // fix a crash for cases where adapter is null. It seems to happen when the system try to restore screen from background state
                        return;
                    }
                    int newItems = isFirstResponse ? sfItemsList.size() + 1 : sfItemsList.size();
                    recyclerView.getAdapter().notifyItemRangeInserted(totalCountBeforeUpdate, newItems);
                    isLoading = false;
                }
            });
        }
    }

    @Override
    public void notifyNoNewItemsToAdd() {
        isLoading = false;
    }

    private int getSFItemIdx(int position, int originalRecycleViewSize) {
        return position - (originalRecycleViewSize + (this.isReadMoreModuleEnabled ? 2 : 1));
    }

    public ArrayList<SFItemData> getSmartFeedItems() {
        return sfItems;
    }

    public int getSmartFeedItemCount() {
        if (this.isReadMoreModuleEnabled) {
            return sfItems.size() == 0 ? 1 : sfItems.size() + 2;
        } else {
            return sfItems.size() == 0 ? 0 : sfItems.size() + 1;
        }
    }

    public SFItemData.SFItemType getSfItemType(final int position, final int originalRecycleViewSize) {
        if (position == originalRecycleViewSize) {
            return SFItemData.SFItemType.SF_HEADER;
        }
        int itemIndex = getSFItemIdx(position, originalRecycleViewSize);
        return itemIndex < sfItems.size() ? sfItems.get(itemIndex).itemType() : SFItemData.SFItemType.SF_BAD_TYPE;
    }

    public boolean hasMore() {
        return hasMore;
    }
}
