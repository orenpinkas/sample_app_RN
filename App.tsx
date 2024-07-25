import React from 'react';
import { View } from 'react-native';
import TestNativeView from './TestNativeView';
import OutbrainWidget from './OutbrainWidget';
import ArticleScreen from './Article';



const App = () =>
 <ArticleScreen>
    <OutbrainWidget  style={{height: 400, width: 400}} widgetId="MB_2"/>
</ArticleScreen>;


export default App;