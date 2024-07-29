import { Platform, requireNativeComponent, UIManager, findNodeHandle, NativeEventEmitter, NativeModules, ViewStyle, View, NativeMethods } from 'react-native';
import React from 'react';
import {OutbrainWidgetProps, NativeComponentProps, OutbrainWidgetHandler} from './types';
import DefaultHandler from './DefaultOutbrainWidgetHandler';

const moduleName = 'SFWidget';  // This should be the name of the native view manager

const NativeSFWidget = requireNativeComponent<NativeComponentProps>(moduleName);

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

        this.handler = props.handler === undefined ? DefaultHandler : props.handler;
        this.nativeCommandKey = this.#getNativeCommandKey();
        this.eventEmitter = new NativeEventEmitter(NativeModules.OBEventModule);

        this.handleHeightChange = this.handleHeightChange.bind(this);
        this.handleRecClick = this.handleRecClick.bind(this);
        this.handleOrganicRecClick = this.handleOrganicRecClick.bind(this);
        this.handleWidgetEvent = this.handleWidgetEvent.bind(this);
    }

    componentDidMount() {
        const {widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId} = this.props;
        const viewId = findNodeHandle(this._outbrainWidget);

        // dispatch init command with widget properties
        UIManager.dispatchViewManagerCommand(
            viewId,
            this.nativeCommandKey,
            [
                {
                    widgetId,
                    widgetIndex,
                    articleUrl,
                    partnerKey,
                    extId,
                    extSecondaryId,
                    pubImpId
                }
            ]
        );

        this.setupEventListeners();
    }

    setupEventListeners() {
        const events = [
            { name: "didChangeHeight", handler: this.handleHeightChange },
            { name: "onRecClick", handler: this.handleRecClick },
            { name: "onOrganicRecClick", handler: this.handleOrganicRecClick },
            { name: "onWidgetEvent", handler: this.handleWidgetEvent }
        ];

        events.forEach(event => {
            this.eventEmitter.addListener(event.name, event.handler);
        });
    }

    removeEventListeners() {
        const eventNames = ["didChangeHeight", "onRecClick", "onOrganicRecClick", "onWidgetEvent"];
        eventNames.forEach(eventName => {
            this.eventEmitter.removeAllListeners(eventName);
        });
    }

    componentWillUnmount() {
        this.removeEventListeners();
    }

    handleHeightChange(event: any) {
        if (event.widgetId !== this.props.widgetId) return;

        this.setState({ height: event.height });
        
        this.handler.onHeightChange?.(event.height);
    }

    handleRecClick(event: any) {
        if (event.widgetId !== this.props.widgetId) return;

        this.handler.onRecClick?.(event.url);
    }

    handleOrganicRecClick(event: any) {
        if (event.widgetId !== this.props.widgetId) return;

        this.handler.onOrganicClick?.(event.url);
    }

    handleWidgetEvent(event: any) {
        if (event.widgetId !== this.props.widgetId) return;

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

