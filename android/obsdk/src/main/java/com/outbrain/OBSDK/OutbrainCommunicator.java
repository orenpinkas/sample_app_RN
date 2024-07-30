package com.outbrain.OBSDK;

import android.content.Context;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;

interface OutbrainCommunicator {
    void register(Context applicationContext, String partnerKey);

    void fetchRecommendations(OBRequest request, RecommendationsListener handler);

    String getUrl(OBRecommendation rec);

    void setTestMode(boolean testMode);

    void testRTB(boolean testRTB);

    void testLocation(String location);

    String getOutbrainAboutURL();

}