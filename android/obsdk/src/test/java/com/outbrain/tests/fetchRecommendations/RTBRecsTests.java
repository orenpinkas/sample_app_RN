package com.outbrain.tests.fetchRecommendations;

import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

public class RTBRecsTests extends TestCase {


    public final void testNormalRTBRecommendation() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rtb_rec_with_disclosure.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendationImpl rec = new OBRecommendationImpl(recommendationJson);

        assertEquals(rec.getSourceName(), "Zoom"); // sanity
        assertTrue(rec.isRTB());
        assertTrue(rec.shouldDisplayDisclosureIcon());
        assertNotNull(rec.getDisclosure());
        assertNotNull(rec.getDisclosure().getClickUrl());
        assertNotNull(rec.getDisclosure().getIconUrl());
        assertNotNull(rec.getPixels());
    }

    public final void testNormalNonRTBRecommendation() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("non_rtb_rec.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendationImpl rec = new OBRecommendationImpl(recommendationJson);

        assertEquals(rec.getSourceName(), "From the Grapevine"); // sanity
        assertFalse(rec.isRTB());
        assertFalse(rec.shouldDisplayDisclosureIcon());
        assertNotNull(rec.getDisclosure());
        assertNull(rec.getDisclosure().getIconUrl());
        assertNull(rec.getDisclosure().getClickUrl());
        assertNull(rec.getPixels());
    }

    public final void testNormalRTBRecommendationNoClickUrl() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rtb_rec_with_no_disclosure_click_url.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendationImpl rec = new OBRecommendationImpl(recommendationJson);

        assertEquals(rec.getSourceName(), "Zoom"); // sanity
        assertFalse(rec.isRTB());
        assertFalse(rec.shouldDisplayDisclosureIcon());
        assertNotNull(rec.getDisclosure());
    }

    public final void testNormalRTBRecommendationWithEmptyDisclosureValues() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rtb_rec_with_disclosure_empty_values.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendationImpl rec = new OBRecommendationImpl(recommendationJson);

        assertEquals(rec.getSourceName(), "Zoom"); // sanity
        assertFalse(rec.isRTB());
        assertFalse(rec.shouldDisplayDisclosureIcon());
        assertNotNull(rec.getDisclosure());
    }

    public final void testNormalRTBRecommendationNoDiscImage() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("rtb_rec_with_no_disclosure_image.json");
        JSONObject recommendationJson = new JSONObject(jsonString);
        assertNotNull(recommendationJson);
        OBRecommendationImpl rec = new OBRecommendationImpl(recommendationJson);

        assertEquals(rec.getSourceName(), "Zoom"); // sanity
        assertFalse(rec.isRTB());
        assertFalse(rec.shouldDisplayDisclosureIcon());
        assertNotNull(rec.getDisclosure());
    }
}
