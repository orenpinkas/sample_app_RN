package com.outbrain.OBSDK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.FetchRecommendations.MultivacListener;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsHasher;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsService;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;
import com.outbrain.OBSDK.Registration.RegistrationService;
import com.outbrain.OBSDK.Utilities.IronSourceService;
import com.outbrain.OBSDK.Utilities.OBAdvertiserIdFetcher;
import com.outbrain.OBSDK.Viewability.SFViewabilityService;
import com.outbrain.OBSDK.Viewability.ViewabilityService;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OutbrainService implements OutbrainCommunicator {

    private static final String LOG_TAG = "OBSDK";

    @SuppressLint("StaticFieldLeak")
    private static OutbrainService mInstance = null;
    private boolean isIronSourceIntegration = false;

    private RegistrationService registrationService;
    private RecommendationsService recommendationsService;
    private Context applicationContext;
    private OkHttpClient httpClient;
    private OBLocalSettings localSettings;

    private OutbrainService() {

    }

    public static OutbrainService getInstance(){
        if (mInstance == null)
        {
            mInstance = new OutbrainService();
            mInstance.localSettings = new OBLocalSettings();
            mInstance.registrationService = RegistrationService.getInstance();
            mInstance.registrationService.setLocalSettings(mInstance.localSettings);
            mInstance.recommendationsService = new RecommendationsService(mInstance.localSettings);
        }
        return mInstance;
    }

    private Context getApplicationContext() {
        return applicationContext;
    }


    @Override
    public void register(Context applicationContext, String partnerKey) {
        this.applicationContext = applicationContext.getApplicationContext(); // The order is important, first line in method has to set the applicationContext
        this.httpClient = OBHttpClient.getClient(this.applicationContext);
        registrationService.register(partnerKey);
        ViewabilityService.init(this.applicationContext);
        SFViewabilityService.init(this.applicationContext);
        OBErrorReporting.init(this.applicationContext, this.localSettings.partnerKey);
        if (this.isIronSourceIntegration) {
            IronSourceService.verifyIronSourceInstallation(this.applicationContext, this.localSettings, this.recommendationsService.getRecommendationsQueueManager());
        }
    }

    @Override
    public void fetchRecommendations(OBRequest request, RecommendationsListener handler) {
        recommendationsService.fetchRecommendations(getApplicationContext(), handler, request);
    }

    public void fetchMultivac(OBRequest request, MultivacListener multivacListener) {
        recommendationsService.fetchMultivac(getApplicationContext(), multivacListener, request);
    }

    @Override
    public String getUrl(OBRecommendation rec) {
        if (rec.isPaid()) {
            return RecommendationsHasher.getUrlForRecommendation(rec);
        }
        else {
            handleOrganicClick(rec);
            return RecommendationsHasher.getOrigUrlForRecommendation(rec);
        }
    }

    @Override
    public void setTestMode(boolean testMode) {
        registrationService.setTestMode(testMode);
    }

    @Override
    public void testRTB(boolean testRTB) {
        registrationService.setTestRTB(testRTB);
    }

    @Override
    public void testLocation(String location) {
        registrationService.setTestLocation(location);
    }

    private void handleOrganicClick(OBRecommendation rec) {
        String url = getOrganicClickUrl(rec);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i("OBSDK", "handleOrganicClick: " + url);
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                String msg = "Erorr in handleOrganicClick: " + e.getLocalizedMessage();
                Log.e("OBSDK", msg);
                e.printStackTrace();
                OBErrorReporting.getInstance().reportErrorToServer(msg);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    String msg = "Erorr in handleOrganicClick unexpected response code: " + response.code();
                    Log.e("OBSDK", msg);
                    OBErrorReporting.getInstance().reportErrorToServer(msg);
                }
                else {
                    Log.i("OBSDK", "success - reported click event on rec");
                }
                if (response.body() != null) {
                    response.body().close();
                }
            }
        });
    }

    private String getOrganicClickUrl(OBRecommendation document) {
        return ((OBRecommendationImpl)document).getPrivateUrl() + "&noRedirect=true";
    }

    @Override
    public String getOutbrainAboutURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("www.outbrain.com");
        builder.appendPath("what-is");
        builder.appendPath("");
        setAdvertiserIdURLParams(builder);
        return builder.build().toString();
    }

    private void setAdvertiserIdURLParams(Uri.Builder builder) {
        AdvertisingIdClient.Info adInfo = OBAdvertiserIdFetcher.getAdClientInfo();
        final String AD_ID_PARAM_KEY = "advertiser_id";
        final String DOO_PARAM_KEY = "doo";
        if (adInfo != null) {
            if (!adInfo.isLimitAdTrackingEnabled()) {
                builder.appendQueryParameter(DOO_PARAM_KEY, "false");
                builder.appendQueryParameter(AD_ID_PARAM_KEY, adInfo.getId());
            }
            else {
                builder.appendQueryParameter(DOO_PARAM_KEY, "true");
                builder.appendQueryParameter(AD_ID_PARAM_KEY, "null");
            }
        }
        else {
            // We got Exception from getAdvertisingIdInfo()
            builder.appendQueryParameter(DOO_PARAM_KEY, "true");
            builder.appendQueryParameter(AD_ID_PARAM_KEY, "na");
        }
    }

    public boolean isTestMode(){
        return localSettings.isTestMode();
    }
    public boolean wasInitialized() {
        return registrationService.wasInitialized();
    }

    public void setIronSourceIntegration(boolean ironSourceIntegration) {
        isIronSourceIntegration = ironSourceIntegration;
    }

    public boolean isIronSourceIntegration() {
        return isIronSourceIntegration;
    }
}
