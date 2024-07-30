package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.Entities.OBViewabilityActions;
import com.outbrain.tests.TestsUtils;

import junit.framework.TestCase;

import org.json.JSONObject;

public class OBViewabilityActionsTest extends TestCase {

    private JSONObject viewabilityActionsJson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String viewabilityActionsJsonStr = TestsUtils.readJsonFromFile("viewability_actions.json");
        viewabilityActionsJson = new JSONObject(viewabilityActionsJsonStr);
    }

    public final void testViewabilityActionsParsing() {
        OBViewabilityActions viewabilityActions = new OBViewabilityActions(viewabilityActionsJson.optJSONObject("viewability_actions"));
        assertEquals(
                viewabilityActionsJson.optJSONObject("viewability_actions").optString("reportServed"),
                viewabilityActions.getReportServedUrl()
        );
    }
}
