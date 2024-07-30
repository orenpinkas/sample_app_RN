package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.Entities.OBThumbnail;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by rabraham on 27/06/2017.
 */

public class OBRecommendationTest extends TestCase {

    private JSONObject responseJson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String jsonString = TestsUtils.readJsonFromFile("obrecommendation_response.json");
        responseJson = new JSONObject(jsonString);
    }

    public final void testConstructor() throws Exception{
        OBRecommendationImpl recommendation = new OBRecommendationImpl(responseJson);

        Field field = OBRecommendationImpl.class.getDeclaredField("origUrl");
        field.setAccessible(true);
        String origUrl = (String) field.get(recommendation);
        assertEquals(responseJson.getString("orig_url"), origUrl);

        assertEquals(responseJson.getString("source_name"), recommendation.getSourceName());
        assertEquals(Boolean.valueOf(responseJson.getString("same_source")).booleanValue(), recommendation.isSameSource());

        field = OBRecommendationImpl.class.getDeclaredField("pcId");
        field.setAccessible(true);
        String pcId = (String) field.get(recommendation);
        assertEquals(String.valueOf(responseJson.getLong("pc_id")), pcId);


        assertEquals(responseJson.getString("adv_name"), recommendation.getAdvertiserName());

        Method getDateMethod = OBRecommendationImpl.class.getDeclaredMethod("getDate", JSONObject.class);
        getDateMethod.setAccessible(true);
        assertEquals(getDateMethod.invoke(recommendation, responseJson), recommendation.getPublishDate());

        assertEquals(responseJson.getString("url"), recommendation.getUrl());
        assertEquals(responseJson.getString("author"), recommendation.getAuthor());
        assertEquals(responseJson.getString("content"), recommendation.getContent());
        assertEquals(new OBThumbnail(responseJson.getJSONObject("thumbnail")), recommendation.getThumbnail());
    }

}
