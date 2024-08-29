import {
  Platform,
  requireNativeComponent,
  UIManager,
  findNodeHandle,
  NativeEventEmitter,
  NativeModules,
  type ViewStyle,
} from 'react-native';
import React from 'react';
import type {
  OutbrainWidgetProps,
  NativeComponentProps,
  OutbrainWidgetHandler,
  OutbrainWidgetDefaultProps,
} from './types';
import DefaultHandler from './DefaultOutbrainWidgetHandler';
import {packageVersion} from './version';
import {componentName, LINKING_ERROR} from './constants';

const viewManagerConfig = UIManager.getViewManagerConfig(componentName);
if (viewManagerConfig == null) {
  throw new Error(LINKING_ERROR);
}

const NativeSFWidget =
  requireNativeComponent<NativeComponentProps>(componentName);

class OutbrainWidgetCls extends React.Component<OutbrainWidgetProps> {
  static defaultProps = {
    darkMode: false,
  } as OutbrainWidgetDefaultProps;

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
    this.nativeCommandKey = this.getNativeCommandKey();
    this.eventEmitter = new NativeEventEmitter(NativeModules.OBEventModule);

    this.handleHeightChange = this.handleHeightChange.bind(this);
    this.handleRecClick = this.handleRecClick.bind(this);
    this.handleOrganicRecClick = this.handleOrganicRecClick.bind(this);
    this.handleWidgetEvent = this.handleWidgetEvent.bind(this);
  }

  componentDidMount() {
    const {
      widgetId,
      widgetIndex,
      articleUrl,
      partnerKey,
      extId,
      extSecondaryId,
      pubImpId,
      darkMode,
    } = this.props;
    const viewId = findNodeHandle(this._outbrainWidget);

    // dispatch 'create' command to instantiate the native SFWidget with the widget properties
    // this extra step in necessary because the widget properties cannot be passed at the time of the
    // native view registration to the Regsitry of React View Manager (in native side)
    UIManager.dispatchViewManagerCommand(viewId, this.nativeCommandKey, [
      {
        widgetId,
        widgetIndex,
        articleUrl,
        partnerKey,
        extId,
        extSecondaryId,
        pubImpId,
        darkMode,
        packageVersion,
      },
    ]);

    this.setupEventListeners();
  }

  setupEventListeners() {
    const events = [
      {name: 'didChangeHeight', handler: this.handleHeightChange},
      {name: 'onRecClick', handler: this.handleRecClick},
      {name: 'onOrganicRecClick', handler: this.handleOrganicRecClick},
      {name: 'onWidgetEvent', handler: this.handleWidgetEvent},
    ];

    events.forEach(event => {
      this.eventEmitter.addListener(event.name, event.handler);
    });
  }

  removeEventListeners() {
    const eventNames = [
      'didChangeHeight',
      'onRecClick',
      'onOrganicRecClick',
      'onWidgetEvent',
    ];
    eventNames.forEach(eventName => {
      this.eventEmitter.removeAllListeners(eventName);
    });
  }

  componentWillUnmount() {
    this.removeEventListeners();
  }

  handleHeightChange(event: any) {
    if (event.widgetId !== this.props.widgetId) {
      return;
    }

    if (event.height > this.state.height) {
      this.setState({height: event.height});
    }

    console.log(`${this.props.widgetId}; onHeightChange: ${event.height}`);
    this.handler.onHeightChange?.(event.height);
  }

  handleRecClick(event: any) {
    if (event.widgetId !== this.props.widgetId) {
      return;
    }

    console.log(`${this.props.widgetId}; onRecClick`);
    this.handler.onRecClick?.(event.url);
  }

  handleOrganicRecClick(event: any) {
    if (event.widgetId !== this.props.widgetId) {
      return;
    }

    console.log(`${this.props.widgetId}; onOrganicRecClick`);
    this.handler.onOrganicClick?.(event.url);
  }

  handleWidgetEvent(event: any) {
    if (event.widgetId !== this.props.widgetId) {
      return;
    }

    console.log(`${this.props.widgetId}; onWidgetEvent: ${event.eventName}`);
    this.handler.onWidgetEvent?.(event.eventName, event.additionalData);
  }

  getNativeCommandKey() {
    if (Platform.OS === 'android' && viewManagerConfig?.Commands?.create) {
      return viewManagerConfig.Commands.create.toString();
    } else {
      return 'create';
    }
  }

  render() {
    return (
      <NativeSFWidget
        style={{height: this.state.height} as ViewStyle}
        ref={(ref: any) => (this._outbrainWidget = ref)}
      />
    );
  }
}

// This is a workaround to be able to export the class-based component
export function OutbrainWidget(props: OutbrainWidgetProps) {
  return <OutbrainWidgetCls {...props} />;
}
