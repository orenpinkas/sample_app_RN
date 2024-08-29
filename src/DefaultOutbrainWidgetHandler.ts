import type {OutbrainWidgetHandler} from './types';
import {OBInAppBrowser} from './OBInAppBrowser';

const DefaultHandler: OutbrainWidgetHandler = {
  onHeightChange: _newHeight => {},
  onRecClick: url => {
    OBInAppBrowser.open(url);
  },
  onOrganicClick: url => {
    OBInAppBrowser.open(url);
  },
  onWidgetEvent: (_eventName, _data) => {},
};

export default DefaultHandler;
