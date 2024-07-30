package com.outbrain.OBSDK.SmartFeed.Theme;

public interface SFTheme {
    int primaryColor();
    int sfHeaderColor();
    int cardShadowColor();
    int recTitleTextColor(boolean isPaid);
    int recSourceTextColor();
    int pageIndicatorSelectedColor();
    int getReadMoreModuleGradientResourceId();
}
