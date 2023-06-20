/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  NativeModules,
  Platform,
} from 'react-native';

const App = () => {
  const {RNAdiscopeModule} = NativeModules;

  const rvUnitId: string = Platform.OS === 'android' ? 'RV1' : 'RV1';
  const interstitialUnitId: string =
    Platform.OS === 'android' ? 'CHILTEN' : 'INTER_TEST';
  const offerwallUnitId: string =
    Platform.OS === 'android' ? 'CHILTEN_AOS' : 'CHILTEN_IOS';

  const showRV = () => {
    RNAdiscopeModule.showRewardedVideo(rvUnitId);
  };

  const showInterstitial = () => {
    RNAdiscopeModule.showInterstitial(interstitialUnitId);
  };

  const showOfferwall = () => {
    RNAdiscopeModule.showOfferwall(offerwallUnitId);
  };

  const setUserId = () => {
    RNAdiscopeModule.setUserId('AD_TEST_USER');
  };

  useEffect(() => {
    if (Platform.OS === 'ios') {
      RNAdiscopeModule.adInitialize();
    }
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity style={styles.button} onPress={showOfferwall}>
        <Text style={styles.button_name}>show Offerwall</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.button} onPress={showInterstitial}>
        <Text style={styles.button_name}>show Interstitial</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.button} onPress={showRV}>
        <Text style={styles.button_name}>show RV</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[styles.button, {backgroundColor: '#777320'}]}
        onPress={setUserId}>
        <Text style={styles.button_name}>set UserId</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  button: {
    backgroundColor: '#3498db',
    padding: 16,
    margin: 10,
    borderRadius: 8,
  },
  button_name: {
    color: 'white',
    fontSize: 19,
    textAlign: 'center',
  },
});

export default App;
