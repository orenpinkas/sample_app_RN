package com.outbrain.tests.fetchRecommendations;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.FetchRecommendationsHandler;
import com.outbrain.OBSDK.FetchRecommendations.MultivacListener;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.Utilities.RecommendationApvHandler;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;
import com.outbrain.OBSDK.Viewability.ViewabilityService;
import com.outbrain.tests.TestsUtils;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
public class MultivacResponseTests {

    private final CountDownLatch lock = new CountDownLatch(1);
    private boolean receivedMultivacResponse = false;

    private Context context;
    private RecommendationsTokenHandler recommendationsTokenHandler;
    private OBRequest obRequest;
    private static final String OBDemoWidgetID1 = "SDK_1";
    private static final String OBDemoUrl = "http://mobile-demo.outbrain.com";

    @Before
    public void setUp() {
        this.recommendationsTokenHandler = new RecommendationsTokenHandler();
        this.obRequest = new OBRequest(OBDemoUrl, OBDemoWidgetID1);
        this.context = ApplicationProvider.getApplicationContext();
        ViewabilityService.init(context);

    }

    @Test
    public final void testAPV() {
        RecommendationApvHandler.updateAPVCacheForResponse(new OBSettings(null), obRequest);
    }

    @Test
    public final void testParseMultivacResponse() throws Exception {
        String jsonString = TestsUtils.readJsonFromFile("outbrain_multivac_response.json");
        assertNotNull(jsonString);

        MultivacListener multivacListener = new MultivacListener() {
            @Override
            public void onMultivacSuccess(ArrayList<OBRecommendationsResponse> cardsResponseList, int feedIdx, boolean hasMore) {
                receivedMultivacResponse = true;
                assertTrue(hasMore);
                assertEquals(0, feedIdx);
                verifyMultivacCardsResponse(cardsResponseList);
                lock.countDown();
            }

            @Override
            public void onMultivacFailure(Exception ex) {
                assertFalse(true);
                lock.countDown();

            }
        };

        FetchRecommendationsHandler fetchRecommendationsHandler = new FetchRecommendationsHandler(this.context, obRequest, null, multivacListener, this.recommendationsTokenHandler);


        // Call private method: this.fetchRecommendationsHandler.handleMultivacResponse(0, jsonString);
        Method getHandleMultivacResponseMethod = FetchRecommendationsHandler.class.getDeclaredMethod("handleMultivacResponse", long.class, String.class);
        getHandleMultivacResponseMethod.setAccessible(true);
        getHandleMultivacResponseMethod.invoke(fetchRecommendationsHandler, 0, jsonString);

        shadowOf(getMainLooper()).idle();
        lock.await(2000, TimeUnit.MILLISECONDS);
        assertTrue(receivedMultivacResponse);
    }

    private void verifyMultivacCardsResponse(ArrayList<OBRecommendationsResponse> cardsResponseList) {
        assertEquals(3, cardsResponseList.size());
        OBRecommendationsResponse recsResponse0 = cardsResponseList.get(0);
        assertEquals(recsResponse0.getRequest().getToken(), "OGM2OTUxYjNiZDcyOTZlYWE1MzQwZGFkM2M0YjQ3NDM=");
        assertEquals(recsResponse0.getRequest().getReqId(), "2e0850a4bb0aed9b3c8f8c61e7260047");
        assertEquals(1, recsResponse0.getAll().size());
        assertEquals("[Pics] Fishermen See Animal Sitting On Iceberg, Then Look Again", recsResponse0.getAll().get(0).getContent());


        OBRecommendationsResponse recsResponse1 = cardsResponseList.get(1);
        assertEquals(recsResponse1.getRequest().getToken(), "OGM2OTUxYjNiZDcyOTZlYWE1MzQwZGFkM2M0YjQ3NDM=");
        assertEquals(recsResponse1.getRequest().getReqId(), "503f180e7aed82cd0e4e455cd4750d86");
        assertEquals(2, recsResponse1.getAll().size());
        assertEquals("Alves delight at goals", recsResponse1.getAll().get(1).getContent());

        OBRecommendationsResponse recsResponse2 = cardsResponseList.get(2);
        assertEquals(recsResponse2.getRequest().getToken(), "OGM2OTUxYjNiZDcyOTZlYWE1MzQwZGFkM2M0YjQ3NDM=");
        assertEquals(recsResponse2.getRequest().getReqId(), "10477ba1c8dcfd4acaa1f706e26ef628");
        assertEquals(1, recsResponse2.getAll().size());
        assertEquals("New poll: Mitch McConnell is trailing Amy McGrath. Chip in to defeat him.", recsResponse2.getAll().get(0).getContent());
    }
}
