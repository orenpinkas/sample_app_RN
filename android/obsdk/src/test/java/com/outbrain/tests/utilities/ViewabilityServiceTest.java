package com.outbrain.tests.utilities;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import com.outbrain.OBSDK.Viewability.ViewabilityService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@SuppressWarnings("ImplicitArrayToString")
@RunWith(RobolectricTestRunner.class)
public class ViewabilityServiceTest {

    private ViewabilityService viewabilityService;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        ViewabilityService.init(context);
        this.viewabilityService = ViewabilityService.getInstance();
    }

    @Test
    public final void testEditTmParameterInUrl() {
        final String tmValue = "111111";
        final boolean optedOutVal = true;
        String urlNoTM = "https://mcdp-chidc2.outbrain.com/l?token=42ea5daf3430b7b031c99581cdbbff1e_2465_1558965693396";
        String urlNoParamsNoTM = "https://mcdp-chidc2.outbrain.com/l";
        String urlWithParamsWithTM = "https://log.outbrainimg.com/loggerServices/widgetGlobalEvent?rId=a7a219ee9e20fc846946341d3ebd6d75&pvId=67a059883e4d0384383343623b5155cd&sid=5291479&pid=4737&idx=3&wId=1146&pad=1&org=0&tm=0&eT=0";
        String urlWithParamsNoTM = "https://log.outbrainimg.com/loggerServices/widgetGlobalEvent?rId=a7a219ee9e20fc846946341d3ebd6d75&pvId=67a059883e4d0384383343623b5155cd&sid=5291479&pid=4737&idx=3&wId=1146&pad=1&org=0&eT=0";

        try {
            Method addTmAndOptedOutParams = ViewabilityService.class.getDeclaredMethod("addTmAndOptedOutParams", String.class, String.class, boolean.class);
            addTmAndOptedOutParams.setAccessible(true);
            String res1 = (String) addTmAndOptedOutParams.invoke(this.viewabilityService, urlNoTM, tmValue, optedOutVal);
            assertTrue(res1.contains("tm="+tmValue));
            assertTrue(res1.contains("oo="+String.valueOf(optedOutVal)));

            String res2 = (String) addTmAndOptedOutParams.invoke(this.viewabilityService, urlNoParamsNoTM, tmValue, optedOutVal);
            assertTrue(res2.contains("tm="+tmValue));
            assertTrue(res2.contains("oo="+String.valueOf(optedOutVal)));

            String res3 = (String) addTmAndOptedOutParams.invoke(this.viewabilityService, urlWithParamsWithTM, tmValue, optedOutVal);
            assertTrue(res3.contains("tm="+tmValue));
            assertTrue(res3.contains("oo="+String.valueOf(optedOutVal)));

            String res4 = (String) addTmAndOptedOutParams.invoke(this.viewabilityService, urlWithParamsNoTM, tmValue, optedOutVal);
            assertTrue(res4.contains("tm="+tmValue));
            assertTrue(res4.contains("oo="+String.valueOf(optedOutVal)));
        }
        catch (InvocationTargetException ex) {
            assertNull("Received exception: " + ex.getCause(), ex);
        }
        catch (Exception ex) {
            assertNull("Received exception: " + ex.getStackTrace(), ex);
        }
    }
}
