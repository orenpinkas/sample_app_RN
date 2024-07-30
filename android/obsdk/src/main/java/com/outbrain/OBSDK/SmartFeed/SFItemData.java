package com.outbrain.OBSDK.SmartFeed;


import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBResponseRequest;
import com.outbrain.OBSDK.Entities.OBSettings;

import java.util.ArrayList;


/**
 * Created by odedre on 3/5/18.
 */

public class SFItemData {

    public enum SFItemType {
        SF_HEADER,
        SF_READ_MORE_ITEM,
        SINGLE_ITEM,
        HORIZONTAL_CAROUSEL,
        BRANDED_CAROUSEL_ITEM,
        GRID_TWO_ITEMS_IN_LINE,
        GRID_THREE_ITEMS_IN_LINE,
        STRIP_THUMBNAIL_ITEM,
        VIDEO_ITEM,
        IN_WIDGET_VIDEO_ITEM,
        GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO,
        BRANDED_APP_INSTALL,
        WEEKLY_UPDATE_ITEM,
        SF_BAD_TYPE
    }

    private ArrayList<OBRecommendation> outbrainRecs = null;
    private OBRecommendation singleRec = null;
    private SFItemType itemType;
    private String widgetID;
    private String title;
    private String videoUrl;
    private String shadowColor;
    private String paidLabelText;
    private String paidLabelTextColor;
    private String paidLabelBackgroundColor;
    private boolean isRTL;
    private boolean isCustomUI = false;
    private boolean isLastInWidget = false; // applies for single rec only
    private OBSettings settings;
    private OBResponseRequest responseRequest;

    public SFItemData(ArrayList<OBRecommendation> outbrainRecs, SFItemType itemType, String title, OBSettings settings, OBResponseRequest responseRequest) {
        this.outbrainRecs = outbrainRecs;
        setCommonFields(itemType, title, settings, responseRequest);
    }

    public SFItemData(OBRecommendation singleRec, SFItemType itemType, String title, OBSettings settings, OBResponseRequest responseRequest, boolean isLastInWidget) {
        this.singleRec = singleRec;
        this.isLastInWidget = isLastInWidget;
        setCommonFields(itemType, title, settings, responseRequest);
    }

    private void setCommonFields(SFItemType itemType, String title, OBSettings settings, OBResponseRequest responseRequest) {
        this.itemType = itemType;
        this.widgetID = responseRequest.getWidgetJsId();
        this.shadowColor = settings.getShadowColor();
        this.title = title;
        this.videoUrl = itemType == SFItemType.VIDEO_ITEM || itemType == SFItemType.IN_WIDGET_VIDEO_ITEM || itemType == SFItemType.GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO ? settings.getVideoUrl() : null;
        this.paidLabelText = settings.getPaidLabelText();
        this.paidLabelTextColor = settings.getPaidLabelTextColor();
        this.paidLabelBackgroundColor = settings.getPaidLabelBackgroundColor();
        this.isRTL = settings.isRTL();
        this.settings = settings;
        this.responseRequest = responseRequest;
    }


    public SFItemType itemType() {
        return this.itemType;
    }

    public OBRecommendation getSingleRec() {
        return this.singleRec;
    }

    public ArrayList<OBRecommendation> getOutbrainRecs() {
        return outbrainRecs;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTitleTextColor() {
        String textColor = this.settings.getWidgetHeaderTextColor();
        return "".equals(textColor) ? null : textColor;
    }

    public String getWidgetID() {
        return this.widgetID;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public OBSettings getSettings() {
        return settings;
    }

    public OBResponseRequest getResponseRequest() {
        return responseRequest;
    }

    public String getPaidLabelText() {
        return paidLabelText;
    }

    public String getPaidLabelTextColor() {
        return paidLabelTextColor;
    }

    public String getPaidLabelBackgroundColor() {
        return paidLabelBackgroundColor;
    }

    public boolean isRTL() {
        return isRTL;
    }

    public void setCustomUI(boolean isCustomUI) {
        this.isCustomUI = isCustomUI;
    }

    public boolean isCustomUI() {
        return this.isCustomUI;
    }

    public boolean isLastInWidget() {
        return isLastInWidget;
    }
}
