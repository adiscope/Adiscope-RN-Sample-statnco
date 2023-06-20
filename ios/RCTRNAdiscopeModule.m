// RCTRNAdiscopeModule.m

#import <Foundation/Foundation.h>
#import <React/RCTLog.h>

#import "RCTRNAdiscopeModule.h"

@implementation RCTRNAdiscopeModule

// To export a module named RNAdiscopeModule
RCT_EXPORT_MODULE(RNAdiscopeModule)

// << Adiscope SDK Initialize >>
// TNK로부터 공유 받은 mediaId, secretKey 등록
RCT_EXPORT_METHOD(adInitialize)
{
  [[AdiscopeInterface sharedInstance] setMainDelegate:self];
  [[AdiscopeInterface sharedInstance] initialize:@"259" mediaSecret:@"fd1af38c1d244585a3e0421e6ed85710" callBackTag:nil];
}

// initialize callback
- (void)onInitialized:(BOOL)isSuccess {
  RCTLogInfo(@">>> onInitialized : %d", isSuccess);
}

// << setUserId >>

RCT_EXPORT_METHOD(setUserId: (NSString *)userId) {
  [[AdiscopeInterface sharedInstance] setUserId:userId];
  RCTLogInfo(@">>> call setUserId() : userId => %@", userId);
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
  RCTLogInfo(@">>> call onRewardedVideoAdFailedToLoad()");
  RCTLogInfo(@">>> error : %@", error);
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
