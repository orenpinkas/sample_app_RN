import {Linking} from 'react-native';
import {InAppBrowser} from 'react-native-inappbrowser-reborn';

export const OBInAppBrowser = Object.freeze({
  open: async function (url: string) {
    try {
      if (InAppBrowser?.isAvailable && (await InAppBrowser.isAvailable())) {
        await InAppBrowser.open(url, {
          // iOS Properties
          readerMode: false,
          animated: true,
          modalPresentationStyle: 'fullScreen',
          modalTransitionStyle: 'coverVertical',
          modalEnabled: true,
          enableBarCollapsing: false,
          // Android Properties
          showTitle: true,
          enableUrlBarHiding: true,
          enableDefaultShare: true,
          forceCloseOnRedirection: false,
          // Specify full animation resource identifier(package:anim/name)
          // or only resource name(in case of animation bundled with app).
          animations: {
            startEnter: 'slide_in_right',
            startExit: 'slide_out_left',
            endEnter: 'slide_in_left',
            endExit: 'slide_out_right',
          },
          headers: {
            'my-custom-header': 'my custom header value',
          },
        });
      } else {
        await Linking.openURL(url);
      }
    } catch (error) {
      console.log('Error opening URL:', error);
      // Fallback to Linking.openURL if InAppBrowser fails
      await Linking.openURL(url);
    }
  },
});
