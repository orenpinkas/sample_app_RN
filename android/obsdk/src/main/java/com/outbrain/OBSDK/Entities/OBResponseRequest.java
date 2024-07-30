package com.outbrain.OBSDK.Entities;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.Serializable;

public class OBResponseRequest extends OBBaseEntity implements Serializable {
    private static final String IDX = "idx";
    private static final String LANG = "lang";
    private static final String PUBLISHERID = "pid";
    private static final String DID = "did";
    private static final String WIDGETJSID = "widgetJsId";
    private static final String REQID = "req_id";
    private static final String TOKEN = "t";
    private static final String OPTED_OUT = "oo";
    private static final String SRCID = "sid";
    private static final String WIDGETID = "wnid";
    private static final String PAGEVIEWID = "pvId";
    private static final String ORGANICREC = "org";
    private static final String PAIDREC = "pad";
    private static final String VID = "vid";
    private static final String AB_TEST_VAL = "abTestVal";

    private JSONObject OBResponseRequestJSONObject;


    public String getIdx() {
        return idx;
    }

    private String idx;

    public String getLang() {
        return lang;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getDid() {
        return did;
    }

    public String getWidgetJsId() {
        return widgetJsId;
    }

    public String getReqId() {
        return reqId;
    }

    public String getToken() { return token; }

    public String getSourceId() { return sourceId; }

    public String getWidgetId() { return widgetId; }

    public String getPageviewId() { return pageviewId; }

    public String getOrganicRec() { return organicRec; }

    public String getPaidRec() { return paidRec; }

    public boolean isVideo() {
        return ("1").equals(vid);
    }

    public String getAbTestVal() {
        return abTestVal;
    }

    public boolean isOptedOut() {
        return optedOut;
    }

    public JSONObject getJSONObject() {
        return OBResponseRequestJSONObject;
    }

    private String lang;
    private String publisherId;
    private String did;
    private String widgetJsId;
    private String reqId;
    private String token;
    private String sourceId;
    private String widgetId;
    private String pageviewId;
    private String organicRec;
    private String paidRec;
    private String vid;
    private String abTestVal;
    private boolean optedOut;



    public OBResponseRequest(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }
        OBResponseRequestJSONObject = jsonObject;
        idx = jsonObject.optString(IDX);
        lang = jsonObject.optString(LANG);
        publisherId = jsonObject.optString(PUBLISHERID);
        did = jsonObject.optString(DID);
        widgetJsId = jsonObject.optString(WIDGETJSID);
        reqId = jsonObject.optString(REQID);
        token = jsonObject.optString(TOKEN);
        sourceId = jsonObject.optString(SRCID);
        widgetId = jsonObject.optString(WIDGETID);
        pageviewId = jsonObject.optString(PAGEVIEWID);
        organicRec = jsonObject.optString(ORGANICREC);
        paidRec = jsonObject.optString(PAIDREC);
        vid = jsonObject.optString(VID);
        abTestVal = jsonObject.optString(AB_TEST_VAL);
        if (abTestVal.equals("no_abtest")) {
            abTestVal = null;
        }
        optedOut = jsonObject.optBoolean(OPTED_OUT, false);
    }

    @NotNull
    @Override
    public String toString() {
        return "OBResponseRequest - idx: " + idx + ", lang: " + lang + "publisherId: " + publisherId + ", did: " + did +
                ", widgetJsId: " + widgetJsId + ", reqId: " + reqId +
                ", token: " + token + ", sourceId: " + sourceId +
                ", widgetId: " + widgetId + ", pageviewId: " + pageviewId +
                ", organicRec: " + organicRec + ", paidRec: " + paidRec + ", abTestVal: " + abTestVal;
    }
}
