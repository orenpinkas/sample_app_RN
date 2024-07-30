package com.outbrain.OBSDK.HttpClient;

import android.content.Context;

import okhttp3.OkHttpClient;

public class OBHttpClient {
    private static OkHttpClient httpClient;

    private OBHttpClient() {
        super();
    }

    public static OkHttpClient getClient(Context appContext)
    {
        if (httpClient == null && appContext != null)
        {
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new UserAgentInterceptor(appContext))
                    .build();
        }
        return httpClient;
    }
}
