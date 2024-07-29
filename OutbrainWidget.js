import { Platform, requireNativeComponent, UIManager, findNodeHandle, NativeEventEmitter, NativeModules } from 'react-native';
import React from 'react';

const moduleName = 'SFWidget';  // This should be the name of the native view manager
const NativeSFWidget = requireNativeComponent(moduleName);

const createCommandKey = (() => {
  if (Platform.OS === 'android') {
    return UIManager.getViewManagerConfig(moduleName).Commands.create.toString();
  } else {
    return 'create';
  }
})();


export default class OutbrainWidget extends React.Component {

    constructor(props) {
        super(props);
        this.widgetId = props.widgetId;
        this.state = {
            height: 0,
        };
        this.eventEmitter = new NativeEventEmitter(NativeModules.OBEventModule);
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
        createCommandKey,
        [{widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId}]
        );

        this.eventEmitter.addListener("didChangeHeight", this.handleHeightChange);
        this.eventEmitter.addListener("onRecClick", this.handleRecClick);
        this.eventEmitter.addListener("onOrganicRecClick", this.handleOrganicRecClick);
        this.eventEmitter.addListener("onWidgetEvent", this.handleWidgetEvent);
    }

    componentWillUnmount() {
        this.eventEmitter.removeListener("didChangeHeight", this.handleHeightChange);
        this.eventEmitter.removeListener("onRecClick", this.handleRecClick);
        this.eventEmitter.removeListener("onOrganicRecClick", this.handleOrganicRecClick);
        this.eventEmitter.removeListener("onWidgetEvent", this.handleWidgetEvent);
    }

    handleHeightChange(event) {
        if (event.widgetId === this.props.widgetId) {
            this.setState({ height: event.height });
        }
    }

    handleRecClick(event) {
        console.log(`Received event from widget ${event.widgetId} - onRecClick`);
    }

    handleOrganicRecClick(event) {
        console.log(`Received event from widget ${event.widgetId} - onOrganicRecClick`);
    }

    handleWidgetEvent(event) {
        console.log(`Received event from widget ${event.widgetId} - ${event.eventName}, ${JSON.stringify(event.additionalData)}`);
    }

    render() {
        return <NativeSFWidget style={{height: this.state.height}} ref={ref => (this._outbrainWidget = ref)} />;
    }
}

