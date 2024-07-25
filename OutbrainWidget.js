import { requireNativeComponent, UIManager, findNodeHandle } from 'react-native';
import React from 'react';

const NativeOutbrainWidget = requireNativeComponent('OutbrainWidget');

export default class OutbrainWidget extends React.Component {
  componentDidMount() {
    const { widgetId, widgetIndex } = this.props;
    const viewId = findNodeHandle(this._outbrainWidget);
    UIManager.dispatchViewManagerCommand(
      viewId,
      UIManager.getViewManagerConfig('OutbrainWidget').Commands.create.toString(),
      [widgetId, widgetIndex]
    );
  }

  render() {
    return <NativeOutbrainWidget style={{height: 800}} ref={ref => (this._outbrainWidget = ref)} />;
  }
}

