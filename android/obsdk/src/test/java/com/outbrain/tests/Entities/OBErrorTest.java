package com.outbrain.tests.Entities;

import com.outbrain.OBSDK.Entities.OBError;
import com.outbrain.OBSDK.Entities.OBResponseRequest;
import com.outbrain.OBSDK.Entities.OBResponseStatus;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationsParser;
import com.outbrain.tests.TestsUtils;


import junit.framework.TestCase;

import org.json.JSONException;


public class OBErrorTest extends TestCase {
    private String errorResponseString;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String filePath = ClassLoader.getSystemClassLoader().getResource("odb_error_request.txt").getFile();
        System.out.println("--> " + filePath);
        this.errorResponseString = TestsUtils.readFileToString(filePath);
    }


    public final void testOBResponseStatus() {
        assertNotNull(this.errorResponseString);
        OBError obError = OBRecommendationsParser.parseError(this.errorResponseString);
        assertNotNull(obError);
        OBResponseStatus responseStatus = obError.status;
        assertEquals("Bad Request, invalid value for required parameter", responseStatus.getContent());
        assertEquals("Initialization of ODB request failed due to missing argument: com.outbrain.protocol.exceptions.ArgumentException: Unknown partner Status:[ST_INVALID_PARAMTER_VALUE] Parameter:[key,iOSSamleApp2014]", responseStatus.getDetails());
        assertEquals(2001, responseStatus.getStatusId());
    }

    public final void testOBResponseRequest() {
        assertNotNull(this.errorResponseString);
        OBError obError = OBRecommendationsParser.parseError(this.errorResponseString);
        assertNotNull(obError);
        OBResponseRequest responseRequest = obError.getRequest();
        assertEquals("699bd8a104679a9693ff50dac855d9ba", responseRequest.getReqId());
    }

    public final void testOBSettings() {
        assertNotNull(this.errorResponseString);
        OBError obError = OBRecommendationsParser.parseError(this.errorResponseString);
        assertNotNull(obError);
        OBSettings obSettings = obError.getSettings();
        try {
            assertEquals(true, obSettings.getJSONObject().getBoolean("stopRater"));
        } catch (JSONException e) {
            assertFalse(true);
        }
    }
}
