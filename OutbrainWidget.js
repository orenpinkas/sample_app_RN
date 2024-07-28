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
        this.state = {
            height: 200,
        };
        // eventEmitter = new NativeEventEmitter(NativeModules.SFWidgetEventsModule);
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
        const {widgetId, widgetIndex} = this.props;
        const viewId = findNodeHandle(this._outbrainWidget);
        UIManager.dispatchViewManagerCommand(
        viewId,
        createCommandKey,
        [widgetId, widgetIndex]
        );
    }

    render() {
        return <NativeSFWidget style={{height: this.state.height}} ref={ref => (this._outbrainWidget = ref)} />;
    }
}

