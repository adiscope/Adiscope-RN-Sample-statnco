package com.gameone.bowling710;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.ReactActivity;
import com.nps.adiscope.AdiscopeError;
import com.nps.adiscope.AdiscopeSdk;
import com.nps.adiscope.interstitial.InterstitialAd;
import com.nps.adiscope.interstitial.InterstitialAdListener;
import com.nps.adiscope.listener.AdiscopeInitializeListener;
import com.nps.adiscope.offerwall.OfferwallAd;
import com.nps.adiscope.offerwall.OfferwallAdListener;
import com.nps.adiscope.reward.RewardItem;
import com.nps.adiscope.reward.RewardedVideoAd;
import com.nps.adiscope.reward.RewardedVideoAdListener;

public class MainActivity extends ReactActivity implements RewardedVideoAdListener, InterstitialAdListener, OfferwallAdListener {
  private static final String TAG = MainActivity.class.getName();

  // static 변수 선언
  private static OfferwallAd mOfferwallAd;
  private static RewardedVideoAd mRewardedVideoAd;
  private static InterstitialAd mInterstitialAd;

  @Override
  protected String getMainComponentName() {
    return "RNAdiscope7Ten"; // 매체 react-native 프로젝트 이름으로 변경
  }

  /*
    앱이 처음 실행될 때 광고 초기화 진행
  */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, ">>> call onCreate()");

    AdiscopeSdk.initialize(this, new AdiscopeInitializeListener() {
      @Override
      public void onInitialized(boolean isSuccess) {
        if (isSuccess) {
          // get rewardVideo singleton instance
          Log.d(TAG, ">>> called onInitialized() : isSuccess -> true");
          mRewardedVideoAd = AdiscopeSdk.getRewardedVideoAdInstance(MainActivity.this);
          mRewardedVideoAd.setRewardedVideoAdListener(MainActivity.this);

          mInterstitialAd = AdiscopeSdk.getInterstitialAdInstance(MainActivity.this);
          mInterstitialAd.setInterstitialAdListener(MainActivity.this);

          mOfferwallAd = AdiscopeSdk.getOfferwallAdInstance(MainActivity.this);
          mOfferwallAd.setOfferwallAdListener(MainActivity.this);

          AdiscopeSdk.getOptionSetterInstance(MainActivity.this).setChildYN("NO"); // 가족 정책

          Log.d(TAG, ">>> called onInitialized() : completed");
        } else {
          // Init 실패 에 대한 처리 Code
          Log.d(TAG, ">>> called onInitialized() : isSuccess -> false");
        }
      }
    });
  }


  //  <<<<<<<<<< region implementation RewardedVideoAdListener >>>>>>>>>>

  /*
    - Rewarded Video(RV) 광고를 받아 오면(load) 자동 호출되는 함수
    - 광고가 load 되었기 때문에 바로 재생(show)
  */
  @Override
  public void onRewardedVideoAdLoaded(String unitId) {
    Log.d(TAG, ">>> call onRewardedVideoAdLoaded()");

    if (mRewardedVideoAd.show()) {
      Log.d(TAG, ">>> RewardedVideoAd.show completed");
    }
    else { // show is already in progress
      Log.d(TAG, ">>> this show request is duplicated");
    }
  }

  /*
    - Rewarded Video(RV) 광고를 받아 오지 못했을 때(load fail) 자동 호출되는 함수
    - 유저가 광고 시청을 요청했기 때문에, Toast를 통해 load fail 상황을 안내하는 예제
  */
  @Override
  public void onRewardedVideoAdFailedToLoad(String unitId, AdiscopeError adiscopeError) {
    Log.e(TAG, ">>> call onRewardedVideoAdFailedToLoad() : " + adiscopeError);
    Log.d(TAG, ">>> call onRewardedVideoAdFailedToLoad() unitId : " + unitId);
    Toast.makeText(getApplicationContext(), "광고를 할당 받지 못했습니다.", Toast.LENGTH_LONG).show();
  }

  /*
    - Rewarded Video(RV) 광고 화면이 open 되었을 때 자동 호출되는 함수
  */
  @Override
  public void onRewardedVideoAdOpened(String unitId) {
    Log.d(TAG, "call onRewardedVideoAdOpened()");
  }

  /*
    - Rewarded Video(RV) 광고 시청 후 X 버튼을 클릭했을 때, 즉 유저가 광고 시청 완료했을 때
      Toast 통해 안내하는 예제
  */
  @Override
  public void onRewardedVideoAdClosed(String unitId) {
    Log.d(TAG, ">>> onRewardedVideoAdClosed");
    Toast.makeText(getApplicationContext(), "보상이 지급되었습니다.", Toast.LENGTH_LONG).show();
  }

  /*
    - 광고 네트워크를 통해 Rewarded Video(RV) 광고 시청 완료 메시지를 받을 때 자동 호출되는 함수
  */
  @Override
  public void onRewarded(String unitId, RewardItem rewardItem) {
    Log.d(TAG, ">>> rewardItem.getType : " + rewardItem.getType());
    Log.d(TAG, ">>> rewardItem.getAmount : " + rewardItem.getAmount());
  }

  /*
    - Rewarded Video(RV) 광고를 받아 왔으나(load) 재생하지 못했을 때 자동 호출되는 함수
    - 유저가 광고를 요청했기 때문에 현재의 상태를 유저에게 Toast 안내하는 예제
  */
  @Override
  public void onRewardedVideoAdFailedToShow(String unitId, AdiscopeError adiscopeError) {
    Log.e(TAG, "onRewardedVideoAdFailedToLoad : " + adiscopeError);
    Toast.makeText(getApplicationContext(), "광고를 재생 못했습니다.", Toast.LENGTH_LONG).show();
  }

  //  <<<<<<<<<< endregion >>>>>>>>>>

  //  <<<<<<<<<< region implementation InterstitialAdListener >>>>>>>>>>

  /*
    - Interstitial 광고를 받아 오면(load) 자동 호출되는 함수
    - 광고가 load 되었기 때문에 바로 재생(show)
  */
  @Override
  public void onInterstitialAdLoaded() {
    Log.d(TAG, ">>> call onInterstitialAdLoaded()");

    if (mInterstitialAd.show()) {
      Log.d(TAG, ">>> InterstitialAd.show completed");
    } else { // show is already in progress
      Log.d(TAG, ">>> this show request is duplicated");
    }
  }

  /*
    - Interstitial 광고를 받아 오지 못했을 때(load fail) 자동 호출되는 함수
    - 유저의 요청과 상관없이 광고 요청이 되었기 때문에, load fail 상태를 유저에게 안내하지 않음
      안내하고자 한다면, Toast 등을 통해 안내 가능
  */
  @Override
  public void onInterstitialAdFailedToLoad(AdiscopeError adiscopeError) {
    Log.e(TAG, ">>> call onRewardedVideoAdFailedToLoad() : " + adiscopeError);
  }

  /*
    - Interstitial 광고 화면이 open 되었을 때 자동 호출되는 함수
  */
  @Override
  public void onInterstitialAdOpened(String unitId) {
    Log.d(TAG, ">>> call onInterstitialAdOpened()");
  }

  /*
    - Interstitial 광고 시청 후 X 버튼을 클릭했을 때 자동 호출되는 함수
  */
  @Override
  public void onInterstitialAdClosed(String unitId) {
    Log.d(TAG, ">>> call onInterstitialAdClosed()");
  }

  /*
    - Interstitial 광고를 받아 왔으나(load) 재생하지 못했을 때 자동 호출되는 함수
    - 유저의 의도와 상관없이 진행되기 때문에, Toast 통해 별도 안내하지 않음
  */
  @Override
  public void onInterstitialAdFailedToShow(String unitId, AdiscopeError adiscopeError) {
    Log.e(TAG, "call onInterstitialAdFailedToShow() : " + adiscopeError);
  }

  //  <<<<<<<<<< endregion >>>>>>>>>>

  //  <<<<<<<<<< region implementation OfferwallAdListener >>>>>>>>>>

  /*
    - Offerwall 화면이 오픈되었을 때 자동 호출되는 함수
  */
  @Override
  public void onOfferwallAdOpened(String s) {
    Log.d(TAG, ">>> call onOfferwallAdOpened()");
  }

  /*
    - Offerwall 화면이 오픈되지 않았을 때 자동 호출되는 함수
  */
  @Override
  public void onOfferwallAdFailedToShow(String unitId, AdiscopeError adiscopeError) {
    Log.e(TAG, ">>> call onOfferwallAdFailedToShow() : " + adiscopeError);
  }

  /*
    - Offerwall 화면을 닫았을 때 자동 호출되는 함수
  */
  @Override
  public void onOfferwallAdClosed(String unitId) {
    Log.d(TAG, ">>> call onOfferwallAdClosed()");
  }
}
