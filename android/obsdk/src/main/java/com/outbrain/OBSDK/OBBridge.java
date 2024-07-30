package com.outbrain.OBSDK;

import android.content.Context;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.Errors.OBErrorReporting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by odedre on 8/10/16.
 */
public class OBBridge {

    public static boolean isOutbrainPaidUrl(String url) {
        return url.contains("paid.outbrain.com/network/redir");
    }

    public static boolean shouldOpenInCustomTabs(String url) {
        return url.contains("cwvShouldOpenInExternalBrowser=true");
    }

    public static boolean registerOutbrainResponse(Context ctx, JSONObject jsonResponse) {

        try {
            if (jsonResponse.optJSONObject("response").optJSONObject("documents").optJSONArray("doc") == null) {
                return false;
            }
        }
        catch (NullPointerException ex) {
            Log.e("OBSDK", "registerOutbrainResponse() - ODB response json seems to be illegal");
            OBErrorReporting.getInstance().reportErrorToServer("registerOutbrainResponse() - ODB response json seems to be illegal");

            return false;
        }

        try {
            OBSettings settings = new OBSettings(jsonResponse.optJSONObject("response").getJSONObject("settings"));
            return true;
        } catch (JSONException e) {
            Log.e("OBSDK", "Error parsing settings from jsonResponse");
        }

        return false;
    }

    public static void registerWithPartnerKey(Context applicationContext, String appKey) {
        Outbrain.register(applicationContext, appKey);
    }
}
