import { Platform, requireNativeComponent, UIManager, findNodeHandle, NativeEventEmitter, NativeModules, ViewStyle, View, NativeMethods } from 'react-native';
import React from 'react';
import {OutbrainWidgetProps, NativeComponentProps, OutbrainWidgetHandler} from './types';
import DefaultHandler from './DefaultOutbrainWidgetHandler';

const moduleName = 'SFWidget';  // This should be the name of the native view manager

const NativeSFWidget = requireNativeComponent<NativeComponentProps>(moduleName);

const createCommandKey = (() => {
  if (Platform.OS === 'android') {
    return UIManager.getViewManagerConfig(moduleName).Commands.create.toString();
  } else {
    return 'create';
  }
})();

export default class OutbrainWidget extends React.Component<OutbrainWidgetProps> {
    private _outbrainWidget: any;
    private eventEmitter: NativeEventEmitter;
    private handler: OutbrainWidgetHandler;
    private nativeCommandKey: string;

    state = {
        height: 0,
    };

    constructor(props: OutbrainWidgetProps) {
        super(props);
        // this.widgetId = props.widgetId;
        console.log(props.handler)
        this.handler = props.handler === undefined ? DefaultHandler : props.handler;
        console.log(this.handler)
        this.nativeCommandKey = this.#getNativeCommandKey();
        this.eventEmitter = new NativeEventEmitter(NativeModules.OBEventModule);
        this.applyEventHandler = this.applyEventHandler.bind(this);
        this.handleHeightChange = this.handleHeightChange.bind(this);
        this.handleRecClick = this.handleRecClick.bind(this);
        this.handleOrganicRecClick = this.handleOrganicRecClick.bind(this);
        this.handleWidgetEvent = this.handleWidgetEvent.bind(this);
        // eventEmitter.addListener(props.widgetId, (event) => {
        //     console.log(`Received event from widget ${props.widgetId}: ${event.name}`);
        //     switch (event.name) {
        //         case 'didChangeHeight':
        //             this.setState({height: event.height});
        //             break;
        //     }
        // });
    }

    componentDidMount() {
        const {widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId} = this.props;
        const viewId = findNodeHandle(this._outbrainWidget);
        UIManager.dispatchViewManagerCommand(
        viewId,
        this.nativeCommandKey,
        [{widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId}]
        );

        this.eventEmitter.addListener("didChangeHeight", this.handleHeightChange);
        this.eventEmitter.addListener("onRecClick", (event) => this.applyEventHandler(event, this.handleRecClick));
        this.eventEmitter.addListener("onOrganicRecClick", this.handleOrganicRecClick);
        this.eventEmitter.addListener("onWidgetEvent", this.handleWidgetEvent);
    }

    componentWillUnmount() {
        this.eventEmitter.removeAllListeners("didChangeHeight");
        this.eventEmitter.removeAllListeners("onRecClick");
        this.eventEmitter.removeAllListeners("onOrganicRecClick");
        this.eventEmitter.removeAllListeners("onWidgetEvent");
    }

        handleHeightChange(event: any) {
        if (event.widgetId === this.props.widgetId) {
            this.setState({ height: event.height });
            this.handler.onHeightChange?.(event.height);
        }
    }

    applyEventHandler(event: any, eventHandler: (event:any) => void) {
        if (event.widgetId === this.props.widgetId) {
            eventHandler(event);
        }
    }

    handleRecClick(event: any) {
        console.log(`${this.props.widgetId}: Received event from widget ${event.widgetId} - onRecClick; url: ${event.url}`);
        this.handler.onRecClick?.(event.url);
    }

    handleOrganicRecClick(event: any) {
        console.log(`Received event from widget ${event.widgetId} - onOrganicRecClick: ${event.url}`);
        this.handler.onOrganicClick?.(event.url);
    }

    handleWidgetEvent(event: any) {
        // console.log(`Received event from widget ${event.widgetId} - ${event.eventName}, ${JSON.stringify(event.additionalData)}`);
        this.handler.onWidgetEvent?.(event.eventName, event.additionalData);
    }

    #getNativeCommandKey () {
        if (Platform.OS === 'android') {
            return UIManager.getViewManagerConfig(moduleName).Commands.create.toString();
        } else {
            return 'create';
        }
    }

    render() {
        return <NativeSFWidget style={{height: this.state.height} as ViewStyle} ref={(ref:any) => (this._outbrainWidget = ref)}/>;
    }
}

