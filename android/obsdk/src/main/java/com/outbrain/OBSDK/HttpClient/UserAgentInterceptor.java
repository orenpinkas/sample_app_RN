package com.outbrain.OBSDK.HttpClient;

import android.content.Context;
import android.os.Build;
import android.util.Patterns;

import com.outbrain.OBSDK.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by oded on 10/8/15.
 */
class UserAgentInterceptor implements Interceptor {

    private String userAgent;
    private final Context ctx;

    public UserAgentInterceptor(Context ctx) {
        this.userAgent = toHumanReadableAscii(System.getProperty("http.agent"));
        this.ctx = ctx;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (userAgent == null || userAgent.length() == 0) {
            userAgent = getDefaultUserAgent();
        }

        Request originalRequest = chain.request();
        if (! Patterns.WEB_URL.matcher(originalRequest.url().toString()).matches()) {
            throw new IOException("OKHTTP request URL is not valid: " + originalRequest.url().toString());
        }

        Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", userAgent)
                .build();
        return chain.proceed(requestWithUserAgent);
    }

    private String getDefaultUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("Outbrain SDK ");
        result.append(isTablet() ? "Tablet " : "Mobile ");
        result.append("(Android ");

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        result.append(version.length() > 0 ? version : "1.0");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");
        return toHumanReadableAscii(result.toString());
    }

    private boolean isTablet() {
        return ctx.getResources().getBoolean(R.bool.isTablet);
    }

    private String toHumanReadableAscii(String s) {
        if (s == null) {
            return null;
        }
        for (int i = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
            c = s.codePointAt(i);
            if (c > '\u001f' && c < '\u007f') continue;

            Buffer buffer = new Buffer();
            buffer.writeUtf8(s, 0, i);
            for (int j = i; j < length; j += Character.charCount(c)) {
                c = s.codePointAt(j);
                buffer.writeUtf8CodePoint(c > '\u001f' && c < '\u007f' ? c : '?');
            }
            return buffer.readUtf8();
        }
        return s;
    }
}
