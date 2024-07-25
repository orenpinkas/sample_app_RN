import { requireNativeComponent, UIManager, findNodeHandle } from 'react-native';
import React from 'react';

const NativeOutbrainWidget = requireNativeComponent('OutbrainWidget');

export default class OutbrainWidget extends React.Component {
  componentDidMount() {
    const { widgetId } = this.props;
    const viewId = findNodeHandle(this._outbrainWidget);
    UIManager.dispatchViewManagerCommand(
      viewId,
      UIManager.getViewManagerConfig('OutbrainWidget').Commands.create.toString(),
      [widgetId]
    );
  }

  render() {
    return <NativeOutbrainWidget ref={ref => (this._outbrainWidget = ref)} />;
  }
}