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
        eventEmitter = new NativeEventEmitter(NativeModules.OBEventModule);
        // eventEmitter.addListener(props.widgetId, (event) => {
        //     console.log(`Received event from widget ${props.widgetId}: ${event.name}`);
        //     switch (event.name) {
        //         case 'didChangeHeight':
        //             this.setState({height: event.height});
        //             break;
        //     }
        // });
        eventEmitter.addListener("didChangeHeight", (event) => {
            console.log(`Received event from widget ${event.widgetId}. event.widgetId === props.widgetId: ${event.widgetId === props.widgetId}. event.height: ${event.height} type of event height: ${typeof event.height}`);
            if (event.widgetId === props.widgetId) {
                this.setState({height: event.height});
            }
        });
    }

    componentDidMount() {
        const {widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId} = this.props;
        const viewId = findNodeHandle(this._outbrainWidget);
        UIManager.dispatchViewManagerCommand(
        viewId,
        createCommandKey,
        [{widgetId, widgetIndex, articleUrl, partnerKey, extId, extSecondaryId, pubImpId}]
        );
    }

    render() {
        return <NativeSFWidget style={{height: this.state.height}} ref={ref => (this._outbrainWidget = ref)} />;
    }
}

