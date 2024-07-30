package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

public class OBBrandedItemSettings extends OBBaseEntity {
    private static final String CONTENT = "content";
    private static final String CAROUSEL_SPONSOR = "carouselSponsor";
    private static final String CAROUSEL_TYPE = "carousel_type";
    private static final String URL = "url";
    private static final String THUMBNAIL = "thumbnail";

    private String content;
    private String sponsor;
    private String type;
    private String url;
    private OBThumbnail thumbnail;

    public OBBrandedItemSettings(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }

        this.content = jsonObject.optString(CONTENT);
        this.sponsor = jsonObject.optString(CAROUSEL_SPONSOR);
        this.type = jsonObject.optString(CAROUSEL_TYPE);
        this.url = jsonObject.optString(URL);
        this.thumbnail = new OBThumbnail(jsonObject.optJSONObject(THUMBNAIL));
    }

    public String getContent() {
        return content;
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public OBThumbnail getThumbnail() {
        return thumbnail;
    }
}