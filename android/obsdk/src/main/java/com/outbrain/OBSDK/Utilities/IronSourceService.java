package com.outbrain.OBSDK.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IronSourceService {

    private static final String IRON_SOURCE_SHARED_PREFS_NAME = "IRON_SOURCE_SHARED_PREFS_NAME";
    private static final String IS_IRON_SOURCE_INSTALLATION_PREFS_KEY = "IS_IRON_SOURCE_INSTALLATION_PREFS_KEY";

    public static void verifyIronSourceInstallation(final Context applicationContext, final OBLocalSettings localSettings, ExecutorService recommendationsQueueManager) {
        SharedPreferences mPrefs = applicationContext.getSharedPreferences(IRON_SOURCE_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        if (mPrefs.contains(IS_IRON_SOURCE_INSTALLATION_PREFS_KEY)) {
            boolean isIronSourceInstallation = mPrefs.getBoolean(IS_IRON_SOURCE_INSTALLATION_PREFS_KEY, false);
            Log.i("OBSDK", "verifyIronSourceInstallation found key in shared pref - isIronSourceInstallation: " + isIronSourceInstallation);
            localSettings.setIronSourceInstallation(isIronSourceInstallation);
            return;
        }

        recommendationsQueueManager.submit(new Runnable() {
            @Override
            public void run() {
                verifyIronSourceInstallationWithServer(applicationContext, localSettings);
            }
        });
    }

    private static void verifyIronSourceInstallationWithServer(final Context applicationContext, final OBLocalSettings localSettings) {
        Log.i("OBSDK", "verifyIronSourceInstallationWithServer...");
        AdvertisingIdClient.Info adInfo = OBAdvertiserIdFetcher.getAdvertisingIdInfo(applicationContext);
        String appIdentifier = applicationContext.getPackageName();

        if (adInfo == null || adInfo.isLimitAdTrackingEnabled()) {
            Log.i("OBSDK", "verifyIronSourceInstallationWithServer return false since IDFA is disabled");
            updateSharedPrefsWithIronSource(applicationContext, false);
            localSettings.setIronSourceInstallation(false);
            return;
        }

        //Outbrain.SDK_VERSION;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("sync.outbrain.com");
        builder.appendPath("gaid");
        builder.appendPath("read");
        builder.appendQueryParameter("gaid", adInfo.getId());
        builder.appendQueryParameter("appId", appIdentifier);
        String url = builder.build().toString();

        OkHttpClient httpClient = OBHttpClient.getClient(applicationContext);
        Request request = new Request.Builder().url(url).build();

        Log.i("OBSDK", "verifyIronSourceInstallationWithServer calling: " + url);
        try {
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                Log.e("OBSDK", "verifyIronSourceInstallationWithServer - OKHttp-Error " + url + ": response code: " + response.code());
            }
            else {
                try {
                    String responseString = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseString);
                    Log.i("OBSDK", "verifyIronSourceInstallationWithServer response: " + jsonResponse);
                    boolean isIronSourceInstallation = jsonResponse.getBoolean("status");
                    Log.i("OBSDK", "verifyIronSourceInstallationWithServer isIronSourceInstallation: " + isIronSourceInstallation);
                    Log.i("OBSDK", "verifyIronSourceInstallationWithServer save to SharedPrefs");
                    updateSharedPrefsWithIronSource(applicationContext, isIronSourceInstallation);
                    localSettings.setIronSourceInstallation(isIronSourceInstallation);
                } catch (IOException | JSONException e) {
                    Log.e("OBSDK", "verifyIronSourceInstallationWithServer - Exception when trying to parse response from server: " + e.getLocalizedMessage());
                    OBErrorReporting.getInstance().reportErrorToServer("verifyIronSourceInstallationWithServer - Exception when trying to parse response from server: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ex) {
            Log.e("OBSDK", "OKHttp onFailure in verifyIronSourceInstallationWithServer: " + ex.getLocalizedMessage());
            OBErrorReporting.getInstance().reportErrorToServer("OKHttp onFailure in verifyIronSourceInstallationWithServer: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    private static void updateSharedPrefsWithIronSource(Context appContext, boolean isIronSourceInstallation) {
        SharedPreferences mPrefs = appContext.getSharedPreferences(IRON_SOURCE_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean(IS_IRON_SOURCE_INSTALLATION_PREFS_KEY, isIronSourceInstallation);
        prefsEditor.apply();
    }
}
