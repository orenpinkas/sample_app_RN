package com.outbrain.OBSDK.VideoUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.outbrain.OBSDK.SmartFeed.OBSmartFeed;

import org.greenrobot.eventbus.EventBus;

public class OBVideoFrameLayout extends FrameLayout {


    public OBVideoFrameLayout(@NonNull Context context) {
        super(context);
    }

    public OBVideoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().post(new OBSmartFeed.OnWebViewDetachedFromWindowEvent());
    }
}
