import React from 'react';
import { View } from 'react-native';
import TestNativeView from './TestNativeView';
import OutbrainWidget from './OutbrainWidget';
import ArticleScreen from './Article';



const App = () =>
 <ArticleScreen>
    <OutbrainWidget  style={{height: 800, width: 400}}/>
</ArticleScreen>;


export default App;