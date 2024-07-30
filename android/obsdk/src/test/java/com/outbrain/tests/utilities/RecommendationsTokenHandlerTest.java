package com.outbrain.tests.utilities;

import com.outbrain.OBSDK.Entities.OBOperation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBPlatformRequest;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by rabraham on 21/06/2017.
 */

public class RecommendationsTokenHandlerTest extends TestCase {

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
        recommendationsTokenHandler = new RecommendationsTokenHandler();
        String jsonString = TestsUtils.readJsonFromFile("odb_response_base.json");
        request = new OBRequest(OBDemoUrl, idx, OBDemoWidgetID1);
        platformRequest = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
        platformRequest.setWidgetIndex(idx);
        response = OBRecommendationsParser.parse(jsonString, request);
    }

    public final void testSetTokenForResponseForNewRequest() {
        OBOperation operation = new OBOperation(request, response);
        assertNull(recommendationsTokenHandler.getTokenForRequest(request));
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(request));
        assertEquals(response.getRequest().getToken(), "MV85ZGQyOGUwY2M1MTVmNWMyMDRkMTdkMDg3ZTBhMGVlOV8w");
    }

    public final void testSetTokenForResponseForPlatformsRequest() {
        OBOperation operation = new OBOperation(platformRequest, response);
        assertNull(recommendationsTokenHandler.getTokenForRequest(platformRequest));
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(platformRequest));
        assertEquals(response.getRequest().getToken(), "MV85ZGQyOGUwY2M1MTVmNWMyMDRkMTdkMDg3ZTBhMGVlOV8w");
    }

    public final void testSetTokenForResponseForExistingRequest() throws Exception {
        OBOperation operation = new OBOperation(request, response);
        assertNull(recommendationsTokenHandler.getTokenForRequest(request));
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(request));

        Field field = RecommendationsTokenHandler.class.getDeclaredField("tokensMap");
        field.setAccessible(true);
        recommendationsTokenHandler.setTokenForResponse(operation);

        @SuppressWarnings("unchecked")
        HashMap<String, String> tokensMap = (HashMap<String, String>) field.get(recommendationsTokenHandler);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(request));
        assertEquals(1, tokensMap.size());
    }

    public final void testSetTokenForResponseForExistingPlatformRequest() throws Exception {
        OBOperation operation = new OBOperation(platformRequest, response);
        assertNull(recommendationsTokenHandler.getTokenForRequest(platformRequest));
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(platformRequest));

        Field field = RecommendationsTokenHandler.class.getDeclaredField("tokensMap");
        field.setAccessible(true);
        recommendationsTokenHandler.setTokenForResponse(operation);

        @SuppressWarnings("unchecked")
        HashMap<String, String> tokensMap = (HashMap<String, String>) field.get(recommendationsTokenHandler);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(platformRequest));
        assertEquals(1, tokensMap.size());
    }

    public final void testTextGetTokenForRequestIfIdxIsZero() throws Exception{
        String jsonString = TestsUtils.readJsonFromFile("odb_response_base.json");
        request = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        response = OBRecommendationsParser.parse(jsonString, request);

        OBOperation operation = new OBOperation(request, response);
        recommendationsTokenHandler.setTokenForResponse(operation);

        assertNull(recommendationsTokenHandler.getTokenForRequest(request));
    }

    public final void testTextGetTokenForPlatformRequestIfIdxIsZero() throws Exception{
        String jsonString = TestsUtils.readJsonFromFile("odb_response_base.json");
        OBPlatformRequest testPlatformRequest = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
        testPlatformRequest.setWidgetIndex(0);
        response = OBRecommendationsParser.parse(jsonString, testPlatformRequest);

        OBOperation operation = new OBOperation(testPlatformRequest, response);
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertNull(recommendationsTokenHandler.getTokenForRequest(testPlatformRequest));
    }

    public final void testTextGetTokenForRequestIfUrlDoesNotExist() {
        OBOperation operation = new OBOperation(request, response);
        recommendationsTokenHandler.setTokenForResponse(operation);

        OBRequest newRequest = new OBRequest(OBDemoUrl + "not exist", OBDemoWidgetID1);
        assertNull(recommendationsTokenHandler.getTokenForRequest(newRequest));
    }

    public final void testTextGetTokenForRequest() {
        OBOperation operation = new OBOperation(request, response);
        recommendationsTokenHandler.setTokenForResponse(operation);
        assertEquals(response.getRequest().getToken(), recommendationsTokenHandler.getTokenForRequest(request));
    }
}
