import React from 'react';
import { View } from 'react-native';
import TestNativeView from './TestNativeView';
import OutbrainWidget from './OutbrainWidget';
import ArticleScreen from './Article';



const App = () =>
 <ArticleScreen>
    <OutbrainWidget widgetId="MB_2" widgetIndex={0} />
</ArticleScreen>;


export default App;