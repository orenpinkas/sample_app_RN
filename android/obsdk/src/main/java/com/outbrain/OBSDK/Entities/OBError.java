package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

public class OBError extends OBBaseEntity {
  private static final String EXEC_TIME = "exec_time";
  private static final String STATUS = "status";
  private static final String REQUEST = "request";
  private static final String SETTINGS = "settings";

  private int execTime;
  private OBResponseRequest request;
  public OBResponseStatus status;
  private OBSettings settings;

  public OBError(JSONObject jsonObject) {
    super(jsonObject);
    if (jsonObject == null) {
      return;
    }
    execTime = jsonObject.optInt(EXEC_TIME);
    status = new OBResponseStatus(jsonObject.optJSONObject(STATUS));
    request = new OBResponseRequest(jsonObject.optJSONObject(REQUEST));
    settings = new OBSettings(jsonObject.optJSONObject(SETTINGS));
  }

  public OBError(int statusId, String reason) {
    super(null);
    status = new OBResponseStatus(statusId, reason);
  }

  public int getExecTime() {
    return execTime;
  }

  public OBResponseRequest getRequest() {
    return request;
  }

  public OBSettings getSettings() {
    return settings;
  }
}
