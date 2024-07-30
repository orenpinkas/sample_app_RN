package com.outbrain.OBSDK.Utilities;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBRecommendationImpl;
import com.outbrain.OBSDK.HttpClient.OBHttpClient;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdsChoicesManager {


    private static final String TAG = "AdsChoicesManager";

    public static void reportViewability(OBRecommendationsResponse response, Context context) {
        ArrayList<OBRecommendation> recommendations = response.getAll();
        for (OBRecommendation rec : recommendations) {
            OBRecommendationImpl recImpl = (OBRecommendationImpl) rec;
            if (recImpl.getPixels() != null) {
                for (String pixelUrl : recImpl.getPixels()) {
                    if ( Patterns.WEB_URL.matcher(pixelUrl).matches()) {
                        sendAdChoicesViewabilityDataToServer(pixelUrl, context);
                    }
                    else {
                        Log.e("OBSDK", "reportViewability - Url is not valid: " + pixelUrl);
                    }
                }
            }
        }
    }

    private static void sendAdChoicesViewabilityDataToServer(String url, Context appContext) {
        OkHttpClient httpClient = OBHttpClient.getClient(appContext);

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error in sendAdChoicesViewabilityDataToServer: " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error in sendAdChoicesViewabilityDataToServer Unexpected response code: " + response.code());
                }
                if (response.body() != null) {
                    response.body().close();
                }
            }
        });
    }

    public interface AdsChoicesListener {
        void onAdsImageReady();
    }

}
