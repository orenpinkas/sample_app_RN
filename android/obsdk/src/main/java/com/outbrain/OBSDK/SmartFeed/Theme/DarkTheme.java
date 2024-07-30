package com.outbrain.OBSDK.SmartFeed.Theme;

import android.graphics.Color;

import com.outbrain.OBSDK.R;

class DarkTheme implements SFTheme {
    @Override
    public int primaryColor() {
        return Color.BLACK;
    }

    @Override
    public int sfHeaderColor() {
        return Color.WHITE;
    }

    @Override
    public int cardShadowColor() {
        return Color.WHITE;
    }

    @Override
    public int recTitleTextColor(boolean isPaid) {
        return Color.WHITE;
    }

    @Override
    public int recSourceTextColor() {
        return Color.parseColor("#BFffffff"); // with 75% opacity
    }

    @Override
    public int pageIndicatorSelectedColor() {
        return Color.WHITE;
    }

    @Override
    public int getReadMoreModuleGradientResourceId() {
        return R.drawable.read_more_gradient_dark;
    }
}
