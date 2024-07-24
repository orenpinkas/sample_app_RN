package com.sample_app_rn;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.SimpleViewManager;

public class TestNativeViewManager extends SimpleViewManager<TestNativeView> {
    public static final String REACT_CLASS = "TestNativeView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected TestNativeView createViewInstance(ThemedReactContext reactContext) {
        TestNativeView view = new TestNativeView(reactContext);
        return view;
    }

}