package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.FetchRecommendations.OBPlatformRequest;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.OBUtils;

import junit.framework.TestCase;

public class OBPlatformRequestTests extends TestCase {

    private static final String OBDemoWidgetID1 = "SDK_1";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";
    private final static String OUTBRAIN_SAMPLE_BUNDLE_URL = "https://play.google.com/store/apps/details?id=com.outbrain";
    private final static String OUTBRAIN_SAMPLE_PORTAL_URL = "https://lp.outbrain.com/increase-sales-native-ads/";

    public final void testPlatformRequestWithBundleUrl() {
        OBPlatformRequest platformRequest = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
        assertEquals(OUTBRAIN_SAMPLE_BUNDLE_URL, platformRequest.getBundleUrl());
        assertEquals("en", platformRequest.getLang());
        assertNull(platformRequest.getPortalUrl());
        assertNull(platformRequest.getUrl());
    }

    public final void testPlatformRequestWithPortalUrl() {
        OBPlatformRequest platformRequest = new OBPlatformRequest(OBDemoWidgetID1, null, OUTBRAIN_SAMPLE_PORTAL_URL, "en");
        assertEquals(OUTBRAIN_SAMPLE_PORTAL_URL, platformRequest.getPortalUrl());
        assertEquals("en", platformRequest.getLang());
        assertNull(platformRequest.getBundleUrl());
        assertNull(platformRequest.getUrl());
    }

    public final void testClassisRequest() {
        OBRequest obRequest = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        assertEquals(OBDemoUrl, obRequest.getUrl());
    }

    public final void testGetUrlFromOBRequest() {
        OBPlatformRequest platformRequestWithPortal = new OBPlatformRequest(OBDemoWidgetID1, null, OUTBRAIN_SAMPLE_PORTAL_URL, "en");
        OBPlatformRequest platformRequestWithBundle = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
        OBRequest obRequest = new OBRequest(OBDemoUrl, OBDemoWidgetID1);

        assertEquals(OUTBRAIN_SAMPLE_PORTAL_URL, OBUtils.getUrlFromOBRequest(platformRequestWithPortal));
        assertEquals(OUTBRAIN_SAMPLE_BUNDLE_URL, OBUtils.getUrlFromOBRequest(platformRequestWithBundle));
        assertEquals(OBDemoUrl, OBUtils.getUrlFromOBRequest(obRequest));

    }
}