package com.outbrain.OBSDK.SmartFeed.Theme;

import android.graphics.Color;

public class SFThemeImpl implements SFTheme {
    private static SFThemeImpl instance;

    private SFTheme currentTheme = new RegularThem();
    private int CURRENT_TYPE = REGULAR;

    private final int VALUE_NOT_SET = -1;
    private int headerColor = VALUE_NOT_SET;
    private int primaryColor = VALUE_NOT_SET;
    private int recTitleTextColor = VALUE_NOT_SET;

    // Theme types
    public static final int REGULAR = 0;
    public static final int DARK = 1;
    public static final int CUSTOM = 2;

    public SFThemeImpl(){}

    public static SFThemeImpl getInstance(){
        if (instance == null){
            instance = new SFThemeImpl();
        }
        return instance;
    }

    public void setThemeMode(int theme) {
        if (theme == DARK) {
            CURRENT_TYPE = DARK;
            this.currentTheme = new DarkTheme();
        } else {
            CURRENT_TYPE = REGULAR;
            this.currentTheme = new RegularThem();
        }
    }

    public void setTheme(SFTheme theme) {
        CURRENT_TYPE = CUSTOM;
        this.currentTheme = theme;
    }

    @Override
    public int primaryColor() {
        if (primaryColor != VALUE_NOT_SET) {
            return primaryColor;
        }
        return currentTheme.primaryColor();
    }

    @Override
    public int sfHeaderColor() {
        if (headerColor != VALUE_NOT_SET) {
            return headerColor;
        }
        return currentTheme.sfHeaderColor();
    }

    @Override
    public int cardShadowColor() {
        return currentTheme.cardShadowColor();
    }

    @Override
    public int recTitleTextColor(boolean isPaid) {
        if (recTitleTextColor != VALUE_NOT_SET) {
            return recTitleTextColor;
        }
        return currentTheme.recTitleTextColor(isPaid);
    }

    @Override
    public int recSourceTextColor() {
        return currentTheme.recSourceTextColor();
    }

    @Override
    public int pageIndicatorSelectedColor() {
        return currentTheme.pageIndicatorSelectedColor();
    }

    @Override
    public int getReadMoreModuleGradientResourceId() {
        return currentTheme.getReadMoreModuleGradientResourceId();
    }

    public boolean isDarkMode() {
        if (this.CURRENT_TYPE == DARK) {
            return true;
        }
        else if (this.recTitleTextColor(true) == Color.WHITE || this.primaryColor() == Color.BLACK) {
            // Custom theme with dark mode color scheme
            return true;
        }
        return false;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setRecTitleTextColor(int recTitleTextColor) {
        this.recTitleTextColor = recTitleTextColor;
    }
}