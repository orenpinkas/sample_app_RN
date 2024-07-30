package com.outbrain.OBSDK.Entities;

import com.outbrain.OBSDK.FetchRecommendations.OBRequest;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/** @brief The object sent as a response to a __fetchRecommendations__ request.
 *
 * It contains the following properties:
 * <ul>
 *    <li><strong>status</strong> - the end status of the __fetchRecommendations__ operation.
 *    <li><strong>request</strong> - an object containing properties related to the request, e.g. widgetId.
 *    <li><strong>recommendationsList</strong> - a list of content recommendations, which can be retrieved in full or iterated over by index.
 * </ul>
 * @see OBRecommendation
 */
public class OBRecommendationsResponse extends OBBaseEntity implements Serializable {
  private static final String EXEC_TIME = "exec_time";
  private static final String STATUS = "status";
  private static final String REQUEST = "request";
  private static final String DOCUMENTS = "documents";
  private static final String SETTINGS = "settings";
  private static final String VIEWABILITY_ACTIONS = "viewability_actions";

  // For branded carousel item
  private static final String FEATURES = "features";
  private static final String CAROUSEL = "carousel";

  private int execTime;
  private OBResponseStatus status;
  private OBResponseRequest request;
  private OBRecommendationsBulk recommendationsBulk;
  private OBSettings settings;
  private OBRequest obRequest;


  public OBSettings getSettings() {
    return settings;
  }

  /** @brief Get the request property. */
  public OBResponseRequest getRequest() {
    return request;
  }

  /** @brief Get the __fetchRecommendations__ status.
   *
   * @see OBResponseStatus
   */
  @SuppressWarnings("unused")
  public OBResponseStatus getStatus() {
    return status;
  }

  @SuppressWarnings("unused")
  public int getExecTime() {
    return execTime;
  }


  public OBRecommendationsResponse(JSONObject jsonObject, OBRequest obRequest) {
    super(jsonObject);
    if (jsonObject == null) {
      return;
    }

    // Storing each json item in variable
    execTime = jsonObject.optInt(EXEC_TIME);
    status = new OBResponseStatus(jsonObject.optJSONObject(STATUS));
    request = new OBResponseRequest(jsonObject.optJSONObject(REQUEST));
    recommendationsBulk = new OBRecommendationsBulk(jsonObject.optJSONObject(DOCUMENTS), request.getReqId());
    settings = new OBSettings(jsonObject.optJSONObject(SETTINGS));

    // Parse branded carousel settings (if exists)
    JSONObject featuresJsonObject = jsonObject.optJSONObject(FEATURES);
    if (featuresJsonObject != null) {
      JSONObject carouselJsonObject = featuresJsonObject.optJSONObject(CAROUSEL);
      settings.setBrandedItemSettings(new OBBrandedItemSettings(carouselJsonObject));
    }

    OBViewabilityActions viewabilityActions = new OBViewabilityActions((jsonObject.optJSONObject(VIEWABILITY_ACTIONS)));
    settings.setViewabilityActions(viewabilityActions);
    this.obRequest = obRequest;
  }

  /** @brief Get the entire ArrayList of recommendations. */
  public ArrayList<OBRecommendation> getAll() {
    return recommendationsBulk != null ? recommendationsBulk.getDocs() : null;
  }

  /** @brief Get a single recommendation at the given index. */
  public OBRecommendation get(int index) {
    return recommendationsBulk != null ? recommendationsBulk.getDocs().get(index) : null;
  }

  public OBRequest getObRequest() {
    return obRequest;
  }

  @Override
  public String toString() {
    return "OBRecommendationsResponse\n\nexecTime: " + execTime + "\nstatus: " + status + "\nrequest: " + request +"\nrecommendationsBulk: " + recommendationsBulk +"\nsettings: " + settings + "\nobRequest: " + obRequest;
  }
}
