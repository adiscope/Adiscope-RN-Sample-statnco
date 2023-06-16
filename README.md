# RN Adiscope 매체 공유 가이드

> Adiscope SDK AOS v3.0.0
>
> Adiscope SDK iOS v2.1.8

> TNK가 제공하는 정보
>
> > media id, media secret key, sub domain, unit id (rv, interstitial, offerwal), admob app id

---

## 테스트 환경

`npx react-native init MyApp --template react-native-template-typescript@6.5.13`

- react-native : 0.63.4
- typescript : ^3.8.3
- node : 16.17.0

`AOS`

- buildToolsVersion = "29.0.2"
- minSdkVersion = 21
- compileSdkVersion = 29
- targetSdkVersion = 29
- gradle : 3.6.4

---

## [AOS]

- **android > build.gradle**

  ```bash
  buildscript {
      ext {
          buildToolsVersion = "29.0.2"
          minSdkVersion = 21 // 16 → 21 수정 (multidex 적용)
          compileSdkVersion = 29
          targetSdkVersion = 29
      }
      repositories {
          google()
          jcenter()
      }
      dependencies {
          classpath('com.android.tools.build:gradle:3.6.4') // gradle upgrade
      }
  }

  allprojects {
      repositories {
  		  ...
          maven { url 'https://repository.adiscope.com/repository/adiscope/' } // 추가
  		  ...
      }
  }
  ```

- **android > app > build.gradle**

  ```tsx
  // 추가

  android {
      ...
      defaultConfig {

          // 애디스콥 측에 media_id 와 media_secret, sub_domain 문의!

          manifestPlaceholders = [
                  adiscope_media_id: "AOS media id, TNK로부터 전달 받은 값",
                  adiscope_media_secret: "TNK로부터 전달 받은 값",
                  adiscope_sub_domain: "TNK로부터 전달 받은 값"
          ] // 추가
      }
      ...
  }

  dependencies {
  		...
  //		implementation "com.facebook.react:react-native:+"
  	implementation "com.facebook.react:react-native:0.63.4!!" // react-native 버전 적용

      // aos adiscope 3.0.0 libraries
      implementation 'com.nps.adiscope:adiscopeCore:3.0.0.0'
      implementation 'com.nps.adiscope:adiscopeAndroid:1.1.8'
      implementation 'com.nps.adiscope:adapter.admob:20.6.0.4' // admob (use play-services-ads:20.6.0 dependency)
      implementation 'com.nps.adiscope:adapter.chartboost:8.4.2.2'
      implementation 'com.nps.adiscope:adapter.ironsource:7.2.1.3'
      implementation 'com.nps.adiscope:adapter.unityads:4.2.1.1'
      implementation 'com.nps.adiscope:adapter.max:11.9.0.0'
      implementation 'com.applovin:applovin-sdk:11.9.0' // applovin 앱러빈은 직접 참조 해야함
      implementation 'com.nps.adiscope:adapter.applovin:11.9.0.0'
      implementation 'com.nps.adiscope:adapter.fan:6.13.7.0'
      implementation 'com.nps.adiscope:adapter.inmobi:10.1.3.4.0'
      implementation 'com.nps.adiscope:adapter.mobvista:16.4.31.0' // mobvista (use androidx)
      implementation 'com.nps.adiscope:adapter.pangle:5.1.0.8.0'
      implementation 'com.nps.adiscope:adapter.smaato:22.1.0.0'
      implementation 'com.nps.adiscope:adapter.tapjoy:12.11.1.2'
      implementation 'com.nps.adiscope:adapter.vungle:6.12.1.1' // vungle (use androidx)
  }
  ```

- **android > app > src > main > AndroidManifest.xml**

  ```xml
  <application ...>
  		<activity
        android:name=".MainActivity"
        android:exported="true" <!-- 추가 -->
        android:label="@string/app_name"
        android:configChanges="keyboard|keyboardHidden|orientation|screenSize|uiMode"
        android:launchMode="singleTask"
        android:windowSoftInputMode="adjustResize">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
      </activity>

      <!-- admob app id for adiscope Admob rewarded video networks -->
      <meta-data
          android:name="com.google.android.gms.ads.APPLICATION_ID"
          android:value="ca-app-pub-xxxxx"/> <!-- 추가, tnk로부터 전달 받은 값 적용 -->
      ...
      <meta-data android:name="adiscope_media_id" android:value="${adiscope_media_id}"/> <!-- 추가 -->
      <meta-data android:name="adiscope_media_secret" android:value="${adiscope_media_secret}"/> <!-- 추가 -->
      <meta-data android:name="adiscope_sub_domain" android:value="${adiscope_sub_domain}"/> <!-- 추가 -->
  </application>
  ```

- **Sync Project With Gradle Files** (adiscope 모듈 받아 오기)

  ![androidstudio_menu_bar](https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/69e22df4-fa8a-4408-bca9-b07f5e57b8af)

  [우측 6번째 아이콘 (Sync Project With Gradle Files)]

- **android > app > src > main > java > com > _project_name_ > MainActivity.java**

  ```java
  // implement interface

  public class MainActivity extends ReactActivity implements RewardedVideoAdListener, InterstitialAdListener, OfferwallAdListener {
    private static final String TAG = MainActivity.class.getName();

    // static 변수 선언
    private static OfferwallAd mOfferwallAd;
    private static RewardedVideoAd mRewardedVideoAd;
    private static InterstitialAd mInterstitialAd;

    @Override
    protected String getMainComponentName() {
      return "rntest1"; // 매체 react-native 프로젝트 이름으로 변경**
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
  ```

- `Native Module 생성` : **android > app > src > main > java > com > _project_name_ > RNAdiscopeModule.java**

  ```java
  // 파일 생성

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
  ```

- `Native Module 패키징` : **android > app > src > main > java > com > _project_name_ > RNAdiscopePackage.java**

  ```java
  // 파일 생성

  public class RNAdiscopePackage implements ReactPackage {
      @Override
      public List<NativeModule> createNativeModules(
              ReactApplicationContext reactContext) {
          List<NativeModule> modules = new ArrayList<>();

          // Custom Module 추가
          modules.add(new RNAdiscopeModule(reactContext));

          return modules;
      }

      @Override
      public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
          return Collections.emptyList();
      }
  }
  ```

- `Native Module Package 등록` : **android > app > src > main > java > com > _project_name_ > MainApplication.java**
  ```java
  @Override
  protected List<ReactPackage> getPackages() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    List<ReactPackage> packages = new PackageList(this).getPackages();
    // Packages that cannot be autolinked yet can be added manually here, for example:
     packages.add(new RNAdiscopePackage()); // NativeModule Package 추가
    return packages;
  }
  ```

---

## [iOS]

- **adiscope SDK 설치**

  ```objectivec
  // project_name > ios > Podfiile (bold 문자열 추가)

  target 'RNAdiscopeSample' do
    config = use_native_modules!

    # Flags change depending on the env values.
    flags = get_default_flags()

    pod 'Adiscope', '2.1.8.0'
    pod 'AdiscopeMediaAppLovin', '2.1.2.0'
    pod 'AdiscopeMediaAdMob', '2.0.6.0'
    pod 'AdiscopeMediaAdManager', '2.1.8.0'
    pod 'AdiscopeMediaFAN', '2.1.2.0'
    pod 'AdiscopeMediaMobVista', '2.1.1.0'
    pod 'AdiscopeMediaUnityAds', '2.1.4.0'
    pod 'AdiscopeMediaTapjoy', '2.1.4.0'
    pod 'AdiscopeMediaIronsource', '2.1.0.0'
    pod 'AdiscopeMediaVungle', '2.1.8.0'
    pod 'AdiscopeMediaChartBoost', '2.1.2.0'

    use_react_native!(
      :path => config[:reactNativePath],
      # Hermes is now enabled by default. Disable by setting this flag to false.
      # Upcoming versions of React Native may rely on get_default_flags(), but
      # we make it explicit here to aid in the React Native upgrade process.
      :hermes_enabled => flags[:hermes_enabled],
      :fabric_enabled => flags[:fabric_enabled],
      # Enables Flipper.
      #
      # Note that if you have use_frameworks! enabled, Flipper will not work and
      # you should disable the next line.
      :flipper_configuration => flipper_config,
      # An absolute path to your application root.
      :app_path => "#{Pod::Config.instance.installation_root}/.."
    )

    target 'RNAdiscopeSampleTests' do
      inherit! :complete
      # Pods for testing
    end

    post_install do |installer|
      react_native_post_install(
        installer,
        # Set `mac_catalyst_enabled` to `true` in order to apply patches
        # necessary for Mac Catalyst builds
        :mac_catalyst_enabled => false
      )
      __apply_Xcode_12_5_M1_post_install_workaround(installer)

        # pod install 할 때, arm64 simulator 자동 제외
  		installer.pods_project.targets.each do |target|
          target.build_configurations.each do |config|
              config.build_settings["ONLY_ACTIVE_ARCH"] = "YES"
              config.build_settings["EXCLUDED_ARCHS[sdk=iphonesimulator*]"] = "arm64"
          end
      end

    end
  end
  ```

  ```objectivec
  // info.plist (key-value 추가)

  <dict>
  	...
  	<key>NSUserTrackingUsageDescription</key>
  	<string></string>
  	<key>GADIsAdManagerApp</key>
  	<true/>
  	<string>URhay2tKwCEd5D15ONJFui9Z7RfNKG0piiOemF-fSDHXRJdqhx3ZeD8mfo8-39omtAPcPOHscZO2t0sTyw7a8G</string>
  	<key>SKAdNetworkItems</key>
  	<array>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>9T245VHMPL.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>N6FK4NFNA4.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>WZMMZ9FP6W.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>AV6W8KGT66.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>F7S53Z58QE.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>3RD42EKR43.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>P78AXXW29G.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>2FNUA5TDW4.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>V72QYCH5UU.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>44JX6755AQ.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>4FZDC2EVR5.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>YDX93A7ASS.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>F73KDQ92P3.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>ZQ492L623R.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>24T9A8VW3C.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>8S468MFL3Y.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>4468KM3ULZ.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>PPXM28T8AP.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>578PRTVX9J.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>HS6BDUKANM.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>488R3Q3DTQ.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>2U9PT9HC89.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>424M5254LK.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>KBD757YWX3.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>9B89H5Y424.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>7RZ58N8NTL.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>G28C52EEHV.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>WG4VFF78ZM.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>FEYAARZU9V.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>XY9T38CT57.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>PWA73G5RT2.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>MTKV5XTK9E.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>ZMVFPC5AQ8.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>YCLNXRL5PM.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>5A6FLPKH64.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>32Z4FX6L9H.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>9YG77X724H.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>MLMMFZH3R3.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>GTA9LK7P23.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>T38B2KH725.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>F38H382JLK.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>PRCB7NJMU6.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>BVPN9UFA9B.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>W9Q455WK68.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>GGVN48R87G.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>4DZT52R2T5.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>MP6XLYR22A.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>S39G8K73MM.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>44N7HLLDY6.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>M8DBW4SV7C.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>54NZKQM89Y.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>V79KVWWJ4G.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>TL55SBB4FM.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>4PFYVQ9L8R.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>7UG5ZH24HU.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>22MMUN2RN5.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>3SH42Y64Q3.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>5L3TPT7T6E.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>3QCR597P9D.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>238DA6JT44.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>LR83YXWKA7.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>6XZPU9S2P8.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>N38LU8286Q.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>5LM9LJ6JB7.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>CG4YQ2SRNC.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>GLQZH8VGBY.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>7953JERFZD.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>KLF5C3L5U5.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>C6K4G5QG8M.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>3QY4746246.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>CSTR6SUWN9.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>UW77J35X4D.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>N66CZ3Y3BX.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>8C4E2GHE7U.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>X44K69NGH6.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>MLS7YZ5DVL.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>5TJDWBRQ8W.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>523JB4FST2.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>E5FVKXWRPN.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>EJVT5QM6AK.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>9RD848Q2BZ.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>CJ5566H2GA.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>N9X2A789QT.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>9NLQEAG3GK.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>V9WTTPBFK9.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>V4NXQHLYQP.skadnetwork</string>
  		</dict>
  		<dict>
  			<key>SKAdNetworkIdentifier</key>
  			<string>737Z793B9F.skadnetwork</string>
  		</dict>
  	</array>
  	...
  </dict>
  ```

  ```bash
  $ npx pod-install ios
  ```

  <img width="1180" alt="xcode_arm64" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/ec0fc81d-a25a-4792-a22a-11996133abba">

  [Excluded Architectures > Simulators > arm64 적용]

- **module header 파일 생성** (파일명 : RCTRNAdiscopeModule.h)

  <img width="1082" alt="module_header_1" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/0d22a999-4fd7-4f7b-8fe8-693a1b20d328">

  <img width="734" alt="module_header_2" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/84f981ba-41d8-437c-b6c9-78f55b61263e">

  ```objectivec
  // RCTRNAdiscopeModule.h

  #ifndef RCTRNAdiscopeModule_h
  #define RCTRNAdiscopeModule_h

  #import <React/RCTBridgeModule.h>
  #import <Adiscope/Adiscope.h>

  @interface RCTRNAdiscopeModule : NSObject <RCTBridgeModule, AdiscopeDelegate>

  @end

  #endif /* RCTRNAdiscopeModule_h */

  ```

- **module 파일 생성** (파일명 : RCTRNAdiscopeModule.m)

  <img width="1075" alt="module_1" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/19960fec-2d0c-4b01-a389-a9ea08025a1d">
  <img width="730" alt="module_2" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/8f12e70e-6213-4a5d-a6fa-afc280b73dd5">
  <img width="801" alt="module_3" src="https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/99b95a0b-1e52-49a6-9a47-0a9219ac66d5">

  ```objectivec
  // RCTRNAdiscopeModule.m

  #import <Foundation/Foundation.h>
  #import <React/RCTLog.h>

  #import "RCTRNAdiscopeModule.h"

  @implementation RCTRNAdiscopeModule

  // To export a module named RNAdiscopeModule
  RCT_EXPORT_MODULE(RNAdiscopeModule)

  // << Adiscope SDK Initialize >>
  RCT_EXPORT_METHOD(adInitialize)
  {
    [[AdiscopeInterface sharedInstance] setMainDelegate:self];
    [[AdiscopeInterface sharedInstance] setUserId:@"MyUserID"];
    [[AdiscopeInterface sharedInstance] initialize:@"86" mediaSecret:@"3f5ae8e75c2d481d9d0f5ea030e544e9" callBackTag:nil];
  }

  // initialize callback
  - (void)onInitialized:(BOOL)isSuccess {
    RCTLogInfo(@">>> %d", isSuccess);
  }

  // << setUserId >>

  RCT_EXPORT_METHOD(setUserId: (NSString *)userId) {
    [[AdiscopeInterface sharedInstance] setUserId:userId];
  }

  // callback example
  RCT_EXPORT_METHOD(isRVLoaded:(NSString *)unitId Callback:(RCTResponseSenderBlock)callback)
  {
    callback(@[@([[AdiscopeInterface sharedInstance] isLoaded:unitId])]);
  }
  //

  // << Rewarded Video >>
  RCT_EXPORT_METHOD(showRewardedVideo: (NSString *)rewardedVideoUnitID)
  {
    RCTLogInfo(@">>> rewarded video unit id %@", rewardedVideoUnitID);
    [[AdiscopeInterface sharedInstance] load:rewardedVideoUnitID];
  }

  // rv callbacks
  - (void)onRewardedVideoAdLoaded:(NSString *)unitID {
    RCTLogInfo(@">>> onRewardedVideoAdLoaded");
    [[AdiscopeInterface sharedInstance] show];
  }

  - (void)onRewardedVideoAdFailedToLoad:(NSString *)unitID Error:(AdiscopeError *)error {
    RCTLogInfo(@">>> onRewardedVideoAdFailedToLoad");
  }

  - (void)onRewardedVideoAdOpened:(NSString *)unitID {
    RCTLogInfo(@">>> onRewardedVideoAdOpened");
  }

  - (void)onRewardedVideoAdClosed:(NSString *)unitID {
    RCTLogInfo(@">>> onRewardedVideoAdClosed");
  }

  - (void)onRewardedVideoAdFailedToShow:(NSString *)unitID Error:(AdiscopeError *)error {
    RCTLogInfo(@">>> onRewardedVideoAdFailedToShow\n%@",error);
  }

  //

  // << Interstitial >>
  RCT_EXPORT_METHOD(showInterstitial: (NSString *)interstitialUnitID)
  {
    RCTLogInfo(@">>> interstitial unit id %@", interstitialUnitID);
    [[AdiscopeInterface sharedInstance] loadInterstitial:interstitialUnitID];
  }

  // interstitial callbacks

  - (void)onInterstitialAdLoaded {
    RCTLogInfo(@">>> onInterstitialAdLoaded");
    [[AdiscopeInterface sharedInstance] showInterstitial];
  }

  - (void)onInterstitialAdFailedToLoad:(NSString *)unitID Error:(AdiscopeError *)error {
    RCTLogInfo(@">>> onInterstitialAdFailedToLoad\n%@", error);
  }

  - (void)onInterstitialAdFailedToShow:(NSString *)unitID Error:(AdiscopeError *)error {
    RCTLogInfo(@">>> onInterstitialAdFailedToLoad\n%@", error);
  }

  // << Offerwall >>
  RCT_EXPORT_METHOD(showOfferwall: (NSString *)offerwallUnitID)
  {
    RCTLogInfo(@">>> interstitial unit id %@", offerwallUnitID);
    [[AdiscopeInterface sharedInstance] showOfferwall:offerwallUnitID];
  }

  @end
  ```

---

## [React Native]

- **Usage**

  ```tsx
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

    const rvUnitId: string = Platform.OS === 'android' ? 'ADMOB' : 'ADMOB';
    const interstitialUnitId: string =
      Platform.OS === 'android' ? 'INTER_ADMOB' : 'INTER_TEST';
    const offerwallUnitId: string =
      Platform.OS === 'android' ? 'OFFERWALL' : 'API_OFFERWALL';

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
        RNAdiscopeModule.adInitilize();
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
  ```

  ![rnscreen_1](https://github.com/adiscope/Adiscope-RN-Sample-statnco/assets/60415962/ea0b806a-6f2c-4214-9711-1141824ad2a2)

---

## [**Reward Callback 수신]\*\*

> 콜백 받을 URL(GET API)은 매체사에서 생성해서 TNK에 전달
>
> > signature의 검증은 Adiscope Admin Page에서 발급받은 **secretKey**와 Callback의 parameters을 통해 값을 검증 (아래 sample code 참조)

- **전달 파라미터 (adiscope → 매체)**

  - transctionId : 고유한 ID값. 중복호출 여부에 사용
  - signature : Callback 유효성 검증에 사용되는 MD5 HASH 값
  - unitId : 참여한 광고 UnitID
  - userId : Client에서 setUserID로 설정한 UserID
  - adid : 광고 추적 ID(iOS: IDFA)
  - currency : 보상 화폐 단위
  - amount : 보상 지급 수량

- **매체사 reward callback 처리 sample code**

  ```jsx
  var express = require('express');
  var crypto = require('crypto');
  var secret = 'YOUR_SECRET_KEY';

  app.listen(process.env.PORT || 3412);

  app.get('/', function (req, res) {
    var userId = req.query.userId;
    var rewardUnit = req.query.rewardUnit;
    var rewardAmount = req.query.rewardAmount;
    var transactionId = req.query.transactionId;
    var signature = req.query.signature;
    var plainText = makePlainText(
      userId,
      rewardUnit,
      rewardAmount,
      transactionId,
    );
    var hmac = getHMAC(plainText, secret);

    console.log(hmac);
    console.log(signature);

    // Signatures checking
    if (hmac === signature) {
      // Check for duplicated transaction id here (whether player already has received the reward)
      if (!isDuplicatedReward(transactionId)) {
        // If there's no duplicate - give virtual goods to player
        // and store the transaction id for duplicate checking.
        giveReward(transactionId);

        // On success, return 200 and include 'OK' in the HTTP body
        res.status(200).send('OK');
      } else {
        // reward already received by user
        res.status(200).send('DUPLICATED');
      }
    } else {
      // signature error
      res.status(200).send('SIGNATURE_ERROR');
    }
  });

  function getHMAC(plainText, secret) {
    return crypto.createHmac('md5', secret).update(plainText).digest('hex');
  }

  function makePlainText(userId, rewardUnit, rewardAmount, transactionId) {
    return userId.concat(rewardUnit, rewardAmount, transactionId);
  }

  function isDuplicatedReward(transactionId) {
    // check transaction id is duplicated or not
    return false;
  }

  function giveReward(transactionId) {
    // give reward to user
    // store transaction id for future checking
  }
  ```

- Reward Callback 재요청 정책
  > 매체사를 통해 200 상태 값을 adiscope이 수신하지 못했을 경우, 재요청 정칙
  1. 10초후에 재시도
  2. 1분후에 재시도
  3. 10분에 재시도
  4. 30분후에 재시도
  5. 1시간후에 재시도
  6. 6시간후에 재시도
  7. 6시간후에 재시도
  8. 12시간후에 재시도
  9. 12시간후에 재시도
  10. 12시간후에 재시도
  11. 재시도 하지 않음
