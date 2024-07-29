//
//  OBEventModule.h
//  sample_app_RN
//
//  Created by Oren Pinkas on 28/07/2024.
//

#ifndef OBEventModule_h
#define OBEventModule_h

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface OBEventModule : RCTEventEmitter <RCTBridgeModule>

- (void)sendWidgetEvent:(NSString *)name withArgs:(NSDictionary *)args;

@end

#endif /* OBEventModule_h */
