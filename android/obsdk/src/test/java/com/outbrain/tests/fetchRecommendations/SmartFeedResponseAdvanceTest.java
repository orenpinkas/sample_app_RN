package com.outbrain.tests.fetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.SmartFeed.AddNewItemsToSmartFeedArrayHandler;
import com.outbrain.OBSDK.SmartFeed.OBSmartFeed;
import com.outbrain.OBSDK.SmartFeed.OBSmartFeedServiceListener;
import com.outbrain.OBSDK.SmartFeed.SFItemData;
import com.outbrain.OBSDK.SmartFeed.SFUtils;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by rabraham on 27/06/2017.
 */

public class SmartFeedResponseAdvanceTest extends TestCase {

    private static final String OBDemoWidgetID1 = "SFD_MAIN_2";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";
    private OBRecommendationsResponse responseParent;
    private OBRecommendationsResponse responseChild1;
    private OBRecommendationsResponse responseChild2;
    private OBRecommendationsResponse responseChild3;
    private OBSmartFeed obSmartFeed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String jsonString = TestsUtils.readJsonFromFile("smart_feed_response_parent.json");
        JSONObject responseJson = new JSONObject(jsonString).getJSONObject("response");
        assertNotNull(responseJson);
        OBRequest request = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        responseParent = OBRecommendationsParser.parse(jsonString, request);

        jsonString = TestsUtils.readJsonFromFile("smart_feed_response_child1.json");
        responseChild1 = OBRecommendationsParser.parse(jsonString, request);

        jsonString = TestsUtils.readJsonFromFile("smart_feed_response_child2.json");
        responseChild2 = OBRecommendationsParser.parse(jsonString, request);

        jsonString = TestsUtils.readJsonFromFile("smart_feed_response_child3.json");
        responseChild3 = OBRecommendationsParser.parse(jsonString, request);

        obSmartFeed = new OBSmartFeed(OBDemoUrl, OBDemoWidgetID1, null, null);
    }

    public final void testSmartFeedResponseContent()  {
        assertEquals(6, responseParent.getAll().size());
        assertEquals(1, responseChild1.getAll().size());
        assertEquals(2, responseChild2.getAll().size());
        assertEquals(1, responseChild3.getAll().size());
    }

    public final void testRecModeIsFetchedCorrectlyFromResponse() {
        assertTrue(responseParent.getSettings().getRecMode().equals("sdk_sfd_2_columns"));
        assertTrue(responseChild1.getSettings().getRecMode().equals("sdk_sfd_1_column"));
        assertTrue(responseChild2.getSettings().getRecMode().equals("sdk_sfd_thumbnails"));
        assertTrue(responseChild3.getSettings().getRecMode().equals("sdk_sfd_1_column"));
    }

    public final void testWidgetHeaderTextIsFetchedCorrectlyFromResponse() {
        assertTrue(responseParent.getSettings().getWidgetHeaderText().equals("Sponsored Links"));
        assertTrue(responseChild1.getSettings().getWidgetHeaderText().equals("Around CNN"));
        assertTrue(responseChild2.getSettings().getWidgetHeaderText().equals(""));
        assertTrue(responseChild3.getSettings().getWidgetHeaderText().equals("Sponsored Links"));
    }

    public final void testPaidLabelParamsIsFetchedCorrectlyFromResponse() {
        assertTrue(responseParent.getSettings().getPaidLabelText().equals("Sponsored"));
        assertTrue(responseParent.getSettings().getPaidLabelTextColor().equals("#ffffff"));
        assertTrue(responseParent.getSettings().getPaidLabelBackgroundColor().equals("#666666"));
    }

    public final void testOBSettingsForParent()  {
        OBSettings obSettings = responseParent.getSettings();
        assertEquals(obSettings.getApv(), true);
        assertEquals(obSettings.isViewabilityEnabled(), true);
        assertTrue(obSettings.isSmartFeed());
        assertNotNull(obSettings.getFeedContentList());
        assertEquals(3, obSettings.getFeedContentList().size());
        assertEquals("SDK_SFD_1", obSettings.getFeedContentList().get(0));
        assertEquals("SDK_SFD_2", obSettings.getFeedContentList().get(1));
        assertEquals("SDK_SFD_3", obSettings.getFeedContentList().get(2));
        assertEquals("Read More", obSettings.getReadMoreText());
    }

    public final void testAbTestsOptimizationsSettings() {
        OBSettings obSettings = responseParent.getSettings();
        assertEquals(14, obSettings.getAbTitleFontSize());
        assertEquals(12, obSettings.getAbSourceFontSize());
        assertEquals(1, obSettings.getAbTitleFontStyle());
        assertEquals("#ffa511", obSettings.getAbSourceFontColor());
        assertEquals(true, obSettings.getAbImageFadeAnimation());
        assertEquals(400, obSettings.getAbImageFadeDuration());
    }

    public final void testAbTestsOptimizationsSettingsWhenResponseEmpty() {
        OBSettings obSettings = responseChild1.getSettings();
        assertEquals(0, obSettings.getAbTitleFontSize());
        assertEquals(0, obSettings.getAbSourceFontSize());
        assertEquals(0, obSettings.getAbTitleFontStyle());
        assertEquals(null, obSettings.getAbSourceFontColor());
        assertEquals(true, obSettings.getAbImageFadeAnimation());
        assertEquals(750, obSettings.getAbImageFadeDuration());
    }

    public final void testFeedCycleLimitIsFetchedCorrectlyFromResponse(){
        assertEquals(5, responseParent.getSettings().getFeedCyclesLimit());
        assertEquals(0, responseChild1.getSettings().getFeedCyclesLimit());
        assertEquals(0, responseChild2.getSettings().getFeedCyclesLimit());
        assertEquals(0, responseChild3.getSettings().getFeedCyclesLimit());
    }

    public final void testChunkSizeIsFetchedCorrectlyFromResponse(){
        assertEquals(3, responseParent.getSettings().getFeedChunkSize());
        assertEquals(0, responseChild1.getSettings().getFeedChunkSize());
        assertEquals(0, responseChild2.getSettings().getFeedChunkSize());
        assertEquals(0, responseChild3.getSettings().getFeedChunkSize());
    }

    public final void testOBSmartFeedSFItemsSize() {
        assertEquals(0, obSmartFeed.getSmartFeedItems().size());
        OBSmartFeedServiceListener listener = new OBSmartFeedServiceListener() {
            @Override
            public void notifyNewItems(ArrayList<SFItemData> sfItemsList, boolean shouldUpdateUI) {
                obSmartFeed.notifyNewItems(sfItemsList, shouldUpdateUI);
            }

            @Override
            public void notifyNoNewItemsToAdd() {

            }
        };

        AddNewItemsToSmartFeedArrayHandler handler = new AddNewItemsToSmartFeedArrayHandler(
                null,
                responseParent,
                new WeakReference<>(listener),
                true,
                false);
        handler.run();
        assertEquals(3, obSmartFeed.getSmartFeedItems().size());

        handler = new AddNewItemsToSmartFeedArrayHandler(
                null,
                responseChild1,
                new WeakReference<>(listener),
                false,
                false);
        handler.run();
        assertEquals(4, obSmartFeed.getSmartFeedItems().size());

        handler = new AddNewItemsToSmartFeedArrayHandler(
                null,
                responseChild2,
                new WeakReference<>(listener),
                false,
                false);
        handler.run();
        assertEquals(6, obSmartFeed.getSmartFeedItems().size());

        handler = new AddNewItemsToSmartFeedArrayHandler(
                null,
                responseChild3,
                new WeakReference<>(listener),
                false,
                false);
        handler.run();
        assertEquals(7, obSmartFeed.getSmartFeedItems().size());
    }

    public final void testOBSmartFeedSFItemsType() {
        OBSmartFeedServiceListener listener = new OBSmartFeedServiceListener() {
            @Override
            public void notifyNewItems(ArrayList<SFItemData> sfItemsList, boolean shouldUpdateUI) {
                obSmartFeed.notifyNewItems(sfItemsList, shouldUpdateUI);
            }

            @Override
            public void notifyNoNewItemsToAdd() {

            }
        };

        OBRecommendationsResponse [] obRecommendationsResponses = {
                responseParent, responseChild1, responseChild2, responseChild3
        };
        AddNewItemsToSmartFeedArrayHandler handler;
        for (OBRecommendationsResponse response : obRecommendationsResponses) {
            handler = new AddNewItemsToSmartFeedArrayHandler(
                    null,
                    response,
                    new WeakReference<>(listener),
                    response.equals(responseParent),
                    false);
            handler.run();
        }
        // two items in line, no title
        assertEquals(SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE, obSmartFeed.getSmartFeedItems().get(0).itemType());
        assertEquals(SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE_WITH_VIDEO, obSmartFeed.getSmartFeedItems().get(1).itemType());
        assertEquals(SFItemData.SFItemType.GRID_TWO_ITEMS_IN_LINE, obSmartFeed.getSmartFeedItems().get(2).itemType());
        assertNull(obSmartFeed.getSmartFeedItems().get(0).getTitle());
        assertNull(obSmartFeed.getSmartFeedItems().get(1).getTitle());
        assertNull(obSmartFeed.getSmartFeedItems().get(2).getTitle());

        // single item with title
        assertEquals(SFItemData.SFItemType.SINGLE_ITEM, obSmartFeed.getSmartFeedItems().get(3).itemType());
        assertEquals("Around CNN", obSmartFeed.getSmartFeedItems().get(3).getTitle());

        // two strip thumbnails, no title
        assertEquals(SFItemData.SFItemType.STRIP_THUMBNAIL_ITEM, obSmartFeed.getSmartFeedItems().get(4).itemType());
        assertEquals(SFItemData.SFItemType.STRIP_THUMBNAIL_ITEM, obSmartFeed.getSmartFeedItems().get(5).itemType());
        assertNull(obSmartFeed.getSmartFeedItems().get(4).getTitle());
        assertNull(obSmartFeed.getSmartFeedItems().get(5).getTitle());

        // single item with title
        assertEquals(SFItemData.SFItemType.SINGLE_ITEM, obSmartFeed.getSmartFeedItems().get(6).itemType());
        assertEquals("Sponsored Links", obSmartFeed.getSmartFeedItems().get(6).getTitle());
    }

    public final void testOBSmartFeedItemsLogo() {
        String logoURL = "https://images.outbrainimg.com/transform/v3/eyJpdSI6ImY5OTE5OTIxMTg5YTNlOThlMDFiMjE3NjQxOTg0ZDcwOGY5ZWU1ZmY5YWFhM2I4YmRhZmZmNjQ3MmIzZDljOTQiLCJ3Ijo4NSwiaCI6MjAsImQiOjIuMCwiY3MiOjAsImYiOjB9.jpg";

        // for child 1 - organic single item
        assertEquals(logoURL, responseChild1.get(0).getLogo().getUrl());
        assertEquals(20, responseChild1.get(0).getLogo().getHeight());
        assertEquals(85, responseChild1.get(0).getLogo().getWidth());

        // for child 2 - two recommendations of type strip thumbnail
        assertEquals(logoURL, responseChild2.get(0).getLogo().getUrl());
        assertEquals(logoURL, responseChild2.get(1).getLogo().getUrl());

        // for parent item - without a logo
        assertEquals(null ,responseParent.get(0).getLogo().getUrl());
        assertEquals(0 ,responseParent.get(0).getLogo().getWidth());
        assertEquals(0 ,responseParent.get(0).getLogo().getHeight());
    }

    public final void testPublisherAds() {
        assertEquals("sponsored", responseParent.get(0).getAudienceCampaignsLabel());
        assertEquals(null, responseParent.get(1).getAudienceCampaignsLabel());
    }

    public final void testListingsViewability() {
        assertTrue(responseParent.getSettings().isViewabilityEnabled());
    }

    public final void testSmartfeedImageType() {
        OBRecommendation rec = responseChild1.getAll().get(0);
        assertFalse(rec.getThumbnail().isGif());

        rec = responseChild2.getAll().get(0);
        assertFalse(rec.getThumbnail().isGif());

        rec = responseChild3.getAll().get(0);
        assertTrue(rec.getThumbnail().isGif());

    }

    public final void testSourceFormatParsing() {
        assertEquals("", responseChild1.getSettings().getOrganicSourceFormat());
        assertEquals("Custom Organic Source",responseChild2.getSettings().getOrganicSourceFormat());
        assertEquals("Recommended by $SOURCE",responseChild3.getSettings().getOrganicSourceFormat());


        assertEquals("", responseChild1.getSettings().getPaidSourceFormat());
        assertEquals("Paid provider", responseChild2.getSettings().getPaidSourceFormat());
        assertEquals("Provided to you by $SOURCE", responseChild3.getSettings().getPaidSourceFormat());
    }

    public final void testGetRecSourceTextForOrganicRec() {
        OBRecommendation testRec = responseChild1.getAll().get(0);
        assertFalse(testRec.isPaid());
        // getOrganicSourceFormat is ""
        OBSettings testSettings = responseChild1.getSettings();
        assertEquals("", testSettings.getOrganicSourceFormat());
        String sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals(testRec.getSourceName(), sourceText);

        // getOrganicSourceFormat is "Custom Organic Source"
        testSettings = responseChild2.getSettings();
        assertEquals("Custom Organic Source", testSettings.getOrganicSourceFormat());
        sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals("Custom Organic Source", sourceText);

        // getOrganicSourceFormat is "Recommended by $SOURCE"
        testSettings = responseChild3.getSettings();
        assertEquals("Recommended by $SOURCE", testSettings.getOrganicSourceFormat());
        sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals("Recommended by " + testRec.getSourceName(), sourceText);
    }

    public final void testGetRecSourceTextForPaidRec() {
        OBRecommendation testRec = responseChild3.getAll().get(0);
        assertTrue(testRec.isPaid());

        // getPaidSourceFormat is ""
        OBSettings testSettings = responseChild1.getSettings();
        assertEquals("", testSettings.getPaidSourceFormat());
        String sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals(testRec.getSourceName(), sourceText);

        // getPaidSourceFormat is "Custom Organic Source"
        testSettings = responseChild2.getSettings();
        assertEquals("Paid provider", testSettings.getPaidSourceFormat());
        sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals("Paid provider", sourceText);

        // getPaidSourceFormat is "Recommended by $SOURCE"
        testSettings = responseChild3.getSettings();
        assertEquals("Provided to you by $SOURCE", testSettings.getPaidSourceFormat());
        sourceText = SFUtils.getRecSourceText(testRec, testSettings);
        assertEquals("Provided to you by " + testRec.getSourceName(), sourceText);
        assertEquals("", responseChild1.getSettings().getPaidSourceFormat());
    }
}
