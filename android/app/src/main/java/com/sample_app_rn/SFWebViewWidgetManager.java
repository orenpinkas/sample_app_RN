package com.sample_app_rn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.Map;

public class SFWebViewWidgetManager extends ViewGroupManager<SFWebViewWidgetWrapper> {
    public static final String REACT_CLASS = "SFWidget";
    public static final String COMMAND_CREATE_NAME = "create";
    public static final int COMMAND_CREATE_ID = 1;


    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected SFWebViewWidgetWrapper createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        return new SFWebViewWidgetWrapper(themedReactContext);
    }


    @Override
    public void receiveCommand(@NonNull SFWebViewWidgetWrapper view, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(view, commandId, args);
        int commandIdInt = Integer.parseInt(commandId);

        // 'create' is called by React Native after the native ViewGroup SFWebViewWidgetWrapper is mounted onto the screen (in 'componentDidMount')
        // 'create' instantiates the SFWebViewWidget subview with the widget parameters passed from RN
        if (commandIdInt == COMMAND_CREATE_ID) {
            assert args != null;
            ReadableMap arguments = args.getMap(0);
            view.initialize(arguments);
        }
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(COMMAND_CREATE_NAME, COMMAND_CREATE_ID);
    }
}