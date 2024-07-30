package com.outbrain.OBSDK.Utilities;

import android.util.Log;

import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.OBUtils;

import java.util.HashMap;

/**
 * Created by odedre on 5/22/17.
 */

public class RecommendationApvHandler {

    private static final HashMap<String, Boolean> apvCache = new HashMap<>();

    // APV is a flag for the server (mostly BI) to identify pageviews which contain an Outbrain widget with **paid** ads
    // So on the first call to ODB (when idx = 0) we tell the server apv=false, i.e. there are no paid ads on this page (yet)..
    // Than the server returns response with either apv=true in the settings, which means there are paid ads in the response,
    // or "false" to identify the other case.
    // In subsequent calls to ODB for the same page (url) in the same session, we want tell the server if this url already received paid recs
    // again, for BI purposes. The client is sort of making the work for the server to reduce processing time in heavy BI queries.
    public static boolean getApvForRequest(OBRequest request) {
        String requestUrlString = OBUtils.getUrlFromOBRequest(request);
        if (request.getIdx() == 0) {
            apvCache.put(requestUrlString, false);
        }

        // Log.i("OBSDK", "getApvForRequest - apv = " + apvCache.get(request.getUrl()) + " for url: " + request.getUrl());
        if (apvCache.get(requestUrlString) == null) {
            return false;
        }
        return apvCache.get(requestUrlString);
    }

    public static void updateAPVCacheForResponse(OBSettings settings, OBRequest request ) {
        String requestUrlString = OBUtils.getUrlFromOBRequest(request);

        // https://stackoverflow.com/a/13687403/583425
        Boolean apv = apvCache.get(requestUrlString);
        if (apv != null && apv) {
            // we are not interested in the apv value in the response since this URL has already been marked for this session.
            return;
        }

        if (requestUrlString == null) { // sanity
            Log.e("OBSDK", "updateAPVCacheForResponse - url is null...");
            return;
        }

        if (settings.getApv()) {
            // Log.i("OBSDK", "updateAPVCacheForResponse - setting apv to true - " + request.getUrl());
            apvCache.put(requestUrlString, true);
        }
        // else.. If apv is false we don't want to set anything
    }

    public static int getApvCacheSize() {
        return apvCache.size();
    }

    public static void clearCache() {
        apvCache.clear();
    }
}
