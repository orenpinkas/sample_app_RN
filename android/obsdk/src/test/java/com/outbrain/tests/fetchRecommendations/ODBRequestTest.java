package com.outbrain.tests.fetchRecommendations;



import androidx.test.core.app.ApplicationProvider;

import com.outbrain.OBSDK.Entities.OBLocalSettings;

import com.outbrain.OBSDK.FetchRecommendations.OBPlatformRequest;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsUrlBuilder;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;


import java.net.URLDecoder;

import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class ODBRequestTest {

    private static final String OBDemoWidgetID1 = "SDK_1";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";
    private final static String OUTBRAIN_SAMPLE_BUNDLE_URL = "https://play.google.com/store/apps/details?id=com.outbrain";
    private final static String OUTBRAIN_SAMPLE_PORTAL_URL = "https://lp.outbrain.com/increase-sales-native-ads/";

    private final CountDownLatch lock = new CountDownLatch(1);

    @Test
    public final void testRequestUrlBuilderForClassicRequest() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OBRequest obRequest = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
                    OBLocalSettings localSettings = new OBLocalSettings();
                    localSettings.setTestMode(false);
                    localSettings.partnerKey = "TEST_PARTNER_KEY";
                    RecommendationsUrlBuilder recommendationsUrlBuilder = new RecommendationsUrlBuilder(localSettings, new RecommendationsTokenHandler());
                    String url = recommendationsUrlBuilder.getUrl(ApplicationProvider.getApplicationContext(), obRequest);
                    System.out.println("url: " + url);
                    Map<String, String> paramsMap = getQueryParams(url);
                    assertTrue(url.contains("https://odb.outbrain.com/utils/get?"));
                    assertEquals("vjnc", paramsMap.get("format"));
                    assertEquals("TEST_PARTNER_KEY", paramsMap.get("key"));
                    assertEquals(OBDemoUrl, paramsMap.get("url"));
                    assertEquals("true", paramsMap.get("va"));
                    assertEquals("true", paramsMap.get("secured"));
                    assertEquals("true", paramsMap.get("rtbEnabled"));
                    assertNull(paramsMap.get("lang"));
                    assertNull(paramsMap.get("bundleUrl"));
                    assertNull(paramsMap.get("portalUrl"));
                    lock.countDown();
                }
            }).start();
            lock.await(20000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public final void testRequestUrlBuilderForPlatformPortalRequest() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OBPlatformRequest platformRequest = new OBPlatformRequest(OBDemoWidgetID1, null, OUTBRAIN_SAMPLE_PORTAL_URL, "en");
                    platformRequest.setPsub("sports");
                    OBLocalSettings localSettings = new OBLocalSettings();
                    localSettings.setTestMode(false);
                    localSettings.partnerKey = "TEST_PARTNER_KEY";
                    RecommendationsUrlBuilder recommendationsUrlBuilder = new RecommendationsUrlBuilder(localSettings, new RecommendationsTokenHandler());
                    String url = recommendationsUrlBuilder.getUrl(ApplicationProvider.getApplicationContext(), platformRequest);
                    System.out.println("url: " + url);
                    Map<String, String> paramsMap = getQueryParams(url);
                    assertTrue(url.contains("https://odb.outbrain.com/utils/platforms?"));
                    assertEquals("vjnc", paramsMap.get("format"));
                    assertEquals("TEST_PARTNER_KEY", paramsMap.get("key"));
                    assertEquals(OUTBRAIN_SAMPLE_PORTAL_URL, paramsMap.get("portalUrl"));
                    assertEquals("en", paramsMap.get("lang"));
                    assertEquals("sports", paramsMap.get("psub"));
                    assertEquals("true", paramsMap.get("va"));
                    assertEquals("true", paramsMap.get("secured"));
                    assertEquals("true", paramsMap.get("rtbEnabled"));
                    assertNull(paramsMap.get("url"));
                    assertNull(paramsMap.get("bundleUrl"));
                    lock.countDown();
                }
            }).start();
            lock.await(20000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public final void testRequestUrlBuilderForPlatformBundleRequest() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OBPlatformRequest platformRequest = new OBPlatformRequest(OBDemoWidgetID1, OUTBRAIN_SAMPLE_BUNDLE_URL, null, "en");
                    OBLocalSettings localSettings = new OBLocalSettings();
                    localSettings.setTestMode(false);
                    localSettings.partnerKey = "TEST_PARTNER_KEY";
                    RecommendationsUrlBuilder recommendationsUrlBuilder = new RecommendationsUrlBuilder(localSettings, new RecommendationsTokenHandler());
                    String url = recommendationsUrlBuilder.getUrl(ApplicationProvider.getApplicationContext(), platformRequest);
                    System.out.println("url: " + url);
                    Map<String, String> paramsMap = getQueryParams(url);
                    assertTrue(url.contains("https://odb.outbrain.com/utils/platforms?"));
                    assertEquals("vjnc", paramsMap.get("format"));
                    assertEquals("TEST_PARTNER_KEY", paramsMap.get("key"));
                    assertEquals(OUTBRAIN_SAMPLE_BUNDLE_URL, paramsMap.get("bundleUrl"));
                    assertEquals("en", paramsMap.get("lang"));
                    assertEquals("true", paramsMap.get("va"));
                    assertEquals("true", paramsMap.get("secured"));
                    assertEquals("true", paramsMap.get("rtbEnabled"));
                    assertNull(paramsMap.get("url"));
                    assertNull(paramsMap.get("portalUrl"));
                    lock.countDown();
                }
            }).start();
            lock.await(20000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getQueryParams(String url) {
        try {
            Map<String, String> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    params.put(key, value);
                }
            }

            return params;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
}