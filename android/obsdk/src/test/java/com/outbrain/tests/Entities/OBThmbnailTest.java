package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.Entities.OBThumbnail;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

/**
 * Created by rabraham on 18/06/2017.
 */

public class OBThmbnailTest extends TestCase {

    private JSONObject thumbnailJson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String thumbnailJsonStr = TestsUtils.readJsonFromFile("thumbnail_response.json");
       thumbnailJson = new JSONObject(thumbnailJsonStr);
    }

    public final void testThumbnailParsing() {
        OBThumbnail thumbnail = new OBThumbnail(thumbnailJson.optJSONObject("thumbnail"));
        assertEquals(thumbnailJson.optJSONObject("thumbnail").optString("url"), thumbnail.getUrl());
        assertEquals(thumbnailJson.optJSONObject("thumbnail").optInt("width"), thumbnail.getWidth());
        assertEquals(thumbnailJson.optJSONObject("thumbnail").optInt("height"), thumbnail.getHeight());
    }

    public void testThumbnailPArsingIfJsonNIsNull() {
        OBThumbnail thumbnail = new OBThumbnail(null);
        assertNull(thumbnail.getUrl());
        assertEquals(0, thumbnail.getWidth());
        assertEquals(0, thumbnail.getHeight());
    }
}
