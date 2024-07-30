package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Timer;
import java.util.TimerTask;

public class SFAutoScrollRecyclerView extends RecyclerView {
    private boolean isAutoScrolling = false;
    private Timer autoScrollTimer;

    private boolean mReady = false;

    static final String TAG = "SFAutoScrollRecyclerView";


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.isAttachedToWindow()) {
            if (visibility == View.VISIBLE) {
                this.startAutoScroll();
            } else {
                this.stopAutoScroll();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopAutoScroll();
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        if (RecyclerView.SCROLL_STATE_DRAGGING == newState) { // on user scroll
            stopAutoScroll();
        } else {
            startAutoScroll();
        }
    }

    public SFAutoScrollRecyclerView(Context context) {
        this(context, null);
    }

    public SFAutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SFAutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // set layout manager
        setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        mReady = true;
    }

    public void startAutoScroll() {
        if (!mReady) {
            Log.e(TAG, "please set the adapter before started scroll");
            return;
        }

        if (isAutoScrolling) {
            return;
        }

        // animate the RecyclerView
        final Handler mainHandler = new Handler(this.getContext().getMainLooper());

        final Runnable scrollRunnable = new Runnable() {
            @Override
            public void run() {
                scrollBy(2, 0);
            }
        };
        autoScrollTimer = new Timer();
        autoScrollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(scrollRunnable);
            }
        }, 0, 45); // run every 45ms

        isAutoScrolling = true;
    }

    public void stopAutoScroll() {
        if (!isAutoScrolling) {
            return;
        }
        autoScrollTimer.cancel();
        isAutoScrolling = false;
    }
}



