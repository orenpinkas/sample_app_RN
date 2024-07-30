package com.outbrain.OBSDK.Entities;


import com.outbrain.OBSDK.Errors.OBErrorReporting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OBSettings extends OBBaseEntity {
    private static final String APV = "apv";
    private static final String IS_SMART_FEED = "isSmartFeed";
    private static final String SF_FEED_CONTENT = "feedContent";
    private static final String VIEWABILITY_ENABLED = "globalWidgetStatistics";
    private static final String VIEWABILITY_THRESHOLD = "ViewabilityThreshold";
    private static final String FEED_CYCLES_LIMIT = "feedCyclesLimit";
    private static final String FEED_CARD_TYPE = "feedCardType";
    private static final String NANO_ORGANINCS_HEADER = "nanoOrganicsHeader";
    private static final String HEADER_TEXT_COLOR = "dynamic:HeaderColor";
    private static final String REC_MODE = "recMode";
    private static final String VIDEO_URL = "sdk_sf_vidget_url";
    private static final String SHADOW_COLOR = "sdk_sf_shadow_color";
    private static final String PAID_LABEL_TEXT = "dynamic:PaidLabel";
    private static final String PAID_LABEL_TEXT_COLOR = "dynamic:PaidLabelTextColor";
    private static final String PAID_LABEL_BACKGROUND_COLOR = "dynamic:PaidLabelBackgroundColor";
    private static final String SOURCE_FORMAT = "dynamicSourceFormat";
    private static final String CHUNK_SIZE = "feedLoadChunkSize";
    private static final String LISTINGS_VIEWABILITY = "listingsViewability";
    private static final String LISTINGS_VIEWABILITY_REPORTING_INTERVAL = "listingsViewabilityReportingIntervalMillis";
    private static final String AB_TITLE_FONT_SIZE = "dynamic:TitleFontSize";
    private static final String AB_TITLE_TEXT_STYLE = "dynamic:TitleTextStyle";
    private static final String AB_SOURCE_FONT_SIZE = "dynamic:SourceFontSize";
    private static final String AB_SOURCE_FONT_COLOR = "dynamic:SourceColor";
    private static final String AB_IMAGE_FADE_ANIMATION = "imgFade";
    private static final String AB_IMAGE_FADE_DURATION = "imgFadeDur";
    private static final String READ_MORE_TEXT = "readMoreText";
    private static final String SHOW_CTA_BUTTON = "dynamic:IsShowButton";
    private static final String SMARTFEED_HEADER_SIZE = "dynamic:HeaderFontSize";

    private static final String ORGANIC_SOURCE_FORMAT = "dynamicOrganicSourceFormat";
    private static final String PAID_SOURCE_FORMAT = "dynamicPaidSourceFormat";


    private boolean apv;
    private boolean isRTL;
    private boolean isSmartFeed;
    private boolean isTrendingInCategoryCard;
    private boolean shouldShowCtaButton;
    private int smartfeedHeaderFontSize;
    private String jsonString;
    private ArrayList<String> feedContentList;
    private int feedCyclesLimit;
    private String widgetHeaderText;
    private String widgetHeaderTextColor;
    private String recMode;
    private String videoUrl;
    private String shadowColor;
    transient private JSONObject OBSettingsJSONObject;
    private String paidLabelText;
    private String paidLabelTextColor;
    private String paidLabelBackgroundColor;
    private String organicSourceFormat;
    private String paidSourceFormat;
    private int feedChunkSize;
    private boolean isViewabilityPerListingEnabled;
    private int viewabilityPerListingReportingIntervalMillis;
    private OBViewabilityActions viewabilityActions;
    private OBBrandedItemSettings brandedItemSettings;
    private String readMoreText;

    // AB TESTS
    private int abTitleFontSize;
    private int abTitleFontStyle; // (Bold (1) or normal (0)
    private int abSourceFontSize;
    private String abSourceFontColor;
    private boolean abImageFadeAnimation;
    private int abImageFadeDuration;

    public OBSettings(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }
        OBSettingsJSONObject = jsonObject;
        // Storing each json item in variable
        this.jsonString = jsonObject.toString();
        this.apv = jsonObject.optBoolean(APV);
        this.isRTL = "RTL".equals(jsonObject.optString("dynamicWidgetDirection"));
        this.isSmartFeed = jsonObject.optBoolean(IS_SMART_FEED);
        this.widgetHeaderText = jsonObject.optString(NANO_ORGANINCS_HEADER);
        this.widgetHeaderTextColor = jsonObject.optString(HEADER_TEXT_COLOR);
        this.recMode = jsonObject.optString(REC_MODE);
        this.videoUrl = jsonObject.optString(VIDEO_URL, "https://libs.outbrain.com/video/app/vidgetInApp.html");
        this.shadowColor = jsonObject.optString(SHADOW_COLOR);
        this.paidLabelText = jsonObject.optString(PAID_LABEL_TEXT);
        this.paidLabelTextColor = jsonObject.optString(PAID_LABEL_TEXT_COLOR);
        this.paidLabelBackgroundColor = jsonObject.optString(PAID_LABEL_BACKGROUND_COLOR);
        this.organicSourceFormat = jsonObject.optString(ORGANIC_SOURCE_FORMAT);
        this.paidSourceFormat = jsonObject.optString(PAID_SOURCE_FORMAT);
        this.isTrendingInCategoryCard = jsonObject.optString(FEED_CARD_TYPE).equals("CONTEXTUAL_TRENDING_IN_CATEGORY");
        this.shouldShowCtaButton = jsonObject.optBoolean(SHOW_CTA_BUTTON);
        this.smartfeedHeaderFontSize = jsonObject.optInt(SMARTFEED_HEADER_SIZE, 0);
        this.isViewabilityPerListingEnabled =
                !jsonObject.has(LISTINGS_VIEWABILITY) || jsonObject.optBoolean(LISTINGS_VIEWABILITY);
        this.viewabilityPerListingReportingIntervalMillis = jsonObject.has(LISTINGS_VIEWABILITY_REPORTING_INTERVAL) ?
                jsonObject.optInt(LISTINGS_VIEWABILITY_REPORTING_INTERVAL) : 2500;
        if (jsonObject.has(SF_FEED_CONTENT)) {
            try {
                this.feedChunkSize = jsonObject.optInt(CHUNK_SIZE);
                this.feedCyclesLimit = jsonObject.optInt(FEED_CYCLES_LIMIT);
                JSONArray jsonArray = new JSONArray(jsonObject.optString(SF_FEED_CONTENT));
                this.feedContentList = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++){
                    this.feedContentList.add(jsonArray.getJSONObject(i).optString("id"));
                }
            } catch (JSONException e) {
                OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        this.readMoreText = jsonObject.optString(READ_MORE_TEXT, null);

        this.abTitleFontSize = jsonObject.optInt(AB_TITLE_FONT_SIZE, 0);
        this.abTitleFontStyle = jsonObject.optInt(AB_TITLE_TEXT_STYLE, 0);
        this.abSourceFontSize = jsonObject.optInt(AB_SOURCE_FONT_SIZE, 0);
        this.abSourceFontColor = jsonObject.optString(AB_SOURCE_FONT_COLOR, null);
        this.abImageFadeAnimation = jsonObject.optBoolean(AB_IMAGE_FADE_ANIMATION, true);
        this.abImageFadeDuration = jsonObject.optInt(AB_IMAGE_FADE_DURATION, 750);
    }

    public boolean getApv() {
        return apv;
    }

    public boolean isSmartFeed() {
        return isSmartFeed;
    }

    public boolean isTrendingInCategoryCard() {
        return isTrendingInCategoryCard;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public ArrayList<String> getFeedContentList() {
        return feedContentList;
    }

    public int getFeedCyclesLimit() {
        return this.feedCyclesLimit;
    }

    public int getFeedChunkSize() {
        return this.feedChunkSize;
    }

    public String getWidgetHeaderText() {
        return this.widgetHeaderText;
    }

    public String getWidgetHeaderTextColor() {
        return widgetHeaderTextColor;
    }

    public String getRecMode() {
        return this.recMode;
    }

    public String getShadowColor() {
        return this.shadowColor;
    }

    public boolean isRTL() {
        return isRTL;
    }

    public boolean isViewabilityEnabled() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return jsonObject == null || jsonObject.optBoolean(VIEWABILITY_ENABLED, true);
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

    public String getOrganicSourceFormat() {
        return organicSourceFormat;
    }

    public String getPaidSourceFormat() {
        return paidSourceFormat;
    }

    public int viewabilityThreshold() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return 1000;
        }
        return jsonObject.optInt(VIEWABILITY_THRESHOLD, 1000); // 1000 milliseconds is the default
    }

    public boolean isViewabilityPerListingEnabled() {
        return isViewabilityPerListingEnabled;
    }

    public int getViewabilityPerListingReportingIntervalMillis() {
        return viewabilityPerListingReportingIntervalMillis;
    }

    public void setViewabilityActions(OBViewabilityActions viewabilityActions) {
        this.viewabilityActions = viewabilityActions;
    }

    public OBViewabilityActions getViewabilityActions() {
        return viewabilityActions;
    }

    public String getReadMoreText() {
        return readMoreText;
    }

    public JSONObject getJSONObject() {
        return OBSettingsJSONObject;
    }

    public int getAbTitleFontSize() {
        return abTitleFontSize;
    }

    public int getAbTitleFontStyle() {
        return abTitleFontStyle;
    }

    public int getAbSourceFontSize() {
        return abSourceFontSize;
    }

    public String getAbSourceFontColor() {
        return abSourceFontColor;
    }

    public boolean getAbImageFadeAnimation() {
        return abImageFadeAnimation;
    }

    public boolean shouldShowCtaButton() {
        return shouldShowCtaButton;
    }

    public int getSmartfeedHeaderFontSize() {
        return smartfeedHeaderFontSize;
    }

    public int getAbImageFadeDuration() {
        return abImageFadeDuration;
    }


    public void setBrandedItemSettings(OBBrandedItemSettings brandedItemSettings) {
        this.brandedItemSettings = brandedItemSettings;
    }

    public OBBrandedItemSettings getBrandedItemSettings() {
        return brandedItemSettings;
    }

    @Override
    public String toString() {
        return "OBSettings - jsonString: " + jsonString + ", apv: " + apv;
    }
}
