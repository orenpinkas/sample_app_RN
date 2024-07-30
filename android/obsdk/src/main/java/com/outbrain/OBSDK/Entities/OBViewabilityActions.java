package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

public class OBViewabilityActions extends OBBaseEntity {
    private static final String REPORT_SERVED = "reportServed";
    private String reportServedUrl = "";

    public OBViewabilityActions(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }
        this.reportServedUrl = jsonObject.optString(REPORT_SERVED);
    }

    public String getReportServedUrl() {
        return reportServedUrl;
    }

}
