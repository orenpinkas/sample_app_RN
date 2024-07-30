package com.outbrain.tests.fetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Entities.OBResponseRequest;
import com.outbrain.OBSDK.Entities.OBResponseStatus;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by rabraham on 27/06/2017.
 */

public class SmartFeedResponseBasicTest extends TestCase {

    private static final String OBDemoWidgetID1 = "SFD_MAIN_1";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";
    private OBRecommendationsResponse response;
    private JSONObject responseJson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String jsonString = TestsUtils.readJsonFromFile("smart_feed_response_1.json");
        responseJson = new JSONObject(jsonString).getJSONObject("response");
        OBRequest request = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        response = OBRecommendationsParser.parse(jsonString, request);
    }

    public final void testOBResponseStatusParsing() {
        try {
            // test OBResponseStatus
            Field field = OBRecommendationsResponse.class.getDeclaredField("status");
            field.setAccessible(true);
            OBResponseStatus status = (OBResponseStatus) field.get(response);
            assertEquals(status.getStatusId(), 0);
            assertEquals(status.getContent(), "Request succeeded");

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public final void testOBRecommendationsBulkParsing() {
        try {
            ArrayList<OBRecommendation> docs = response.getAll();
            JSONObject documentsJson = responseJson.getJSONObject("documents");
            assertEquals(docs.size(), documentsJson.getInt("count"));

            JSONArray docsJsonArray = documentsJson.getJSONArray("doc");
            for (int i = 0; i < docsJsonArray.length(); i++) {
                OBRecommendation rec = docs.get(i);
                assertEquals(rec.getSourceName(), docsJsonArray.getJSONObject(i).getString("source_name"));

                if (rec.isPaid()) {
                    assertEquals(rec.getPaidContentId(), String.valueOf(docsJsonArray.getJSONObject(i).getLong("pc_id")));
                } else {
                    assertFalse(docsJsonArray.getJSONObject(i).has("pc_id"));
                }

                assertEquals(rec.getContent(), docsJsonArray.getJSONObject(i).getString("content"));
                assertEquals(rec.getThumbnail().getUrl(), docsJsonArray.getJSONObject(i).getJSONObject("thumbnail").getString("url"));
                assertTrue(((OBRecommendationImpl) rec).getUrl().contains(docsJsonArray.getJSONObject(i).getString("url")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public final void testOBRecommendationsBulkParsingWithNullJson() {
        OBRecommendationsResponse obRecommendationsResponse = new OBRecommendationsResponse(null, null);
        assertNotNull(obRecommendationsResponse);
        assertNull(obRecommendationsResponse.getAll());
    }

    public final void testOBRequest() {
        try {
            OBRequest request = response.getObRequest();

            assertEquals(request.getUrl(), OBDemoUrl);
            assertEquals(request.getWidgetId(), OBDemoWidgetID1);
            assertEquals(request.getIdx(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public final void testOBRecommendationsResponseParsing() {
        try {
            // test OBResponseRequest
            Field field = OBRecommendationsResponse.class.getDeclaredField("request");
            field.setAccessible(true);
            OBResponseRequest resRequest = (OBResponseRequest) field.get(response);
            JSONObject requestJson = responseJson.getJSONObject("request");
            assertEquals(resRequest.getIdx(), requestJson.getString("idx"));
            assertEquals(resRequest.getWidgetJsId(), requestJson.getString("widgetJsId"));
            assertEquals(resRequest.getWidgetId(), String.valueOf(requestJson.getInt("wnid")));
            assertEquals(resRequest.getDid(), requestJson.getString("did"));
            assertEquals(resRequest.getLang(), requestJson.getString("lang"));
            assertEquals(resRequest.getOrganicRec(), requestJson.getString("org"));
            assertEquals(resRequest.getPageviewId(), requestJson.getString("req_id"));
            assertEquals(resRequest.getPaidRec(), requestJson.getString("pad"));
            assertEquals(resRequest.getPublisherId(), requestJson.getString("pid"));
            assertEquals(resRequest.getSourceId(), String.valueOf(requestJson.getLong("sid")));
            assertEquals(resRequest.getToken(), requestJson.getString("t"));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
