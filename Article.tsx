import React from 'react';
import { View, Text, Image, ScrollView, StyleSheet } from 'react-native';

import OutbrainWidget from './OutbrainWidget';
import MapView from './OutbrainWidgetIOS';

const ArticleScreen = () => {
    return (
        <ScrollView style={styles.container} contentContainerStyle={styles.childOfContainer}>
            <Image
                source={{ uri: 'https://img.olympics.com/images/image/private/t_s_pog_staticContent_hero_xl_2x/f_auto/primary/c5r52rbifxn2srhp9no0' }}
                style={styles.image}
            />
            <View style={styles.textContainer}>
                <Text style={styles.header}>Article Header</Text>
                <Text style={styles.subheader}>Article Sub-header</Text>
                <Text style={styles.content}>
                    Paragraph 1: Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                    Nullam eget purus ac magna hendrerit tincidunt. Sed finibus, lorem id
                    iaculis venenatis, mauris est maximus nisi, vel posuere velit nulla at
                    quam.
                </Text>
                <Text style={styles.content}>
                    Paragraph 2: Donec vehicula turpis nec ex venenatis, vel maximus velit
                    consectetur. Curabitur at enim ac risus pharetra gravida. Sed
                    bibendum, odio eget congue sodales, quam augue malesuada lorem, vel
                    faucibus orci ligula eget mi.
                </Text>
                <Text style={styles.content}>
                    Paragraph 3: Donec vehicula turpis nec ex venenatis, vel maximus velit
                    consectetur. Curabitur at enim ac risus pharetra gravida. Sed
                    bibendum, odio eget congue sodales, quam augue malesuada lorem, vel
                    faucibus orci ligula eget mi.
                </Text>
                <MapView style={{ width: 400, height: 300 }} />
                <Text style={styles.content}>
                    Paragraph 3: Donec vehicula turpis nec ex venenatis, vel maximus velit
                    consectetur. Curabitur at enim ac risus pharetra gravida. Sed
                    bibendum, odio eget congue sodales, quam augue malesuada lorem, vel
                    faucibus orci ligula eget mi.
                </Text>
                {/* <OutbrainWidget widgetId="MB_1" widgetIndex={1}/> */}
            </View>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
    },
    childOfContainer: {
        backgroundColor: '#fff',
    },
    image: {
        width: '100%',
        height: 200,
        resizeMode: 'cover',
        marginBottom: 16,
    },
    textContainer: {
        padding: 4,
    },
    header: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 8,
        backgroundColor: '#002bff',
        color: 'white',
        padding: 8,
    },
    subheader: {
        fontSize: 18,
        marginBottom: 16,
        padding: 4,
    },
    content: {
        fontSize: 16,
        lineHeight: 24,
        marginBottom: 16,
        color: 'black',
    },
    outbrainContainer: {
        backgroundColor: '#f0f0f0',
        padding: 16,
        alignItems: 'center',
    },
});

export default ArticleScreen;