package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by rabraham on 25/07/2017.
 */

public class OBDisclosure extends OBBaseEntity implements Serializable{

    private static final String ICON = "icon";
    private static final String URL = "url";

    private String iconUrl;
    private String clickUrl;


    public OBDisclosure(JSONObject jsonObject) {
        super(jsonObject);

        if (jsonObject == null) {
            return;
        }

        String iconUrlStr = jsonObject.optString(ICON);
        if (iconUrlStr != null && iconUrlStr.length() > 0) {
            iconUrl = iconUrlStr;
        }

        String clickUrlStr = jsonObject.optString(URL);
        if (clickUrlStr != null && clickUrlStr.length() > 0) {
            clickUrl = clickUrlStr;
        }
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
