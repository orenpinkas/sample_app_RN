package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

import java.io.Serializable;

/** @brief Indicates the success/failure status of a request to the SDK.
 * <ul>
 *    <li><strong>statusId</strong> - the numeric status ID.
 *    <li><strong>content</strong> - a string describing the status.
 * </ul>
 *
 */
public class OBResponseStatus extends OBBaseEntity implements Serializable {
  private static final String STATUSID = "id";
  private static final String CONTENT = "content";
  private static final String DETAILS = "detailed";

  /** @brief Get the numeric status ID. */
  public int getStatusId() {
    return statusId;
  }

  /** @brief Get the string describing the status. */
  public String getContent() {
    return content;
  }

  /** @brief Get the string describing the details. */
  public String getDetails() {
    return details;
  }

  private int statusId;
  private String content;
  private String details;

  public OBResponseStatus(JSONObject jsonObject) {
    super(jsonObject);
    if (jsonObject == null) {
      return;
    }
    // Storing each json item in variable
    statusId = jsonObject.optInt(STATUSID);
    content = jsonObject.optString(CONTENT);
    details = jsonObject.optString(DETAILS);
  }

  public OBResponseStatus(int statusId, String content) {
    super(null);
    this.statusId = statusId;
    this.content = content;
  }

  @Override
  public String toString() {
    return "OBResponseStatus - statusId: " + statusId + ", content: " + content + ", details: " + details;
  }
}

