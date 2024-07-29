
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#import <OutbrainSDK/OutbrainSDK.h>

#import "sample_app_RN-Swift.h"

@interface SFWidgetManager : RCTViewManager
@end

@implementation SFWidgetManager

RCT_EXPORT_MODULE(SFWidget)
RCT_EXPORT_METHOD(create:(nonnull NSNumber*)reactTag args:(NSDictionary *)args) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        SFWidgetWrapper *view = viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:[SFWidgetWrapper class]]) {
            RCTLogError(@"Cannot find SFWidget with tag #%@", reactTag);
            return;
        }
      [view createWithArgs:args];
    }];

}

- (UIView *)view
{
  return [[SFWidgetWrapper alloc] init];
}

@end
