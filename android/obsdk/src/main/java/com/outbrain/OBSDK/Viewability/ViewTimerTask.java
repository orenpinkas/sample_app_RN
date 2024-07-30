package com.outbrain.OBSDK.Viewability;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.TimerTask;

/**
 * Created by odedre on 1/5/16.
 */
class ViewTimerTask extends TimerTask {
    private final WeakReference<View> viewRef;
    private boolean isCancelled;
    private final String viewabilityKey;
    private String requestId; // optional for Viewability per widget compatability
    private long onScreenStartTime = 0;
    private final long VISIBILITY_TIME_THRESHOLD;
    private static final HashMap<String, ViewTimerTask> keysToRunningTimerTasks = new HashMap<>();



    public ViewTimerTask(View view, long threshold) {
        this(view, threshold, null);
    }

    public ViewTimerTask(View view, long threshold, String viewabilityKey) {
        this.viewRef = new WeakReference<>(view);
        this.VISIBILITY_TIME_THRESHOLD = threshold;
        this.viewabilityKey = viewabilityKey;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public void run() {
        View view = viewRef.get();
        if (null == view) {
            cancel();
            return;
        }
        if (isCancelled) return;

//        Log.i("OBSDK", "ViewTimerTask - " + view.hashCode() + " fireVisibilityEventsWhenAppropriate: " + this.key);
        fireVisibilityEventsWhenAppropriate(view);
    }

    private void fireVisibilityEventsWhenAppropriate(View view ) {
        if ( view == null ) return;

        if (is50PercentOfAdIsOnScreen(view)) {
            setOnScreenStartTimeIfNeeded();
            if (hasViewBeenOnScreenForTimeThreshold()) {
                // report to server that view is visible
                SFViewabilityService.getInstance().reportViewabilityForOBViewKey(this.viewabilityKey);
//                Log.i("OBSDK", "ViewTimerTask - " + view.hashCode() + " cancel() - ViewBeenOnScreenForTimeThreshold: " + this.key);
                this.cancel(); // cancel the repeated TimerTask
                ViewTimerTask.removeRunningTaskForKey(this.viewabilityKey);
            }
        } else {
            onScreenStartTime = 0;
        }
    }


    private boolean is50PercentOfAdIsOnScreen(View view) {
        Rect rect = new Rect();
        if (view.isShown() && view.getGlobalVisibleRect(rect)) {
            int visibleArea = rect.width() * rect.height();
            int viewArea = view.getHeight() * view.getWidth();

            return visibleArea * 2 >= viewArea;
        }

        return false;
    }

    private void setOnScreenStartTimeIfNeeded() {
        if (onScreenStartTime == 0) {
            onScreenStartTime = System.currentTimeMillis();
        }
    }

    private boolean hasViewBeenOnScreenForTimeThreshold() {
        return System.currentTimeMillis() - onScreenStartTime >= VISIBILITY_TIME_THRESHOLD;
    }

    @Override
    public boolean cancel() {
        isCancelled = true;
        return super.cancel();
    }

    public static ViewTimerTask getRunningTaskForKey(String key) {
        return keysToRunningTimerTasks.get(key);
    }

    public static void removeRunningTaskForKey(String key) {
        keysToRunningTimerTasks.remove(key);
    }

    public static void addRunningTaskWithKey(ViewTimerTask detectViewabilityTimerTask, String key) {
        ViewTimerTask.keysToRunningTimerTasks.put(key, detectViewabilityTimerTask);
    }

    public static void cancelAllRunningTimerTasks() {
        synchronized(keysToRunningTimerTasks) {
            for (String key : keysToRunningTimerTasks.keySet()) {
                ViewTimerTask task = keysToRunningTimerTasks.get(key);
                if (task != null && !task.isCancelled()) {
                    Log.d("OBSDK", "ViewTimerTask cancelAllRunningTimerTasks - ViewTimerTask - key: " + key);
                    task.cancel();
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public View getView() {
        return viewRef.get();
    }

    @SuppressWarnings("unused")
    public boolean isCancelled() {
        return isCancelled;
    }
}
