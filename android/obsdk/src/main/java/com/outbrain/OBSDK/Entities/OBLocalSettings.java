package com.outbrain.OBSDK.Entities;

import com.outbrain.OBSDK.OutbrainException;
import org.json.JSONException;
import org.json.JSONObject;

public class OBLocalSettings {
  private static final String PARTNERKEY = "PartnerKey";

  public String partnerKey;
  public String version;
  private boolean testMode = false;
  private boolean testRTB = false;
  private boolean isIronSourceInstallation = false;
  public String testLocation = null;

  public OBLocalSettings updateSettings(JSONObject jsonObject) throws OutbrainException {
    if (jsonObject == null) {
      return this;
    }

    try {
      this.partnerKey = jsonObject.getString(PARTNERKEY);
    } catch (JSONException e) {
      e.printStackTrace();
      throw new OutbrainException("Config file is invalid. Please check it's well formatted and contains partnerKey parameter");
    }
    return this;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public void setTestRTB(boolean testRTB) {
    this.testRTB = testRTB;
  }

  public void setTestLocation(String location) {
    this.testLocation = location;
  }

  public void setIronSourceInstallation(boolean ironSourceInstallation) {
    isIronSourceInstallation = ironSourceInstallation;
  }

  public boolean testRTB() {
    return testRTB;
  }

  public String getTestLocation() {
    return testLocation;
  }

  public boolean isTestMode() {
    return testMode;
  }

  public boolean isIronSourceInstallation() {
    return isIronSourceInstallation;
  }
}
