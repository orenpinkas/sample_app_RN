import { OutbrainWidgetHandler } from './types';
import OBInAppBrowser from './OBInAppBrowser';

const DefaultHandler: OutbrainWidgetHandler = {
    onHeightChange: (newHeight) => {
      console.log('Default Handler - onHeightChange');
    },
    onRecClick: (url) => {
      OBInAppBrowser.open(url);
    },
    onOrganicClick: (url) => {
      OBInAppBrowser.open(url);
    },
    onWidgetEvent: (eventName, data) => {
      console.log(`Default Handler - onWidgetEvent: ${eventName}`, data);
    }
};

export default DefaultHandler;