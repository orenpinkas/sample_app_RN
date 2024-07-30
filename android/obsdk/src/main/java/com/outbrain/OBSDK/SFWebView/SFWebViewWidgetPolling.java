package com.outbrain.OBSDK.SFWebView;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.squareup.otto.Subscribe;


@SuppressLint("ViewConstructor")
public class SFWebViewWidgetPolling extends SFWebViewWidget {
    /**
     * This subclass substitutes the visibility logic from being based on:
     * scroll-events - for initiation of the visibility calculation
     * position within the parent view - for visibility calculation
     * to being based on polling and position within the viewport.
     *
     * This type of SFWebViewWidget is used by Flutter and React Native.
     **/

    private final String LOG_TAG = "SFWebViewWidgetFlutter";
    private Handler handler;
    private Runnable pollingRunnable;
    private static final int POLLING_INTERVAL = 150;
    private SFWebViewWidgetListener clickListener;

    public SFWebViewWidgetPolling(final Context context, String URL, String widgetID, int widgetIndex, String installationKey, SFWebViewWidgetListener clickListener, SFWebViewEventsListener eventListener, boolean darkMode, String extId, String extSecondaryId, String pubImpId) {
        super(context, URL, widgetID, widgetIndex, installationKey, clickListener, darkMode);
        this.clickListener = clickListener;
        setParamsDelegate(createParamsDelegateObject(extId, extSecondaryId, pubImpId));
        super.setSfWebViewEventsListener(eventListener);
        super.enableEvents();
        infiniteWidgetsOnTheSamePage = true;

        // initiate polling that updates the visibility of the widget
        handler = new Handler();
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                handleVisibility();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        handler.post(pollingRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      handler.removeCallbacks(pollingRunnable);
    }

    @Override
    void notifyHeightChanged(int height) {
      super.notifyHeightChanged(height);
      clickListener.didChangeHeight(height);
    }

    @Override
    protected void openURLInBrowserWrapper(String url, final Context ctx) {
        // don't open a browser in native code - propagate the event to flutter instead
        clickListener.onRecClick(url);
    }

    private void handleVisibility() {
        try {
            _handleVisibility();
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Something went wrong in handleVisibility() " + ex.getLocalizedMessage());
        }
    }
    private void _handleVisibility() {
        // calculate the widget size and position on the screen
        RectF widgetRect = calculateRectOnScreen(this);

        // calculate the viewport size
        float viewportHeight = getResources().getDisplayMetrics().heightPixels;
        float viewportWidth = getResources().getDisplayMetrics().widthPixels;
        RectF viewportRect = new RectF(0f, 0f, viewportWidth, viewportHeight);

        // calculate visibilty w.r.t the viewport
        SFWebViewWidgetVisibility viewVisibility = getViewVisibility(widgetRect, viewportRect);

        this.updateVisibility(viewVisibility);
    }

    private SFWebViewWidgetVisibility getViewVisibility(RectF oneRect, RectF containerRect) {

        float distanceFromTop = containerRect.top - oneRect.top;
        float distanceFromBottom = oneRect.bottom - containerRect.bottom;

        int containerViewHeight = (int) containerRect.bottom;

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

    @Override
    @Subscribe
    public void receivedBridgeParamsEvent(OutbrainBusProvider.BridgeParamsEvent event) {
        super.receivedBridgeParamsEvent(event);
    }

    @Override
    @Subscribe
    public void receivedTParamsEvent(OutbrainBusProvider.TParamsEvent event) {
        super.receivedTParamsEvent(event);
    }

    private SFWebViewParamsDelegate createParamsDelegateObject(String extId, String extSecondaryId, String pubImpId) {
        return new SFWebViewParamsDelegate() {
            @Override
            public String getExternalId() {
                return extId;
            }
            @Override
            public String getExternalSecondaryId() {
                return extSecondaryId;
            }

            @Override
            public String getPubImpId() {
                return pubImpId;
            }
        };
    }
}