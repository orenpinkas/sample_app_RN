package com.outbrain.OBSDK.SFWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;
import java.util.Objects;

@SuppressLint("ViewConstructor")
public class SFWebViewWidgetPollingTestsMode extends SFWebViewWidgetPolling {
   private final HttpHandler httpHandler;

   public interface HttpHandler {
      void handleRequest(String type, Map<String, Object> request);
   }

   public SFWebViewWidgetPollingTestsMode(Context context, String URL, String widgetID, int widgetIndex, String installationKey, SFWebViewWidgetListener clickListener, SFWebViewEventsListener eventListener, boolean darkMode, String extId, String extSecondaryId, String pubImpId, HttpHandler httpHandler) {
      super(context, URL, widgetID, widgetIndex, installationKey, clickListener, eventListener, darkMode, extId, extSecondaryId, pubImpId);
      this.httpHandler = httpHandler;
   }

   @Override
   protected void setWebViewSettings() {
      super.setWebViewSettings();
      this.addJavascriptInterface(new HttpHandlerInterface(), HTTP_HANDLER_INTERFACE);

      // Set a WebViewClient to inject the script after page load
      this.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            injectFetchMonitoringScript();
         }
      });
   }

   private static final String HTTP_HANDLER_INTERFACE = "HttpHandler";
   private void injectFetchMonitoringScript() {
      String httpOverrideScript =
              "  function wrapHttpMethod(originalMethod) {\n" +
                      "    return function(input, init) {\n" +
                      "        const url = typeof input === \"string\" ? input : input.url;\n" +
                      "        if (url.includes(\"outbrain.com\") || url.includes(\"log.outbrainimg.com\")) {\n" +
                      "        console.log(\"Fetch request:\", url, init);\n" +
                      HTTP_HANDLER_INTERFACE + ".handleHttpRequest(url, JSON.stringify(init));" +
                      "        return originalMethod.apply(this, arguments).then((response) => {\n" +
                      "          response\n" +
                      "            .clone()\n" +
                      "            .text()\n" +
                      "            .then((bodyText) => {\n" +
                      "              console.log(\"Fetch response:\", url, response.status, bodyText);\n" +
                      HTTP_HANDLER_INTERFACE + ".handleHttpResponse(url, JSON.stringify(init));" +
                      "            });\n" +
                      "          return response;\n" +
                      "        });\n" +
                      "        }\n" +
                      "        return originalMethod.apply(this, arguments);\n" +
                      "    };\n" +
                      "}\n" +
                      "  window.fetch = wrapHttpMethod(window.fetch);\n" +
                      "  navigator.sendBeacon = wrapHttpMethod(navigator.sendBeacon);\n";

      this.evaluateJavascript(httpOverrideScript, null);

   }

   private class HttpHandlerInterface {
      @JavascriptInterface
      public void handleHttpRequest(String url, String options) {
         Log.d(HTTP_HANDLER_INTERFACE, "http request to: " + url + " with options: " + options);

         // Parse the URL string
         Uri uri = Uri.parse(url);

         // Get various components of the URL
         String host = uri.getHost() != null ? uri.getHost() : "";
         String path = uri.getPath() != null ? uri.getPath() : "";
         String query = uri.getQuery() != null ? uri.getQuery() : "";

         Map<String, Object> request = Map.of(
                 "url", url,
                 "host", host,
                 "path", path,
                 "query", query,
                 "options", options
         );
         switch (Objects.requireNonNull(host)) {
            case "log.outbrainimg.com":
               SFWebViewWidgetPollingTestsMode.this.httpHandler.handleRequest("viewability", request);
               break;
         }

      }
   }
}
