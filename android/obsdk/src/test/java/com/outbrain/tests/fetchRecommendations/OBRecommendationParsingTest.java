package com.outbrain.tests.fetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

public class OBRecommendationParsingTest extends TestCase {

    public final void testDecodeHTMLEncodedString() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rec_with_html_encoded_title_1.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendation rec = new OBRecommendationImpl(recommendationJson);
        assertEquals(rec.getContent(), "Netflix’s New Trailer for ‘Nailed It! Holiday’ Is Proof Your Cooking Skills Could Be Worse");

        jsonString = TestsUtils.readJsonFromFile("rec_with_html_encoded_title_2.json");
        recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        rec = new OBRecommendationImpl(recommendationJson);
        assertEquals(rec.getContent(), "Roger Federer's tears for former coach: 'Never broke down like this'");

        jsonString = TestsUtils.readJsonFromFile("rec_with_html_encoded_title_3.json");
        recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        rec = new OBRecommendationImpl(recommendationJson);
        assertEquals(rec.getContent(), "If you can't beat the robot, make them");
    }

    public final void testGetDescriptionOnRecWhenAvailable() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rec_with_html_encoded_title_1.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendation rec = new OBRecommendationImpl(recommendationJson);
        assertEquals("Acer, Aspire, A515-51G-C97B, 15,6\", 1,6 GHz, 8GB, 1 TB, Windows 10, Intel Core i5 8250U...", rec.getDescription());
    }

    public final void testGetDescriptionOnRecWhenMissing() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rec_with_html_encoded_title_2.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendation rec = new OBRecommendationImpl(recommendationJson);
        assertNull(rec.getDescription());
    }
}