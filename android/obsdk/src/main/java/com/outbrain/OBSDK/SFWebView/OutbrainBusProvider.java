package com.outbrain.OBSDK.SFWebView;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class OutbrainBusProvider {

    public static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return BUS;
    }

    private OutbrainBusProvider() {
        // No instances.
    }

    public static class BridgeParamsEvent {
        private String bridgeParams;

        public BridgeParamsEvent(String bridgeParams) {
            this.bridgeParams = bridgeParams;
        }

        public String getBridgeParams() {
            return bridgeParams;
        }
    }

    public static class ViewabilityFiredEvent {
        private String requestId;
        private int position;

        public ViewabilityFiredEvent(String requestId, int position) {
            this.requestId = requestId;
            this.position = position;
        }

        public String getRequestId() {
            return requestId;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class TParamsEvent {
        private String tParam;

        public TParamsEvent(String tParam) {
            this.tParam = tParam;
        }

        public String getTParam() {
            return tParam;
        }
    }

    public static class HeightChangeEvent extends BridgeRecsReceivedEvent {
        private int height;

        public HeightChangeEvent(String URL, String widgetID, int widgetIndex, int height) {
            super(URL, widgetID, widgetIndex);
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class BridgeRecsReceivedEvent {
        private String URL;
        private String widgetID;
        private int widgetIndex;

        public BridgeRecsReceivedEvent(String URL, String widgetID, int widgetIndex) {
            this.URL = URL;
            this.widgetID = widgetID;
            this.widgetIndex = widgetIndex;
        }

        public String getURL() {
            return URL;
        }

        public String getWidgetID() {
            return widgetID;
        }

        public int getWidgetIndex() {
            return widgetIndex;
        }
    }
}
