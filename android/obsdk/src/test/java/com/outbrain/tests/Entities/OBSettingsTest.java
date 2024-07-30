package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.ObjectSerializer;

import junit.framework.TestCase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class OBSettingsTest extends TestCase {

    public final void testBasicSettingsParsing() {
        try {
            String jsonString= readJsonFromFile("settings_base.json");
            System.out.println("--> " + jsonString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
            System.out.println("--> jsonObject: " + jsonObject);
            OBSettings settings = new OBSettings(jsonObject.optJSONObject("settings"));
            assertEquals(settings.getApv(), true);
            assertEquals(settings.isViewabilityEnabled(), true);
            assertEquals(settings.viewabilityThreshold(), 1000);
            assertEquals(settings.getVideoUrl(), "https://libs.outbrain.com/video/app/vidgetInApp.html");
            assertFalse(settings.isSmartFeed());
            assertNull(settings.getFeedContentList());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public final void testMixedSettingsParsing() {
        try {
            String jsonString= readJsonFromFile("settings_mixed.json");
            System.out.println("--> " + jsonString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
            System.out.println("--> jsonObject: " + jsonObject);
            OBSettings settings = new OBSettings(jsonObject.optJSONObject("settings"));
            assertFalse(settings.getApv());
            assertFalse(settings.isViewabilityEnabled());
            assertEquals(settings.viewabilityThreshold(), 2000);
            assertTrue(settings.shouldShowCtaButton());
            assertEquals(18, settings.getSmartfeedHeaderFontSize());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public final void testSerialize() {
        try {
            String jsonString= readJsonFromFile("settings_mixed.json");
            System.out.println("--> " + jsonString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
            System.out.println("--> jsonObject: " + jsonObject);

            OBSettings s1 = new OBSettings(jsonObject.optJSONObject("settings"));
            String bytes = ObjectSerializer.serialize(s1);
            OBSettings s2 = (OBSettings) ObjectSerializer.deserialize(bytes);



            assertEquals(s1.getApv(), s2.getApv());
            assertEquals(s1.isViewabilityEnabled(), s2.isViewabilityEnabled());
            assertEquals(s1.viewabilityThreshold(), s2.viewabilityThreshold());
            assertEquals(s1.getApv(), s2.getApv());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private String readJsonFromFile(String fileName) throws Exception {
        JSONParser parser = new JSONParser();
        String filePath = this.getClass().getClassLoader().getResource(fileName).getFile();
        System.out.println("--> " + filePath);
        org.json.simple.JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
        return jsonObject.toString();
    }
}