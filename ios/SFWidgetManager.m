#import <MapKit/MapKit.h>

#import <React/RCTViewManager.h>

#import <OutbrainSDK/OutbrainSDK.h>

#import "sample_app_RN-Swift.h"

@interface SFWidgetManager : RCTViewManager
@end

@implementation SFWidgetManager

RCT_EXPORT_MODULE(SFWidget)
RCT_EXPORT_VIEW_PROPERTY(widgetIdTest, NSString)

- (UIView *)view
{
  return [[SFWidgetWrapper alloc] init];
}

@end
