import React from 'react';
import { View } from 'react-native';
import TestNativeView from './TestNativeView';
import OutbrainWidget from './OutbrainWidget';
import ArticleScreen from './Article';
import OutbrainWidgetIOS from './OutbrainWidgetIOS';
import {requireNativeComponent} from 'react-native';

// const SFWidget = requireNativeComponent('RNTMap');


const App = () => 
<ArticleScreen>
   <OutbrainWidgetIOS />;
  </ArticleScreen>

export default App;

//     <OutbrainWidget widgetId="MB_2" widgetIndex={0} />
