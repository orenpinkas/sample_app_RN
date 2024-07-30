
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#import <OutbrainSDK/OutbrainSDK.h>

#import "sample_app_RN-Swift.h"

@interface SFWidgetManager : RCTViewManager
@end

@implementation SFWidgetManager

RCT_EXPORT_MODULE(SFWidget)

// 'create' is called by React Native after the native UIView SFWidgetWrapper is mounted onto the screen (in 'componentDidMount')
// 'create' instantiates the SFWidget subview with the widget parameters passed from RN
RCT_EXPORT_METHOD(create:(nonnull NSNumber*)reactTag args:(NSDictionary *)args) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
      UIView *tempView = viewRegistry[reactTag];
      if (!tempView || ![tempView isKindOfClass:[SFWidgetWrapper class]]) {
          RCTLogError(@"Cannot find SFWidget with tag #%@", reactTag);
          return;
      }
      SFWidgetWrapper *view = (SFWidgetWrapper *)tempView;
      [view createWithArgs:args];
    }];

}

- (UIView *)view
{
  return [[SFWidgetWrapper alloc] init];
}

@end
