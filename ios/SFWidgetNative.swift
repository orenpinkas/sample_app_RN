import OutbrainSDK
import React

@objc(SFWidgetNative)
class SFWidgetNative: RCTViewManager {

  override func view() -> (SFWidgetWrapper) {
    return SFWidgetWrapper()
  }

  @objc override static func requiresMainQueueSetup() -> Bool {
    return false
  }
    
    @objc func create(_ node: NSNumber, args: NSDictionary) {
        DispatchQueue.main.async {
            let uiManager = self.bridge?.module(for: RCTUIManager.self) as? RCTUIManager
            if let view = uiManager?.view(forReactTag: node) as? SFWidgetWrapper {
                view.create(args: args)
            }
        }
    }
}

class SFWidgetWrapper : UIView, SFWidgetDelegate {
    
    var sfWidget: SFWidget!
    let OBEventModuleInstance = OBEventModule()
    var widgetId: String!

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
    
    func create(args: NSDictionary) {
        guard let widgetId = args["widgetId"] as? String else {
          print("widgetId is missing")
          return
        }
        self.widgetId = widgetId
        configure(args:args);
      }
      
      private func configure(args: NSDictionary) {
        guard let widgetId = args["widgetId"] as? String,
              let widgetIndex = args["widgetIndex"] as? Int,
              let articleUrl = args["articleUrl"] as? String,
              let partnerKey = args["partnerKey"] as? String,
              let darkMode = args["darkMode"] as? Bool,
              let packageVersion = args["packageVersion"] as? String else {
          print("Required widget arguments are missing")
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
        SFWidget.enableReactNativeMode(RN_packageVersion: packageVersion)
        sfWidget.enableEvents()
        
        sfWidget.configure(with: self, url: articleUrl, widgetId: widgetId, widgetIndex: widgetIndex, installationKey: partnerKey, userId: nil, darkMode: darkMode, isSwiftUI: true)
      }
    
    func view() -> UIView {
        return sfWidget
    }
    
    
    func onRecClick(_ url: URL) {
      let args: [String: Any] = [
        "widgetId": widgetId!,
        "url": url.absoluteString
      ]
     self.OBEventModuleInstance.sendWidgetEvent("onRecClick", withArgs: args)
    }
    
    func didChangeHeight(_ newHeight: CGFloat) {
      let args: [String: Any] = [
        "widgetId": widgetId!,
        "height": newHeight
      ]
     self.OBEventModuleInstance.sendWidgetEvent("didChangeHeight", withArgs: args )
    }
    
    func onOrganicRecClick(_ url: URL) {
      let args: [String: Any] = [
        "widgetId": widgetId!,
        "url": url.absoluteString
      ]
     self.OBEventModuleInstance.sendWidgetEvent("onOrganicRecClick", withArgs: args )
    }
    
    func widgetEvent(_ eventName: String, additionalData: [String : Any]) {
        let args: [String: Any] = [
          "widgetId": widgetId!,
          "eventName": eventName,
          "additionalData": additionalData
        ]
     self.OBEventModuleInstance.sendWidgetEvent("onWidgetEvent", withArgs: args)
    }
}
