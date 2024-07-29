//
//  SFWidgetWrapper.swift
//  sample_app_RN
//
//  Created by Oren Pinkas on 28/07/2024.
//

import Foundation
import UIKit
import WebKit
import OutbrainSDK


@objc(SFWidgetWrapper)
class SFWidgetWrapper: UIView, SFWidgetDelegate {
    
  var sfWidget: SFWidget!
  let OBEventModuleInstance = OBEventModule()
  var widgetId: String?

  // MARK: - Initializers
  
  @objc override init(frame: CGRect) {
    super.init(frame: frame)
    sfWidget = SFWidget(frame: bounds)
    sfWidget.translatesAutoresizingMaskIntoConstraints = false
    addSubview(sfWidget)
    NSLayoutConstraint.activate([
        sfWidget.topAnchor.constraint(equalTo: topAnchor),
        sfWidget.leadingAnchor.constraint(equalTo: leadingAnchor),
        sfWidget.trailingAnchor.constraint(equalTo: trailingAnchor),
        sfWidget.bottomAnchor.constraint(equalTo: bottomAnchor)
    ])
  }
  
  // This initializer is required, but we can make it unavailable
  @available(*, unavailable)
  required init?(coder: NSCoder) {
      fatalError("init(coder:) has not been implemented")
  }
  // MARK: - Setup
  
//  @objc func create(widgetId: String, widgetIndex: NSInteger, articleUrl: String, partnerKey: String, extId: String?, extSecondaryId: String?, pubImpId: String?){
//    self.widgetId = widgetId
//    configure(widgetId: widgetId, widgetIndex: widgetIndex, articleUrl: articleUrl, partnerKey: partnerKey, extId: extId, extSecondaryId: extSecondaryId, pubImpId: pubImpId)
//  }
  
  @objc func create(args: [String: Any]) {
    guard let widgetId = args["widgetId"] as? String else {
      print("widgetId is missing")
      return
    }
    self.widgetId = widgetId
    configure(args:args);
  }
  
  private func configure(args: [String: Any]) {
    guard let widgetId = args["widgetId"] as? String,
          let widgetIndex = args["widgetIndex"] as? Int,
          let articleUrl = args["articleUrl"] as? String,
          let partnerKey = args["partnerKey"] as? String else {
      print("Required keys are missing")
      return
    }
    
    let extId = args["extId"] as? String
    let extSecondaryId = args["extSecondaryId"] as? String
    let pubImpId = args["pubImpId"] as? String
    
    Outbrain.initializeOutbrain(withPartnerKey: partnerKey)
    sfWidget.extId = extId
    sfWidget.extSecondaryId = extSecondaryId
    sfWidget.OBPubImp = pubImpId
    
    SFWidget.infiniteWidgetsOnTheSamePage = true
    sfWidget.enableEvents()
    
    sfWidget.configure(with: self, url: articleUrl, widgetId: widgetId, widgetIndex: widgetIndex, installationKey: partnerKey, userId: nil, darkMode: false, isSwiftUI: true)
  }
    
//  private func configure(args: Any?) {
//      if let argsDict = args as? [String: Any],
//         let articleUrl = argsDict["articleUrl"] as? String,
//         let widgetId = argsDict["widgetId"] as? String,
//         let partnerKey = argsDict["partnerKey"] as? String,
//         let widgetIndex = argsDict["widgetIndex"] as? Int {
//          // extId and extSecondaryId are optional fields
//          let extId = argsDict["extId"] as? String
//          let extSecondaryId = argsDict["extSecondaryId"] as? String
//          let pubImpId = argsDict["pubImpId"] as? String
//          
//          self.sfWidget.extId = extId
//          self.sfWidget.extSecondaryId = extSecondaryId
//          sfWidget.OBPubImp = pubImpId
//          
//          SFWidget.infiniteWidgetsOnTheSamePage = true
//          SFWidget.setIsFlutter(value: true)
//          
//          self.widgetId = widgetId;
//          
//          self.sfWidget.enableEvents()
//          self.sfWidget.configure(with: self,
//                                  url: articleUrl,
//                                  widgetId: widgetId,
//                                  widgetIndex: widgetIndex,
//                                  installationKey: partnerKey,
//                                  userId: nil,
//                                  darkMode: false,
//                                  isSwiftUI: true
//          )
//      } else {
//          // Handle the case where arguments are missing or of the wrong type
//          print("Invalid or missing arguments")
//      }
//  }
  
  func view() -> UIView {
      return sfWidget
  }
  
  func onRecClick(_ url: URL) {
    print("Swift - OnRecClick", url)
    print("widgetIdTest")

  }
  
  func didChangeHeight(_ newHeight: CGFloat) {
    print("Height of SFWidget is \(newHeight)")
    let args: [String: Any] = [
      "widgetId": widgetId,
      "height": newHeight
    ]
    self.OBEventModuleInstance.sendWidgetEvent("didChangeHeight", withArgs: args )
  }
  
  func onOrganicRecClick(_ url: URL) {
      print("Swift - onOrganicRecClick")
  }
  
  func widgetEvent(_ eventName: String, additionalData: [String : Any]) {
      print("Swift - widgetEvent \(eventName)")
      let arguments: [String: Any] = [
          "eventName": eventName,
          "additionalData": additionalData
      ]
      
  }

}
