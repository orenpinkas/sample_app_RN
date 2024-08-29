#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(SFWidgetNative, RCTViewManager)
RCT_EXTERN_METHOD(create:(nonnull NSNumber *)node args:(NSDictionary *)args)
@end
