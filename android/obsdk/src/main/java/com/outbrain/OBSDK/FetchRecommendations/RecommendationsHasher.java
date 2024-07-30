package com.outbrain.OBSDK.FetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendation;

public class RecommendationsHasher {
    public static String getOrigUrlForRecommendation(OBRecommendation doc) {
        return ((OBRecommendationImpl)doc).getOrigUrl();
    }

    public static String getUrlForRecommendation(OBRecommendation doc) {
        return ((OBRecommendationImpl)doc).getUrl();
    }
}