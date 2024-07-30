package com.outbrain.OBSDK.FetchRecommendations;

import com.outbrain.OBSDK.Entities.OBError;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Errors.OBErrorReporting;

import org.json.JSONException;
import org.json.JSONObject;

//This class takes care of all parsing of the recommendations response. It returns OBRecommendationsResponse if parsing succeeded.
public class OBRecommendationsParser {
    public static OBRecommendationsResponse parse(String result, OBRequest request) throws JSONException {
        JSONObject jsonResponse = new JSONObject(result);
        return new OBRecommendationsResponse(jsonResponse.optJSONObject("response"), request);
    }

    public static OBError parseError(String result) {
        try {
            result = quickFix(result);
            JSONObject jsonResponse = new JSONObject(result);
            JSONObject errorJson = jsonResponse.optJSONObject("response");
            return new OBError(errorJson);
        }
        catch (JSONException ex) {
            OBErrorReporting.getInstance().reportErrorToServer(ex.getLocalizedMessage());
            return null;
        }
    }

    //TODO: REMOVE THIS!!!!
    private static String quickFix(String st) {
        st = st.replace("outbrain.returnedError(", "");
        st = st.replace("})", "}");
        return st;
    }
}
