package com.outbrain.OBSDK.FetchRecommendations;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.GDPRUtils.GDPRUtils;
import com.outbrain.OBSDK.OBUtils;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.OutbrainService;
import com.outbrain.OBSDK.SmartFeed.Theme.SFThemeImpl;
import com.outbrain.OBSDK.Utilities.OBAdvertiserIdFetcher;
import com.outbrain.OBSDK.Utilities.RecommendationApvHandler;
import com.outbrain.OBSDK.Utilities.RecommendationsTokenHandler;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Random;

//Building the URL from previous settings and parameters
public class RecommendationsUrlBuilder {
    private final OBLocalSettings localSettings;
    private final RecommendationsTokenHandler recommendationsTokenHandler;


    public RecommendationsUrlBuilder(OBLocalSettings localSettings, RecommendationsTokenHandler recommendationsTokenHandler) {
        this.localSettings = localSettings;
        this.recommendationsTokenHandler = recommendationsTokenHandler;
    }

    public String getUrl(Context applicationContext, OBRequest request) {
        return buildUrl(applicationContext, request);
    }

    private String buildUrl(Context applicationContext, OBRequest request) {
        Uri.Builder builder = new Uri.Builder();
        boolean isPlatfromRequest = request instanceof OBPlatformRequest;
        builder.scheme("https");

        if (isPlatfromRequest) {
            buildPlatformBaseUrl(builder);
        }
        else if (request.isMultivac()) {
            buildMultivac(builder);
        }
        else {
            buildODB(builder);
        }

        handleWidgetId(request, builder);
        handleKey(builder);
        handleIdx(request, builder);
        handleFormat(builder);
        handleRandom(builder);
        handleVersion(builder);
        handleApv(request, builder);
        if (isPlatfromRequest) {
            handlePlatformParamsAppending(request, builder);
        }
        else {
            handleUrlAppending(request, builder);
        }
        handleTestMode(request, builder);
        handleAdId(applicationContext, builder);
        handleToken(request, builder);
        handleInstallationType(builder);
        handleSecureConnection(builder);
        handleReferrer(builder);
        handleScreenSize(applicationContext, builder);
        handleDeviceName(applicationContext, builder);
        handleOSName(builder);
        handleOSVersion(builder);
        handleAppVersion(applicationContext, builder);
        handleAppIdentifier(applicationContext, builder);
        handleRTB(builder);
        handleGDPR(applicationContext, builder);
        handleExternalId(request, builder);
        handlePubImpId(request, builder);
        handleMultivac(request, builder);
        handleViewabilityActions(builder);
        handleIronSource(builder);
        handleDarkModeFlag(builder);
        return builder.build().toString();
    }

    private void buildODB(Uri.Builder builder) {
        builder.authority("odb.outbrain.com");
        builder.appendPath("utils");
        builder.appendPath("get");
    }

    private void buildMultivac(Uri.Builder builder) {
        builder.authority("mv.outbrain.com");
        builder.appendPath("Multivac");
        builder.appendPath("api");
        builder.appendPath("get");
    }

    private void buildPlatformBaseUrl(Uri.Builder builder) {
        builder.authority("odb.outbrain.com");
        builder.appendPath("utils");
        builder.appendPath("platforms");
    }

    private void handleAdId(Context applicationContext, Uri.Builder builder) {
        AdvertisingIdClient.Info adInfo = OBAdvertiserIdFetcher.getAdvertisingIdInfo(applicationContext);
        if (adInfo != null) {
            if (!adInfo.isLimitAdTrackingEnabled()) {
                builder.appendQueryParameter("doo", "false");
                builder.appendQueryParameter("api_user_id", adInfo.getId());
            }
            else {
                builder.appendQueryParameter("doo", "true");
                builder.appendQueryParameter("api_user_id", "null");
            }
        }
        else {
            // We got Exception from getAdvertisingIdInfo()
            builder.appendQueryParameter("doo", "true");
            builder.appendQueryParameter("api_user_id", "na");
        }
    }

    private void handleApv(OBRequest request, Uri.Builder builder) {
        if (RecommendationApvHandler.getApvForRequest(request)) {
            builder.appendQueryParameter("apv", "true");
        }
    }

    private void handleUrlAppending(OBRequest request, Uri.Builder builder) {
        String url = request.getUrl();
        builder.appendQueryParameter("url", url);
    }

    private void handlePlatformParamsAppending(OBRequest request, Uri.Builder builder) {
        OBPlatformRequest platformRequest = (OBPlatformRequest)request;
        // BundleUrl or PortalUrl
        if (platformRequest.getBundleUrl() != null) {
            builder.appendQueryParameter("bundleUrl", platformRequest.getBundleUrl());
        }
        else {
            builder.appendQueryParameter("portalUrl", platformRequest.getPortalUrl());
        }
        // Lang param
        if (platformRequest.getLang() != null) {
            builder.appendQueryParameter("lang", platformRequest.getLang());
        }
        // psub param
        if (platformRequest.getPsub() != null) {
            builder.appendQueryParameter("psub", platformRequest.getPsub());
        }
        // news param (Outbrain News)
        if (platformRequest.getNews() != null) {
            builder.appendQueryParameter("news", platformRequest.getNews());
        }
        // newsFrom param (Outbrain News)
        if (platformRequest.getNewsFrom() != null) {
            builder.appendQueryParameter("newsFrom", platformRequest.getNewsFrom());
        }
    }

    private void handleVersion(Uri.Builder builder) {
        builder.appendQueryParameter("version", Outbrain.SDK_VERSION);
    }

    private void handleRandom(Uri.Builder builder) {
        String randomInt =  Integer.toString(new Random().nextInt(10000));
        builder.appendQueryParameter("rand", randomInt);
    }

    private void handleFormat(Uri.Builder builder) {
        builder.appendQueryParameter("format", "vjnc");
    }

    private void handleIdx(OBRequest request, Uri.Builder builder) {
        if (request.isMultivac()) {
            builder.appendQueryParameter("feedIdx", Integer.toString(request.getIdx()));
            return;
        }
        builder.appendQueryParameter("idx", Integer.toString(request.getIdx()));
    }

    private void handleKey(Uri.Builder builder) {
        builder.appendQueryParameter("key", localSettings.partnerKey);
    }

    private void handleWidgetId(OBRequest request, Uri.Builder builder) {
        builder.appendQueryParameter("widgetJSId", request.getWidgetId());
    }

    private void handleTestMode(OBRequest request, Uri.Builder builder) {
        if (localSettings.isTestMode()) {
            builder.appendQueryParameter("testMode", "true");

            // Test RTB recs (only in testMode)
            if (localSettings.testRTB()) {
                builder.appendQueryParameter("fakeRec", "RTB-CriteoUS");
                builder.appendQueryParameter("fakeRecSize", "2");
            }
            if (localSettings.getTestLocation() != null) {
                builder.appendQueryParameter("location", localSettings.getTestLocation());
            }
        }
    }

    private void handleToken(OBRequest request, Uri.Builder builder) {
        String token = recommendationsTokenHandler.getTokenForRequest(request);
        if (token != null) {
            builder.appendQueryParameter("t", token);
        }
    }

    private void handleInstallationType(Uri.Builder builder) {
        builder.appendQueryParameter("installationType", "android_sdk");
    }

    private void handleSecureConnection(Uri.Builder builder) {
        builder.appendQueryParameter("secured", "true");
    }

    private void handleReferrer(Uri.Builder builder) {
        builder.appendQueryParameter("ref", "https://app-sdk.outbrain.com/");
    }

    private void handleScreenSize(Context applicationContext, Uri.Builder builder) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);

        double screenInches = Math.sqrt(x+y);

        BigDecimal a = new BigDecimal(screenInches);
        BigDecimal roundOff = a.setScale(1, BigDecimal.ROUND_HALF_EVEN);
        builder.appendQueryParameter("dss", roundOff.toString());
    }

    private void handleDeviceName(Context applicationContext, Uri.Builder builder) {
        String androidModel = "";
        try {
            androidModel = URLEncoder.encode(Build.MODEL, "utf-8");
        } catch (UnsupportedEncodingException ignored) {

        }

        builder.appendQueryParameter("dm", androidModel);
        builder.appendQueryParameter("deviceType", OBUtils.isTablet(applicationContext) ? "tablet" : "mobile");
    }

    private void handleOSName(Uri.Builder builder) {
        builder.appendQueryParameter("dos", "android");
        builder.appendQueryParameter("platform", "android");
    }

    private void handleOSVersion(Uri.Builder builder) {
        builder.appendQueryParameter("dosv", OBUtils.getOSVersion());
    }

    private void handleAppVersion(Context applicationContext, Uri.Builder builder) {
        String appVersion = "";

        try {
            PackageInfo pInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        builder.appendQueryParameter("app_ver", appVersion);
    }

    private void handleAppIdentifier(Context applicationContext, Uri.Builder builder) {
        String appIdentifier = applicationContext.getPackageName();
        builder.appendQueryParameter("app_id", appIdentifier);
    }

    private void handleRTB(Uri.Builder builder) {
        builder.appendQueryParameter("rtbEnabled", "true");
    }

    private void handleGDPR(Context applicationContext, Uri.Builder builder) {
        // GDPR V1
        String consentString;
        if (GDPRUtils.getCmpPresentValue(applicationContext)) {
            consentString = GDPRUtils.getGdprV1ConsentString(applicationContext);
            builder.appendQueryParameter("cnsnt", consentString);
        }
        // GDPR V2
        if (GDPRUtils.getGdprV2ConsentString(applicationContext) != null) {
            consentString = GDPRUtils.getGdprV2ConsentString(applicationContext);
            builder.appendQueryParameter("cnsntv2", consentString);
        }
        // CCPA
        String ccpaString = GDPRUtils.getCcpaString(applicationContext);
        if (!ccpaString.equals("")) {
            builder.appendQueryParameter("ccpa", ccpaString);
        }
        // GPP_SID
        String IAB_GPP_SID = GDPRUtils.getIABGPP_HDR_SectionsKey(applicationContext);
        if(!IAB_GPP_SID.equals("")){
            builder.appendQueryParameter("gpp_sid", IAB_GPP_SID);
        }
        // GPP
        String IAB_GPP_STRING = GDPRUtils.getIABGPP_HDR_GppStringKey(applicationContext);
        if(!IAB_GPP_STRING.equals("")){
            builder.appendQueryParameter("gpp", IAB_GPP_STRING);
        }
    }

    private void handleExternalId(OBRequest request, Uri.Builder builder) {
        if (request.getExternalID() != null) {
            builder.appendQueryParameter("extid", request.getExternalID());
        }

        if (request.getExternalSecondaryId() != null) {
            builder.appendQueryParameter("extid2", request.getExternalSecondaryId());
        }
    }

    private void handlePubImpId(OBRequest request, Uri.Builder builder) {
        if (request.getPubImpId() != null) {
            builder.appendQueryParameter("pubImpId", request.getPubImpId());
        }
    }

    private void handleMultivac(OBRequest request, Uri.Builder builder) {
        if (request.isMultivac()) {
            builder.appendQueryParameter("lastCardIdx", Integer.toString(request.getLastCardIdx()));
            builder.appendQueryParameter("lastIdx", Integer.toString(request.getLastIdx()));
            if (request.getFab() != null) {
                builder.appendQueryParameter("fab", request.getFab());
            }
        }
    }

    private void handleViewabilityActions(Uri.Builder builder) {
        builder.appendQueryParameter("va", "true");
    }

    private void handleIronSource(Uri.Builder builder) {
        if (OutbrainService.getInstance().isIronSourceIntegration()) {
            builder.appendQueryParameter("sdk_aura", "true");
        }
        if (this.localSettings.isIronSourceInstallation()) {
            builder.appendQueryParameter("contextKV", "iron-source");
        }
    }

    private void handleDarkModeFlag(Uri.Builder builder) {
        builder.appendQueryParameter("darkMode", SFThemeImpl.getInstance().isDarkMode() ? "true" : "false");
    }
}
