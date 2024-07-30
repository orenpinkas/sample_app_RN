import { OutbrainWidgetHandler } from './types';
import OBInAppBrowser from './OBInAppBrowser';

const DefaultHandler: OutbrainWidgetHandler = {
    onHeightChange: (newHeight) => {},
    onRecClick: (url) => {
      OBInAppBrowser.open(url);
    },
    onOrganicClick: (url) => {
      OBInAppBrowser.open(url);
    },
    onWidgetEvent: (eventName, data) => {}
};

export default DefaultHandler;