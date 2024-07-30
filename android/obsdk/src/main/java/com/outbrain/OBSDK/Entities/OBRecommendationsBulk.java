package com.outbrain.OBSDK.Entities;

import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

class OBRecommendationsBulk extends OBBaseEntity implements Serializable {
    private static final String DOC = "doc";

    public ArrayList<OBRecommendation> getDocs() {
        return docs;
    }

    private final ArrayList<OBRecommendation> docs;

    private boolean verifyMandatoryFields(OBRecommendationImpl rec) {
        return (rec.getContent() != null && rec.getContent().length() != 0) && rec.getUrl() != null;
    }

    public OBRecommendationsBulk(JSONObject jsonObject, String reqId) {
        super(jsonObject);
        docs = new ArrayList<>();
        if (jsonObject == null) {
            return;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(DOC);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = (JSONObject)jsonArray.get(i);
                jsonObj.put("reqId", reqId);
                OBRecommendationImpl document = new OBRecommendationImpl(jsonObj);
                if (verifyMandatoryFields(document)) {
                    docs.add(document);
                }
            }
        } catch (JSONException e) {
            OBErrorReporting.getInstance().reportErrorToServer(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "OBRecommendationsBulk: " + docs;
    }
}
