import {Platform} from 'react-native';

export const LINKING_ERROR =
  "The package 'outbrain-react-native' doesn't seem to be linked. Make sure: \n\n" +
  Platform.select({ios: "- You have run 'pod install'\n", default: ''}) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export const componentName = 'SFWidgetNative'; // This should be the name of the native view manager
