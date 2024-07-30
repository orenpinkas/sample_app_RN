package com.outbrain.OBSDK.FetchRecommendations;


import com.outbrain.OBSDK.Entities.OBBaseEntity;
import com.outbrain.OBSDK.Entities.OBDisclosure;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBThumbnail;
import com.outbrain.OBSDK.Errors.OBErrorReporting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.text.StringEscapeUtils;

public class OBRecommendationImpl extends OBBaseEntity implements OBRecommendation, Serializable {
    private static final String ORIG_URL = "orig_url";
    private static final String SOURCE_NAME = "source_name";
    private static final String SAME_SOURCE = "same_source";
    private static final String PCID = "pc_id";
    private static final String ADS_TYPE = "ads_type";
    private static final String ADV_NAME = "adv_name";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String URL = "url";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String DESCRIPTION = "desc";
    private static final String THUMBNAIL = "thumbnail";
    private static final String IS_VIDEO = "isVideo";
    private static final String PIXELS = "pixels";
    private static final String CARD = "card";
    private static final String DISCLOSURE = "disclosure";
    private static final String LOGO = "logo";
    private static final String PUBLISHER_ADS = "publisherAds";
    private static final String PUBLISHER_ADS_LABEL = "label";
    private static final String IS_PUBLISHER_ADS = "isPublisherAds";
    private static final String POSITION = "pos";
    private static final String CTA = "cta";
    private static final String REQ_ID = "reqId";

    private boolean isVideo;
    private String sourceName;
    private boolean sameSource;
    private String pcId;
    private String advName;
    private Date publishDate;
    private String url;
    private String author;
    private String content;
    private String description;
    private OBThumbnail thumbnail;
    private String origUrl;
    private String[] pixels;
    private OBDisclosure disclosure;
    private OBThumbnail logo;
    private String publisherAdsLabel;
    private String position;
    private String categoryName;
    private String ctaText;
    private String reqId;
    private String adsType;


    String getOrigUrl() {
        return origUrl;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean isVideo() {
        return isVideo;
    }

    @Override
    public String getAudienceCampaignsLabel() {
        return publisherAdsLabel;
    }

    @Override
    public boolean isPaid() {
        try {
            if (url.contains("https://obnews.outbrain.com/network/redir")) {
                // OBNews context
                return (adsType != null && Integer.parseInt(adsType) == 1); // "ads_type == 1 paid, ads_type == 2 organic
            }
            if (pcId != null && Integer.parseInt(pcId) > 0) {
                return true;
            }
        }
        catch (NumberFormatException ex){
            // OBErrorReporting.getInstance().reportErrorToServer("Exception in isPaid() " + e.getLocalizedMessage());
            ex.printStackTrace();
        }

        return false;

    }

    @Override
    public OBThumbnail getThumbnail() {
        return thumbnail;
    }

    @Override
    public OBThumbnail getLogo() {
        return logo;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Date getPublishDate() {
        return publishDate;
    }

    @Override
    public String getAdvertiserName() {
        return advName;
    }

    @Override
    public String getPaidContentId() {
        return pcId;
    }

    @Override
    public boolean isSameSource() {
        return sameSource;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public boolean isRTB() {
        return shouldDisplayDisclosureIcon();
    }

    @Override
    public boolean shouldDisplayDisclosureIcon() {
        return disclosure.getClickUrl() != null && disclosure.getIconUrl() != null;
    }

    public String[] getPixels() {
        return pixels;
    }

    @Override
    public OBDisclosure getDisclosure() {
        return disclosure;
    }

    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public String getCtaText() {
        return ctaText;
    }

    public OBRecommendationImpl(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }
        
        origUrl = jsonObject.optString(ORIG_URL);
        sourceName = jsonObject.optString(SOURCE_NAME);
        sameSource = jsonObject.optString(SAME_SOURCE).equals("true");
        pcId = jsonObject.optString(PCID, null);
        adsType = jsonObject.optString(ADS_TYPE, null);
        advName = jsonObject.optString(ADV_NAME);
        publishDate = getDate(jsonObject);
        url = jsonObject.optString(URL, null);
        author = jsonObject.optString(AUTHOR);
        String contentStr = jsonObject.optString(CONTENT);
        content = StringEscapeUtils.unescapeHtml4(contentStr);
        description = jsonObject.optString(DESCRIPTION, null);
        thumbnail = new OBThumbnail(jsonObject.optJSONObject(THUMBNAIL));
        isVideo = jsonObject.optString(IS_VIDEO).equals("true");
        parsePixels(jsonObject.optJSONArray(PIXELS));
        parseCardCategoryIfNeeded(jsonObject.optJSONObject(CARD));
        disclosure = new OBDisclosure(jsonObject.optJSONObject(DISCLOSURE));
        logo = new OBThumbnail(jsonObject.optJSONObject(LOGO));
        JSONObject publisherAds = jsonObject.optJSONObject(PUBLISHER_ADS);
        if (publisherAds != null && publisherAds.optBoolean(IS_PUBLISHER_ADS)) {
            publisherAdsLabel = publisherAds.optString(PUBLISHER_ADS_LABEL);
        }
        position = jsonObject.optString(POSITION, "0");
        ctaText = jsonObject.optString(CTA);
        reqId = jsonObject.optString(REQ_ID);
    }

    private void parsePixels(JSONArray jsonArray) {
        if (jsonArray != null) {
            pixels = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                pixels[i] = jsonArray.optString(i);
            }
        }
    }

    private void parseCardCategoryIfNeeded(JSONObject jsonObj) {
        if (jsonObj != null) {
            categoryName = jsonObj.optString("contextual_topic");
        }
    }


    private Date getDate(JSONObject jsonObject) {
        String sDate = jsonObject.optString(PUBLISH_DATE);
        if (sDate.equals("")) {
            return null;
        }
        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        try {
            return sdf.parse(sDate);
        } catch (ParseException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getPrivateUrl() {
        return url;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getReqID() {
        return reqId;
    }
}
