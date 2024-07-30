package com.outbrain.tests.fetchRecommendations;

import com.outbrain.OBSDK.Entities.OBBrandedItemSettings;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.SmartFeed.AddNewItemsToSmartFeedArrayHandler;
import com.outbrain.OBSDK.SmartFeed.OBSmartFeed;
import com.outbrain.OBSDK.SmartFeed.OBSmartFeedServiceListener;
import com.outbrain.OBSDK.SmartFeed.SFItemData;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SmartFeedBrandedCarouselTests extends TestCase {

    private static final String OBDemoWidgetID1 = "SFD_MAIN_2";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";

    private OBRecommendationsResponse responseParent;
    private OBSmartFeed obSmartFeed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String jsonString = TestsUtils.readJsonFromFile("smart_feed_response_branded_carousel.json");
        JSONObject responseJson = new JSONObject(jsonString).getJSONObject("response");
        OBRequest request = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        responseParent = OBRecommendationsParser.parse(jsonString, request);

        obSmartFeed = new OBSmartFeed(OBDemoUrl, OBDemoWidgetID1, null, null);
    }

    public final void testSmartFeedResponseSettings()  {
        OBSettings settings = responseParent.getSettings();
        assertEquals("odb_dynamic_ad-carousel", settings.getRecMode());
        OBBrandedItemSettings brandedCarouselSettings = settings.getBrandedItemSettings();
        assertNotNull(brandedCarouselSettings);
        assertEquals("COVID-19 Mythbusters: Get the Facts", brandedCarouselSettings.getContent());
        assertEquals("Outbrain", brandedCarouselSettings.getSponsor());
        assertEquals("Carousel", brandedCarouselSettings.getType());
        assertEquals("https://images.outbrainimg.com/transform/v3/eyJpdSI6IjM0ZGI3YzFhYWZhNTZjOTU0MjY4MmU0ZTVhMzdlNDQ3NWExZmY0NzlmNDZhYWI4NjEzMjdjMDBjZTEwOWIyYzEiLCJ3Ijo0MiwiaCI6NDIsImQiOjEuNSwiY3MiOjAsImYiOjR9.webp", brandedCarouselSettings.getThumbnail().getUrl());
        assertEquals("https://paid.outbrain.com/network/redir?p=TV-jqloioZ5gh9rbmhG_8hGi5SyowVGcgT7q1Mmv264LQJS9JQOgk6lGNxlypgykkS7o2iFV2kIjj4ErjCTovw9dd8o4o43t32YI1SAoTALOKp-ooeVJ8uoXi3Cov7x93uF9mG1YgwxImNytvcqAeoCcrcmHM18-bzKDl4gqZQGlBMOtw9fnBidXa5Nvp7K6FjEYZ_nZWxPl3vsxYTj4z57FI995Qbht1tY39Y3cXpYpYUiQPGjjp8C4ibSXVrzWQnyiojQAXiiStmlVGFp19q2KdhQIcOgiwn6stlxhcBhXsqSzXvK8YcAKrLqEushl1egiedldXLr3O_WK1HECNamxa6zJUBxMzLBbzZF8FAGhRc7WsJWztd4effXQLs6AnvCevaaI_jRfiMAblUUySHQCmBCATz8VMIiag21rDpsmyun39JH0Jaipvvhs57qFiZ1jGh-NW17Wr8Yd3-bf2rJTwQfjGePO0_1yZp4V1yk3NRaXIF33pn9L7Grwk-W_onwU3jor665IdDE56wv7hwvA2FG9yDtiwmh6u0kG5exrvDKjp58HfI1VKFuD2Cn6tIkbi40aajjZ55fg6-kwSITtxWMy5Gwv7KExU-DAmQT2IB8CuWOragsn_-OurCqfQGvwnnQL29ODv-qD-VqreIitC-joleO8x1J2Au2BeHgwlwsisr6ZBkyVjpf-4b4PrSomeSWn2OvZpD9RRsu6PXI-Jj4UPvd945y__iVR_HT0NLRC7GVTI5-33SKw7wko3-s49xyIYxoRlaiRzMklgUqeyssz3zxgMbOekkcHcnM&c=579a87f7&v=3", brandedCarouselSettings.getUrl());
    }

    public final void testParentSFItemParsing()  {
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
        assertEquals(1, obSmartFeed.getSmartFeedItems().size());

        SFItemData itemData = obSmartFeed.getSmartFeedItems().get(0);
        assertEquals(SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM,itemData.itemType());
        assertNull(itemData.getSingleRec());
        assertNotNull(itemData.getOutbrainRecs());
        assertEquals("BCR_1", itemData.getWidgetID());
        OBRecommendation firstRec = itemData.getOutbrainRecs().get(0);
        assertEquals("Fact: Coronavirus Can Be Transmitted in Hot Climates", firstRec.getContent());
        assertEquals("Learn More", firstRec.getCtaText());
    }
}
