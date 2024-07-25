package com.sample_app_rn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.Map;

public class SFWebViewWidgetManager extends ViewGroupManager<SFWebViewWidgetWrapper> {
    public static final String REACT_CLASS = "OutbrainWidget";
    public static final int COMMAND_CREATE = 1;

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
    public void receiveCommand(@NonNull SFWebViewWidgetWrapper root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        int commandIdInt = Integer.parseInt(commandId);

        if (commandIdInt == COMMAND_CREATE) {
            assert args != null;
            String widgetId = args.getString(0);
            int widgetIndex = args.getInt(1);
            root.initialize(widgetId, widgetIndex);
        }
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("create", COMMAND_CREATE);
    }
}