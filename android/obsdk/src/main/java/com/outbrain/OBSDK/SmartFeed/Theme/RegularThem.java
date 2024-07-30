package com.outbrain.OBSDK.SmartFeed.Theme;

import android.graphics.Color;

import com.outbrain.OBSDK.R;

class RegularThem implements SFTheme {
    @Override
    public int primaryColor() {
        return Color.WHITE;
    }

    @Override
    public int sfHeaderColor() {
        return Color.parseColor("#565656");
    }

    @Override
    public int cardShadowColor() {
        return Color.BLACK;
    }

    @Override
    public int recTitleTextColor(boolean isPaid) {
        return Color.rgb(16, 16, 16);
    }

    @Override
    public int recSourceTextColor() {
        return Color.parseColor("#707070");
    }

    @Override
    public int pageIndicatorSelectedColor() {
        return Color.parseColor("#9b9b9b");
    }

    @Override
    public int getReadMoreModuleGradientResourceId() {
        return R.drawable.read_more_gradient;
    }
}
