import { requireNativeComponent, UIManager, findNodeHandle, NativeEventEmitter, NativeModules } from 'react-native';
import React from 'react';

const NativeOutbrainWidget = requireNativeComponent('OutbrainWidget');

export default class OutbrainWidget extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            height: 200,
        };
        eventEmitter = new NativeEventEmitter(NativeModules.SFWidgetEventsModule);
        eventEmitter.addListener(props.widgetId, (event) => {
            console.log(`Received event from widget ${props.widgetId}: ${event.name}`);
            switch (event.name) {
                case 'didChangeHeight':
                    this.setState({height: event.height});
                    break;
            }
        });
    }

    componentDidMount() {
        const {widgetId, widgetIndex} = this.props;
        const viewId = findNodeHandle(this._outbrainWidget);
        UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.getViewManagerConfig('OutbrainWidget').Commands.create.toString(),
        [widgetId, widgetIndex]
        );
    }

    render() {
        return <NativeOutbrainWidget style={{height: this.state.height}} ref={ref => (this._outbrainWidget = ref)} />;
    }
}

