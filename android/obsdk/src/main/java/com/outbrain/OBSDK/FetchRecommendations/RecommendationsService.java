package com.outbrain.OBSDK.FetchRecommendations;

import android.content.Context;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.OutbrainException;
import com.outbrain.OBSDK.OutbrainService;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecommendationsService {
    private final OBLocalSettings localSettings;
    private final RecommendationsTokenHandler recommendationsTokenHandler;
    private final ExecutorService recommendationsQueueManager;

    public RecommendationsService(OBLocalSettings localSettings) {
        this.localSettings = localSettings;
        this.recommendationsTokenHandler = new RecommendationsTokenHandler();
        this.recommendationsQueueManager = Executors.newSingleThreadExecutor();
    }

    public void fetchRecommendations(Context applicationContext, RecommendationsListener recommendationsListener, OBRequest request) {
        if (localSettings == null || localSettings.partnerKey == null || localSettings.partnerKey.equals("")) {
            throw new OutbrainException("partnerKey was not found, did you call the register function?");
        }

        if (request.getWidgetId() == null || request.getWidgetId().equals("")) {
            recommendationsListener.onOutbrainRecommendationsFailure(new OutbrainException("widgetId was not found, please make sure you set the widgetId correctly"));
            return;
        }

        boolean isPlatfromRequest = request instanceof OBPlatformRequest;
        if (isPlatfromRequest) {
            OBPlatformRequest platformRequest = (OBPlatformRequest)request;
            // if we set one of bundleUrl or portalUrl - the other will be empty - if both empty then we have a problem...
            if (isNonValidParam(platformRequest.getBundleUrl()) && isNonValidParam(platformRequest.getPortalUrl())) {
                recommendationsListener.onOutbrainRecommendationsFailure(new OutbrainException("Portal or Bundle URL were not found, please make sure you set the Bundle, Portal correctly"));
                return;
            }
        }
        else if (isNonValidParam(request.getUrl())) {
            recommendationsListener.onOutbrainRecommendationsFailure(new OutbrainException("URL was not found, please make sure you set the URL correctly"));
            return;
        }

        FetchRecommendationsHandler fetchRecommendationsHandler = new FetchRecommendationsHandler(applicationContext, request, localSettings, recommendationsListener, recommendationsTokenHandler);
        this.recommendationsQueueManager.submit(fetchRecommendationsHandler);
    }

    public void fetchMultivac(Context applicationContext, MultivacListener multivacListener, OBRequest request) {
        FetchRecommendationsHandler fetchRecommendationsHandler = new FetchRecommendationsHandler(applicationContext, request, localSettings, multivacListener, recommendationsTokenHandler);
        this.recommendationsQueueManager.submit(fetchRecommendationsHandler);
    }

    private boolean isNonValidParam(String param) {
        return (param == null) || "".equals(param);
    }

    public ExecutorService getRecommendationsQueueManager() {
        return recommendationsQueueManager;
    }
}

