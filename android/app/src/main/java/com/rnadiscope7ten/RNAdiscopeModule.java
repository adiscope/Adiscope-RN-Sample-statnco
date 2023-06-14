package com.rnadiscope7ten;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.nps.adiscope.AdiscopeSdk;
import com.nps.adiscope.core.Adiscope;
import com.nps.adiscope.interstitial.InterstitialAd;
import com.nps.adiscope.offerwall.OfferwallAd;
import com.nps.adiscope.reward.RewardedVideoAd;

public class RNAdiscopeModule extends ReactContextBaseJavaModule {
    RNAdiscopeModule(ReactApplicationContext context) {
        super(context);
    }

    /*
      - ReactNative NativeModules에서 불러오는 이름 정의
      import { NativeModules } from react-native
      ...
      const { RNAdiscopeModule } = NativeModules
    */
    @NonNull
    @Override
    public String getName() {
        return "RNAdiscopeModule";
    }

    private static final String TAG = RNAdiscopeModule.class.getName();

    /*
      - Rewarded Video(RV), Interstitial, Offerwal API 요청 전에, Initialized Instance에 userId 적용
      - 앱 실행 후 한번만 적용
    */
    @ReactMethod
    public void setUserId(String userId) {
        Log.d(TAG, ">>> ReactNative call showUserId()");
        AdiscopeSdk.setUserId(userId);
    }

    /*
      - Rewarded Video(RV) 광고 호출 API
    */
    @ReactMethod
    public void showRewardedVideo(String unitId) {
        Log.d(TAG, ">>> ReactNative call showRewardedVideo()");
        RewardedVideoAd rewardedVideoAd = Adiscope.getRewardedVideoAdInstance(getCurrentActivity());

        rewardedVideoAd.load(unitId);
    }

    /*
      - Interstitial 광고 호출 API
      - 유저가 직접 호출하지 않기 때문에, useEffect() 또는 useFocusEffect()에서 호출
    */
    @ReactMethod
    public void showInterstitial(String unitId) {
        Log.d(TAG, ">>> ReactNative call showInterstitial()");
        InterstitialAd interstitialAd = AdiscopeSdk.getInterstitialAdInstance(getCurrentActivity());

        interstitialAd.load(unitId);
    }

    /*
      - Offerwall 화면 오픈 API
    */
    @ReactMethod
    public void showOfferwall(String unitId) {
        Log.d(TAG, ">>> ReactNative call showOfferwall()");
        OfferwallAd offerwallAd = Adiscope.getOfferwallAdInstance(getCurrentActivity());

        if (offerwallAd.show(getCurrentActivity(), unitId, new String[]{})) {
            // Succeed
        } else {
            // show is already in progress
        }
    }
}
