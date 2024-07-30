package com.outbrain.OBSDK.SFWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.outbrain.OBSDK.Errors.OBErrorReporting;
import com.outbrain.OBSDK.GDPRUtils.GDPRUtils;
import com.outbrain.OBSDK.OBUtils;
import com.outbrain.OBSDK.Outbrain;
import com.outbrain.OBSDK.OutbrainException;
import com.outbrain.OBSDK.R.styleable;
import com.outbrain.OBSDK.Utilities.OBAdvertiserIdFetcher;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.outbrain.OBSDK.OBUtils.getAppIdentifier;
import static com.outbrain.OBSDK.OBUtils.getApplicationName;
import static com.outbrain.OBSDK.OBUtils.runOnMainThread;


public class SFWebViewWidget extends WebView {
    private final WeakReference<Context> ctxRef;
    private String URL;
    private String widgetID;
    private int widgetIndex;
    private String installationKey;
    private boolean darkMode;
    public static boolean isWidgetEventsEnabled;
    public static boolean isWidgetEventsTestMode;
    public static boolean shouldCollapseOnError;
    private static WeakReference<SFWebViewNetworkDelegate> networkDelegateRef = new WeakReference<>(null);;
    private static WeakReference<SFWebViewParamsDelegate> paramsDelegateWeakReference = new WeakReference<>(null);

    private static WeakReference<SFWebViewHeightDelegate> heightDelegateWeakReference = new WeakReference<>(null);

    // platforms
    public static boolean usingPortalUrl;
    public static boolean usingBundleUrl;
    public static boolean usingContentUrl;
    public static String lang; // mandatory field for platforms
    public static String psub; // optional field for platforms
    public static String news; // optional field for platforms, for example "IAB1,IAB3,IAB10"
    public static String newsFrom; // optional field for platforms, for example "es"

    private String tParam;
    private String bridgeParams;

    // This will be used by IS for cases where we'll have infinite Bridge widgets on the same page
    public static String globalBridgeParams;
    public static boolean infiniteWidgetsOnTheSamePage;

    private String userId;
    private boolean isLoadingMoreItems;

    private static boolean isFlutter = false;
    private static boolean isReactNative = false;
    private static String flutter_packageVersion;
    private static String RN_packageVersion;

    private boolean notifieidRecsReceived;
    private boolean isAttachedToWindow;
    private boolean isRegisteredToOttoBus;
    private SFWebViewClickListener sfWebViewClickListener;
    private SFWebViewEventsListener sfWebViewEventsListener;

    private final String LOG_TAG = "SFWebViewWidget";

    public static class SFWebViewWidgetVisibility {
        private final int visibleFrom;
        private final int visibleTo;

        public SFWebViewWidgetVisibility(int from, int to) {
            this.visibleFrom = from;
            this.visibleTo = to;
        }
    }

    public SFWebViewWidget(ViewGroup containerView, String URL, String widgetID, String installationKey) {
        this(containerView, URL, widgetID, installationKey, null);
    }

    public SFWebViewWidget(final ViewGroup containerView, String URL, String widgetID, String installationKey, SFWebViewClickListener sfWebViewClickListener) {
        this(containerView, URL, widgetID, 0, installationKey, sfWebViewClickListener, false);
    }

    public SFWebViewWidget(final ViewGroup containerView, String URL, String widgetID, int widgetIndex, String installationKey, SFWebViewClickListener sfWebViewClickListener, boolean darkMode) {
        this(containerView.getContext(), URL, widgetID, widgetIndex, installationKey, sfWebViewClickListener, darkMode);

        setOnScrollChangedListener(containerView);
    }

    public SFWebViewWidget(final Context context, String URL, String widgetID, int widgetIndex, String installationKey, SFWebViewClickListener sfWebViewClickListener, boolean darkMode) {
        super(context);
        this.ctxRef = new WeakReference<>(context.getApplicationContext());
        this.URL = URL;
        this.widgetID = widgetID;
        this.widgetIndex = widgetIndex;
        this.installationKey = installationKey;
        this.darkMode = darkMode;
        this.sfWebViewClickListener = sfWebViewClickListener;

        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        commonInit();
    }

    public SFWebViewWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SFWebViewWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ctxRef = new WeakReference<>(context.getApplicationContext());
        parseXmlAttributes(context, attrs);
    }

    // SFWidget in XML
    public void init(ViewGroup containerView, String URL) {
        this.init(containerView, URL, widgetID, widgetIndex, installationKey, false);
    }

    // SFWidget in XML
    public void init(final ViewGroup containerView, String URL, String widgetID, int widgetIndex, String installationKey, boolean darkMode) {
        this.URL = URL;
        if (widgetID != null) {
            this.widgetID = widgetID;
        }
        if (installationKey != null) {
            this.installationKey = installationKey;
        }
        if (widgetIndex != 0) {
            this.widgetIndex = widgetIndex;
        }

        if (darkMode) {
            this.darkMode = darkMode;
        }

        setOnScrollChangedListener(containerView);

        commonInit();

        evaluateHeightScript(200);
    }

    // SFWidget in XML
    public void setSfWebViewClickListener(SFWebViewClickListener sfWebViewClickListener) {
        this.sfWebViewClickListener = sfWebViewClickListener;
    }

    public void onActivityConfigurationChanged() {
        evaluateHeightScript(300);
    }

    private void commonInit() {
        final Context ctx = ctxRef.get();
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setFocusableInTouchMode(false);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        // Set Error Reporting Values
        OBErrorReporting.init(ctx,this.installationKey);
        OBErrorReporting.getInstance().setWidgetId(this.widgetID);
        OBErrorReporting.getInstance().setOdbRequestUrlParamValue(this.URL);

        // Fix for crash in Chromium loading video with a missing poster image
        // See https://stackoverflow.com/questions/46886701/webview-crashes-when-displaying-a-youtube-video
        this.setWebChromeClient(new WebChromeClient() {
            @Nullable
            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
            }
        });
        if (shouldCollapseOnError) {
            setWebViewClientListener();
        }

        // Now load the Bridge URL into the WebView
        if (ctx != null) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    AdvertisingIdClient.Info adInfo = OBAdvertiserIdFetcher.getAdvertisingIdInfo(ctx);
                    if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                        SFWebViewWidget.this.userId = adInfo.getId();
                    }

                    if (shouldCollapseOnError && SFWebViewWidget.networkDelegateRef.get() != null && SFWebViewWidget.networkDelegateRef.get().checkInternetConnection() == false) {
                        Log.e("OBSDK", "SFWebViewWidget - no-connection - skip load URL");
                        OBUtils.runOnMainThread(ctx, new Runnable() {
                            @Override
                            public void run() {
                                SFWebViewWidget.this.getLayoutParams().height = 0;
                                SFWebViewWidget.this.requestLayout();
                            }
                        });
                        return;
                    }

                    if (SFWebViewWidget.this.widgetIndex > 0) {
                        if (SFWebViewWidget.infiniteWidgetsOnTheSamePage && (SFWebViewWidget.globalBridgeParams != null)) {
                            Log.i(LOG_TAG, "using infiniteWidgetsOnTheSamePage flag + globalBridgeParams is available");
                            SFWebViewWidget.this.loadOutbrainURLOnMainThread(ctx);
                        }
                        else {
                            Log.i(LOG_TAG, "Differ fetching until we'll have the \"bridgeParams\" from the first widget. Waiting for Bus event...");
                            OutbrainBusProvider.getInstance().register(SFWebViewWidget.this);
                            SFWebViewWidget.this.isRegisteredToOttoBus = true;
                        }
                    } else {
                        SFWebViewWidget.globalBridgeParams = null;
                        SFWebViewWidget.this.loadOutbrainURLOnMainThread(ctx);
                    }
                }
            });
        }
    }

    private void setWebViewClientListener() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                try {
                    Uri url = request.getUrl();
                    Log.i("OBSDK", "shouldOverrideUrlLoading: " + (url != null ? url.toString() : ""));
                    final Context sfWidgetContext = SFWebViewWidget.this.ctxRef.get();
                    if (sfWidgetContext == null) {
                        OBErrorReporting.getInstance().reportErrorToServer("shouldOverrideUrlLoading() - sfWidgetContext is null");
                        return false;
                    }
                    if (url == null || !URLUtil.isValidUrl(String.valueOf(url))) {
                        OBErrorReporting.getInstance().reportErrorToServer("shouldOverrideUrlLoading() - invalid url: " + url);
                        return false;
                    }
                    boolean targetFrame = request.isForMainFrame();
                    String host = url.getHost();
                    String scheme = url.getScheme();
                    if (targetFrame && host != null && scheme.equals("https") && !host.contains("widgets.outbrain.com")) {
                        Log.i("OBSDK","shouldOverrideUrlLoading() will open URL in external browser: " + url);
                        openURLInBrowser(String.valueOf(url), sfWidgetContext);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    OBErrorReporting.getInstance().reportErrorToServer("shouldOverrideUrlLoading error (display supported ) " + e.getMessage());
                    return false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("OBSDK", "SFWebViewWidget failed to load: " + description);
                if (!shouldCollapseOnError || !failingUrl.contains("widgets.outbrain.com")) {
                    return;
                }
                if (errorCode == ERROR_CONNECT || errorCode == ERROR_HOST_LOOKUP) {
                    SFWebViewWidget.this.getLayoutParams().height = 0;
                    SFWebViewWidget.this.requestLayout();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("OBSDK", "SFWebViewWidget onPageFinished");
                if (SFWebViewWidget.shouldCollapseOnError && SFWebViewWidget.networkDelegateRef.get() != null) {
                    Log.i("OBSDK", "SFWebViewWidget onPageFinished - checkInternetConnection()");
                    if (SFWebViewWidget.networkDelegateRef.get().checkInternetConnection() == false) {
                        Log.e("OBSDK", "SFWebViewWidget onPageFinished - no-connection");
                        SFWebViewWidget.this.getLayoutParams().height = 0;
                        SFWebViewWidget.this.requestLayout();
                    }
                }
            }
        });
    }

    private void loadOutbrainURLOnMainThread(Context ctx) {
        final String url = buildWidgetUrl(ctx);

        Handler mainHandler = new Handler(ctx.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                setWebViewSettings();
                Log.i(LOG_TAG, "WebView loadUrl() --> " + url);
                SFWebViewWidget.this.loadUrl(url);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void setOnScrollChangedListener(final ViewGroup containerView) {
        this.getViewTreeObserver().addOnScrollChangedListener(new SFScrollChangedListener(this, containerView));
    }

    private static class SFScrollChangedListener implements ViewTreeObserver.OnScrollChangedListener {
        WeakReference<ViewGroup> containerViewWeakReference;
        WeakReference<SFWebViewWidget> sfWebViewWidgetWeakReference;

        public SFScrollChangedListener(SFWebViewWidget sfWebViewWidget, ViewGroup containerView) {
            this.containerViewWeakReference = new WeakReference<>(containerView);
            this.sfWebViewWidgetWeakReference = new WeakReference<>(sfWebViewWidget);
        }

        @Override
        public void onScrollChanged() {
            if (containerViewWeakReference.get() != null && sfWebViewWidgetWeakReference.get() != null) {
                sfWebViewWidgetWeakReference.get().onViewScrollChanged(containerViewWeakReference.get());
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        evaluateHeightScript(200);
        isAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        if (this.widgetIndex > 0 && this.isRegisteredToOttoBus) {
            synchronized (this) {
                if (this.isRegisteredToOttoBus) {
                    OutbrainBusProvider.getInstance().unregister(this);
                    this.isRegisteredToOttoBus = false;
                }
            }
        }
    }

    void notifyRecsReceivedIfNeeded() {
        if (notifieidRecsReceived) {
            return;
        }
        Log.i(LOG_TAG, "notifyRecsReceived via OttoBusProvider");
        OutbrainBusProvider.BridgeRecsReceivedEvent event = new OutbrainBusProvider.BridgeRecsReceivedEvent(URL, widgetID, widgetIndex);
        OutbrainBusProvider.getInstance().post(event);
        this.notifieidRecsReceived = true;
    }

    void notifyHeightChanged(int height) {
        Log.i(LOG_TAG, "notifyHeightChange via OttoBusProvider: " + height);
        OutbrainBusProvider.HeightChangeEvent event = new OutbrainBusProvider.HeightChangeEvent(URL, widgetID, widgetIndex, height);
        OutbrainBusProvider.getInstance().post(event);
    }

    void finishUpdatingHeight() {
        if (!isLoadingMoreItems) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoadingMoreItems = false;
            }
        },1000);
    }

    public void loadMore() {
        if (!isLoadingMoreItems) {
            isLoadingMoreItems = true;
            evaluateLoadMoreScript();
        }
    }

    public void enableEvents() {
        this.isWidgetEventsEnabled = true;
    }

    public void testModeAllEvents() {
        this.isWidgetEventsTestMode = true;
    }

    public void toggleDarkMode(boolean displayDark) {
        if (this.ctxRef.get() != null) {
            runOnMainThread(this.ctxRef.get(), new Runnable() {
                @Override
                public void run() {
                    evaluateToggleDarkMode(displayDark);
                }
            });
        }
    }

    private void evaluateHeightScript(int timeout) {
        String script =
                "setTimeout(function() {" +
                    "OBBridge.resizeHandler.getCurrentHeight();" +
                 "}," + timeout + ")";
        this.evaluateScript(script);
    }

    private void evaluateLoadMoreScript() {
        Log.i(LOG_TAG, "load more ---->");

        String script = "OBR.viewHandler.loadMore(); true;";
        this.evaluateScript(script);

        this.evaluateHeightScript(500);
    }

    private void evaluateToggleDarkMode(boolean displayDark) {
        Log.i(LOG_TAG, "Toggle DarkMode: " + displayDark);
        String script = "OBBridge.darkModeHandler.setDarkMode(" + displayDark + ");";
        this.evaluateScript(script);
    }

    private void evaluateViewabilityScriptFor(SFWebViewWidgetVisibility viewVisibility) {
        String script = "OBBridge.viewHandler.setViewData(" +
                getMeasuredWidth() + ", " + // totalWidth
                getMeasuredHeight() + ", " + // totalHeight
                viewVisibility.visibleFrom + ", " + // visibleFrom
                viewVisibility.visibleTo + ")"; // visibleTo

        this.evaluateScript(script);
    }

    private void evaluateScript(String script) {
        this.evaluateJavascript(script, null);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void setWebViewSettings() {
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setUseWideViewPort(true);
        this.addJavascriptInterface(new SFWebViewJSInterface(this, this.getContext(), widgetID), "ReactNativeWebView");

//         for debugging
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.setWebContentsDebuggingEnabled(true);
//        }
    }

    private String buildWidgetUrl(Context ctx) {
        if (this.URL == null || this.widgetID == null) {
            Log.e(LOG_TAG,"SFWidget - url and widgetId are mandatory attributes, at least one of them is missing");
        }
        String appName = getApplicationName(ctx);
        String appBundleId = getAppIdentifier(ctx);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.appendPath("widgets.outbrain.com");
        builder.appendPath("reactNativeBridge");
        builder.appendPath("index.html");

        // handle URL for regualr requests and for platforms API (started Nov 2022)
        if (SFWebViewWidget.usingBundleUrl || SFWebViewWidget.usingPortalUrl) {
            // first verify that mandatory param "lang" is set
            if (SFWebViewWidget.lang == null) {
                throw new OutbrainException("It seems you set Bridge to run with platform API and did NOT set the mandatory (language) *SFWebViewWidget.lang* static param.");
            }
            String urlParamKey = SFWebViewWidget.usingBundleUrl ? "bundleUrl" : "portalUrl";
            builder.appendQueryParameter(urlParamKey, this.URL);
            builder.appendQueryParameter("lang", SFWebViewWidget.lang);
            if (SFWebViewWidget.psub != null) {
                builder.appendQueryParameter("psub", SFWebViewWidget.psub);
            }
            if (SFWebViewWidget.news != null) {
                builder.appendQueryParameter("news", SFWebViewWidget.news);
            }
            if (SFWebViewWidget.newsFrom != null) {
                builder.appendQueryParameter("newsFrom", SFWebViewWidget.newsFrom);
            }
        }
        else if (SFWebViewWidget.usingContentUrl) { // this is another platform API option
            builder.appendQueryParameter("contentUrl", this.URL);
            if (SFWebViewWidget.psub != null) {
                builder.appendQueryParameter("psub", SFWebViewWidget.psub);
            }
            if (SFWebViewWidget.news != null) {
                builder.appendQueryParameter("news", SFWebViewWidget.news);
            }
            if (SFWebViewWidget.newsFrom != null) {
                builder.appendQueryParameter("newsFrom", SFWebViewWidget.newsFrom);
            }
        }
        else { // this will be used 99% of time (unless publisher uses platform API)
            builder.appendQueryParameter("permalink", this.URL);
        }

        builder.appendQueryParameter("widgetId", this.widgetID);
        builder.appendQueryParameter("sdkVersion", Outbrain.SDK_VERSION);

        if (this.isWidgetEventsEnabled) {
            builder.appendQueryParameter("widgetEvents", "all");
        }
        else if (this.isWidgetEventsTestMode) {
            builder.appendQueryParameter("widgetEvents", "test");
        }

        if (this.installationKey != null) {
            builder.appendQueryParameter("installationKey", this.installationKey);
        }

        if (this.widgetIndex != 0) {
            builder.appendQueryParameter("idx", String.valueOf(this.widgetIndex));
        }

        if (this.tParam != null) {
            builder.appendQueryParameter("t", this.tParam);
        }
        if (this.bridgeParams != null) {
            builder.appendQueryParameter("bridgeParams", this.bridgeParams);
        }
        if (infiniteWidgetsOnTheSamePage && globalBridgeParams != null) {
            builder.appendQueryParameter("bridgeParams", globalBridgeParams);
        }

        if (this.userId != null) {
            builder.appendQueryParameter("userId", this.userId);
        }

        if (this.darkMode) {
            builder.appendQueryParameter("darkMode", "true");
        }

        if (isFlutter) {
            builder.appendQueryParameter("flutter", "true");
            builder.appendQueryParameter("flutterPackageVersion", flutter_packageVersion);
        }

        if (isReactNative) {
            builder.appendQueryParameter("reactNative", "true");
            builder.appendQueryParameter("reactNativePackageVersion", RN_packageVersion);
        }

        // Video support
        builder.appendQueryParameter("platform", "android");
        builder.appendQueryParameter("inApp", "true");
        builder.appendQueryParameter("appName", appName);
        builder.appendQueryParameter("appBundle", appBundleId);

        // additional params
        builder.appendQueryParameter("deviceType", OBUtils.isTablet(ctx) ? "tablet" : "mobile");
        builder.appendQueryParameter("dosv", OBUtils.getOSVersion());

        // Viewability
        builder.appendQueryParameter("viewData", "enabled");

        // GDPR V1
        String consentString;
        if (GDPRUtils.getCmpPresentValue(ctx)) {
            consentString = GDPRUtils.getGdprV1ConsentString(ctx);
            builder.appendQueryParameter("cnsnt", consentString);
        }
        // GDPR V2
        if (GDPRUtils.getGdprV2ConsentString(ctx) != null) {
            consentString = GDPRUtils.getGdprV2ConsentString(ctx);
            builder.appendQueryParameter("cnsntv2", consentString);
        }

        // Check for more params with SFWebViewParamsDelegate
        if (SFWebViewWidget.paramsDelegateWeakReference.get() != null) {
            SFWebViewParamsDelegate delegate = SFWebViewWidget.paramsDelegateWeakReference.get();
            // External ID
            if (delegate.getExternalId() != null) {
                builder.appendQueryParameter("extid", delegate.getExternalId());
            }
            // External Secondary ID
            if (delegate.getExternalSecondaryId() != null) {
                builder.appendQueryParameter("extid2", delegate.getExternalSecondaryId());
            }
            // pubImp ID
            if (delegate.getPubImpId() != null) {
                builder.appendQueryParameter("pubImpId", delegate.getPubImpId());
            }
        }

        String ccpaString = GDPRUtils.getCcpaString(ctx);
        if (!ccpaString.equals("")) {
            builder.appendQueryParameter("ccpa", ccpaString);
        }
        // GPP_SID
        String IAB_GPP_SID = GDPRUtils.getIABGPP_HDR_SectionsKey(ctx);
        if(!IAB_GPP_SID.equals("")){
            builder.appendQueryParameter("gpp_sid", IAB_GPP_SID);
        }
        // GPP
        String IAB_GPP_STRING = GDPRUtils.getIABGPP_HDR_GppStringKey(ctx);
        if(!IAB_GPP_STRING.equals("")){
            builder.appendQueryParameter("gpp", IAB_GPP_STRING);
        }

        return builder.build().toString();
    }

    private void parseXmlAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, styleable.SFWebViewWidget, 0, 0);
        this.widgetID = a.getString(styleable.SFWebViewWidget_ob_widget_id);
        this.installationKey = a.getString(styleable.SFWebViewWidget_ob_installation_key);
        this.widgetIndex = a.getInt(styleable.SFWebViewWidget_ob_widget_index, 0);
        a.recycle();
    }

    protected RectF calculateRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }

    private SFWebViewWidgetVisibility getViewVisibility(ViewGroup containerView) {
        RectF oneRect = calculateRectOnScreen(this);
        RectF otherRect = calculateRectOnScreen(containerView);

        float distanceFromTop = otherRect.top - oneRect.top;
        float distanceFromBottom = oneRect.bottom - otherRect.bottom;

        int containerViewHeight = containerView.getMeasuredHeight();

        int visibleFrom;
        int visibleTo;
        if (distanceFromTop < 0) { // top
            visibleFrom = 0;
            visibleTo = containerViewHeight + (int) distanceFromTop;
        } else if (distanceFromBottom < 0) { // bottom
            visibleFrom = getMeasuredHeight() - (containerViewHeight + (int) distanceFromBottom);
            visibleTo = getMeasuredHeight();
        } else { // full
            visibleFrom = (int) distanceFromTop;
            visibleTo = (int) distanceFromTop + containerViewHeight;
        }

        return new SFWebViewWidgetVisibility(visibleFrom, visibleTo);
    }

    public void onViewScrollChanged(ViewGroup containerView) {
        if (!isInViewPort()) {
            return;
        }

        SFWebViewWidgetVisibility viewVisibility = getViewVisibility(containerView);

        this.updateVisibility(viewVisibility);
    }

    // Public use only for Compose plugin
    public void updateVisibility(SFWebViewWidgetVisibility viewVisibility) {
        this.handleLoadMoreItemsFor(viewVisibility);
        this.handleViewabilityFor(viewVisibility);
    }

    private void handleLoadMoreItemsFor(SFWebViewWidgetVisibility viewVisibility) {
        int visibleTo = viewVisibility.visibleTo;

        int diffFromEnd = getMeasuredHeight() - visibleTo;

        if (diffFromEnd >= 0 && diffFromEnd < 1000) {
            this.loadMore();
        }
    }

    private boolean isInViewPort() {
        Rect bounds = new Rect();
        return getLocalVisibleRect(bounds) && isAttachedToWindow;
    }

    private void handleViewabilityFor(SFWebViewWidgetVisibility viewVisibility) {
        this.evaluateViewabilityScriptFor(viewVisibility);
    }

    @Subscribe
    public void receivedBridgeParamsEvent(OutbrainBusProvider.BridgeParamsEvent event) {
        Log.i(LOG_TAG, "receivedBridgeParamsEvent: " + event.getBridgeParams());
        this.bridgeParams = event.getBridgeParams();
        this.loadOutbrainURLOnMainThread(this.ctxRef.get());
        synchronized (this) {
            if (this.isRegisteredToOttoBus) {
                OutbrainBusProvider.getInstance().unregister(this);
                this.isRegisteredToOttoBus = false;
            }
        }
    }

    @Subscribe // This event can only be dispatched from the "regular SDK" (in the Bridge we will post BridgeParamsEvent)
    public void receivedTParamsEvent(OutbrainBusProvider.TParamsEvent event) {
        Log.i(LOG_TAG, "receivedBridgeParamsEvent: " + event.getTParam());
        this.tParam = event.getTParam();
        this.loadOutbrainURLOnMainThread(this.ctxRef.get());
        synchronized (this) {
            if (this.isRegisteredToOttoBus) {
                OutbrainBusProvider.getInstance().unregister(this);
                this.isRegisteredToOttoBus = false;
            }
        }
    }

    void handleClickOnUrl(String url, String orgUrl) {
        Context ctx = ctxRef.get();
        if (URLUtil.isValidUrl(url)) {
            if (orgUrl != null && sfWebViewClickListener != null) {
                String trafficStringUrl = Uri.parse(url).buildUpon().scheme("https").appendQueryParameter("noRedirect", "true").toString();
                try {
                    URL trafficUrl = new URL(trafficStringUrl);

                    HttpURLConnection httpConn = (HttpURLConnection)trafficUrl.openConnection();
                    int responseCode = httpConn.getResponseCode();
                    Log.i(LOG_TAG, "report organic click response code: " + responseCode);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error reporting organic click: " + trafficStringUrl + " error: " + e.getMessage());
                    OBErrorReporting.getInstance().reportErrorToServer("Error reporting organic click: " + trafficStringUrl + " error: " + e.getMessage());

                }
                OBUtils.runOnMainThread(ctx, new Runnable() {
                    @Override
                    public void run() {
                        SFWebViewWidget.this.sfWebViewClickListener.onOrganicClick(orgUrl);
                    }
                });
            } else {
                if (ctx != null) {
                    openURLInBrowserWrapper(url, ctx);
                }
            }
        } else {
            Log.e(LOG_TAG,"click on invalid url - " + url);
        }
    }

    void handleWidgetEvent(String eventName, JSONObject additionalData) {
        if (this.sfWebViewEventsListener != null) {
            this.sfWebViewEventsListener.onWidgetEvent(eventName, additionalData);
        }
    }

    private static void openURLInBrowser(String url, final Context ctx) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        customTabsIntent.launchUrl(ctx, uri);
    }

    protected void openURLInBrowserWrapper(String url, final Context ctx) {
        // to keep openURLInBrowser static, it is wrapped with a non-static wrapper that can be overriden in subclasses
        openURLInBrowser(url, ctx);
    }

    public void setSfWebViewEventsListener(SFWebViewEventsListener sfWebViewEventsListener) {
        this.sfWebViewEventsListener = sfWebViewEventsListener;
    }

    public static void setNetworkDelegate(SFWebViewNetworkDelegate sfWebViewNetworkDelegate) {
        SFWebViewWidget.networkDelegateRef = new WeakReference<>(sfWebViewNetworkDelegate);
    }

    public static void setParamsDelegate(SFWebViewParamsDelegate sfWebViewParamsDelegate) {
        SFWebViewWidget.paramsDelegateWeakReference = new WeakReference<>(sfWebViewParamsDelegate);
    }

    public static void enableFlutterMode() {
        isFlutter = true;
    }

    public static void enableReactNativeMode(String RN_packageVersion) {
        isReactNative = true;
    }

    public static void setHeightDelegateWeakReference(SFWebViewHeightDelegate sfWebViewHeightDelegate) {
        SFWebViewWidget.heightDelegateWeakReference = new WeakReference<>(sfWebViewHeightDelegate);
    }

    public static WeakReference<SFWebViewHeightDelegate> getHeightDelegateWeakReference() {
        return heightDelegateWeakReference;
    }
}
