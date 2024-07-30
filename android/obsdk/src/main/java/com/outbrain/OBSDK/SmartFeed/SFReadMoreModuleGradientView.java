package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


import androidx.annotation.Nullable;

import com.outbrain.OBSDK.R;

public class SFReadMoreModuleGradientView extends View {
    public SFReadMoreModuleGradientView(Context context) {
        this(context, null);
    }

    public SFReadMoreModuleGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setId(R.id.sf_read_more_gradient);
    }
}
