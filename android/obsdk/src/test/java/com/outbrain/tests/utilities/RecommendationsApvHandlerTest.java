package com.outbrain.tests.utilities;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.OBPlatformRequest;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.Utilities.RecommendationApvHandler;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rabraham on 21/06/2017.
 */

public class RecommendationsApvHandlerTest extends TestCase {

    private static final String OBDemoWidgetID1 = "SDK_1";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";
    private final static String OUTBRAIN_SAMPLE_BUNDLE_URL = "https://play.google.com/store/apps/details?id=com.outbrain";
    private static final int idx = 1;

    private RecommendationsTokenHandler recommendationsTokenHandler;
    private OBRequest request;
    private OBPlatformRequest platformRequest;
    private OBRecommendationsResponse response;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RecommendationApvHandler.clearCache();
        String jsonString = TestsUtils.readJsonFromFile("odb_response_base.json");
        request = new OBRequest(OBDemoUrl, idx, OBDemoWidgetID1);
        platformRequest = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
        platformRequest.setWidgetIndex(idx);
        response = OBRecommendationsParser.parse(jsonString, request);
    }

    public final void testUpdateApvForResponseForNewRequest() {
        assertTrue(RecommendationApvHandler.getApvCacheSize() == 0);
        assertFalse(RecommendationApvHandler.getApvForRequest(request));
        RecommendationApvHandler.updateAPVCacheForResponse(response.getSettings(), request);
        RecommendationApvHandler.getApvForRequest(request);
        assertTrue(RecommendationApvHandler.getApvForRequest(request));
    }

    public final void testUpdateApvFalseForResponseForNewRequest() throws JSONException {
        assertTrue(RecommendationApvHandler.getApvCacheSize() == 0);
        JSONObject jsonObject = response.getSettings().getJSONObject();
        jsonObject.put("apv", false);
        OBSettings settings = new OBSettings(jsonObject);
        assertFalse(RecommendationApvHandler.getApvForRequest(request));
        RecommendationApvHandler.updateAPVCacheForResponse(settings, request);
        RecommendationApvHandler.getApvForRequest(request);
        assertFalse(RecommendationApvHandler.getApvForRequest(request));
    }

    public final void testUpdateApvForResponseForNewPlatformRequest() {
        assertTrue(RecommendationApvHandler.getApvCacheSize() == 0);
        assertFalse(RecommendationApvHandler.getApvForRequest(platformRequest));
        RecommendationApvHandler.updateAPVCacheForResponse(response.getSettings(), platformRequest);
        RecommendationApvHandler.getApvForRequest(platformRequest);
        assertTrue(RecommendationApvHandler.getApvForRequest(platformRequest));
    }

    public final void testUpdateApvFalseForResponseForNewPlatformRequest() throws JSONException {
        assertTrue(RecommendationApvHandler.getApvCacheSize() == 0);
        JSONObject jsonObject = response.getSettings().getJSONObject();
        jsonObject.put("apv", false);
        OBSettings settings = new OBSettings(jsonObject);
        assertFalse(RecommendationApvHandler.getApvForRequest(platformRequest));
        RecommendationApvHandler.updateAPVCacheForResponse(settings, platformRequest);
        RecommendationApvHandler.getApvForRequest(platformRequest);
        assertFalse(RecommendationApvHandler.getApvForRequest(platformRequest));
    }


}
