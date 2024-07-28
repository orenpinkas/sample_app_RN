import React from 'react';
import {requireNativeComponent} from 'react-native';

const SFWidget = requireNativeComponent('SFWidget');

export default class OutbrainWidgetIOS extends React.Component {

    render() {
        return <SFWidget widgetIdTest="MB_1" style={{height: 400, width: 400}} ref={ref => (this._sfWidget = ref)} />;
    }
}