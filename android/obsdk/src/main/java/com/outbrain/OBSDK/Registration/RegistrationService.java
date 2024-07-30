package com.outbrain.OBSDK.Registration;

import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.OutbrainException;

public class RegistrationService {
  private static RegistrationService mInstance = null;

  public static boolean WAS_INITIALIZED = false;

  private volatile OBLocalSettings localSettings;

  private RegistrationService() {

  }

  public static RegistrationService getInstance(){
    if (mInstance == null)
    {
      mInstance = new RegistrationService();
    }
    return mInstance;
  }

  public void setLocalSettings(OBLocalSettings localSettings) {
    this.localSettings = localSettings;
  }

  public void setTestMode(boolean testMode) {
    localSettings.setTestMode(testMode);
  }

  public void setTestRTB(boolean testRTB) {
    localSettings.setTestRTB(testRTB);
  }

  public void setTestLocation(String location) {
    localSettings.setTestLocation(location);
  }

  public void register(String partnerKey) {
    if (!WAS_INITIALIZED) {
      if (partnerKey == null || partnerKey.equals("")) {
        throw new OutbrainException("Partner key must have a non-null value");
      }
      localSettings.partnerKey = partnerKey;

      WAS_INITIALIZED = true;
    }
  }

  public String getPartnerKey() {
    return localSettings.partnerKey;
  }
  public boolean wasInitialized() {
    return WAS_INITIALIZED;
  }
}
