//
//  OBEventModule.m
//  sample_app_RN
//
//  Created by Oren Pinkas on 28/07/2024.
//

#import "OBEventModule.h"

@implementation OBEventModule
{
  bool hasListeners;
}

RCT_EXPORT_MODULE();

+ (id)allocWithZone:(NSZone *)zone {
  static OBEventModule *sharedInstance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    sharedInstance = [super allocWithZone:zone];
  });
  return sharedInstance;
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"didChangeHeight"]; // Add all event names you want to support
}

// Will be called when this module's first listener is added.
-(void)startObserving {
  hasListeners = YES;
  // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
  hasListeners = NO;
  // Remove upstream listeners, stop unnecessary background tasks
}

- (void)sendWidgetEvent:(NSString *)name withArgs:(NSDictionary *)args
{
  if (hasListeners) {// Only send events if anyone is listening
    [self sendEventWithName:name body:args];
  }
}


@end
