package com.outbrain.OBSDK.Viewability;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.Timer;

public class OBFrameLayout extends FrameLayout {
    private final Timer visibleTimer;
    private ViewTimerTask detectViewabilityTimerTask;
    private String key;
    private String reqId;
    private boolean wasDetached;

    public OBFrameLayout(Context context) {
        super(context);
        this.visibleTimer = new Timer();
    }

    public OBFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.visibleTimer = new Timer();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        wasDetached = false;
        // Log.d("OBSDK", "OBFrameLayout " + this.hashCode() + "  - onAttachedToWindow -->" + key);
        if (this.key != null && !SFViewabilityService.getInstance().didAlreadyReportedKey(this.getKey()))
        {
            // Log.d("OBSDK", "OBFrameLayout " + this.hashCode() + "  - onAttachedToWindow --> trackViewability: " + key);
            trackViewability();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Log.d("OBSDK", "OBFrameLayout " + this.hashCode() + "- onDetachedFromWindow: " + key);
        killViewTimerTask();
        wasDetached = true;
    }

    public void trackViewability() {
        if (wasDetached || (detectViewabilityTimerTask != null && !detectViewabilityTimerTask.isCancelled())) {
            return; // if detectViewabilityTimerTask is currently running for this view there is no need to start it again.
        }

        scheduleViewTimerTask();
    }

    private void scheduleViewTimerTask() {
        final long threshold = 1000; // one sec - IAB spc
        ViewTimerTask task = ViewTimerTask.getRunningTaskForKey(key);
        if (task != null && !task.isCancelled()) {
            // Log.d("OBSDK", "OBFrameLayout " + this.hashCode() + " - scheduleViewTimerTask - cancel existing running ViewTimerTask - key: " + this.key);
            task.cancel();
        }

        detectViewabilityTimerTask = new ViewTimerTask(this, threshold, key);
        detectViewabilityTimerTask.setRequestId(this.reqId);
        ViewTimerTask.addRunningTaskWithKey(detectViewabilityTimerTask, key);
        visibleTimer.schedule(detectViewabilityTimerTask, 0, 200); // period - This is the time in milliseconds between successive task executions.
    }

    private void killViewTimerTask() {
        if (detectViewabilityTimerTask != null && visibleTimer != null) {
            detectViewabilityTimerTask.cancel();
        }
        if (key != null) {
            ViewTimerTask.removeRunningTaskForKey(key);
        }
    }

    // Getters and Setters
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

}
