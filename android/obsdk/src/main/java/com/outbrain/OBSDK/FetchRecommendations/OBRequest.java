package com.outbrain.OBSDK.FetchRecommendations;

import java.io.Serializable;

/**
 * @brief This object contains all the details required to produce content recommendations.
 *
 * It is passed as a parameter to __fetchRecommendations__.\n
 * Its properties are:\n
 * <ul>
 *    <li><strong>url (mandatory)</strong> - the web URL that the user is currently viewing. (For in-feed or Homepage installations, please consult with your account manager.)\n
 *    <li><strong>idx (mandatory)</strong> - 0 if sending a single OBRequest from the page. Otherwise, start from 0 and increment the index for each OBRequest sent from the page.\n
 *    <li><strong>widgetId (mandatory)</strong> - a string ID for the widget in which content recommendations will be displayed. This ID is assigned by your account manager.\n
 *		    	(Please consult with your account manager if you do not know your widgetIds.)\n
 *			    The widgetId is mapped to configuration settings that define how recommendations will be displayed (e.g. with or without thumbnail images).\n
 *    <li><strong>additionalData</strong> - custom data that you want to associate with the viewed URL.\n
 *                                          Outbrain stores this value and returns it if and when this URL is returned as a recommendation.
 *    <li><strong>mobileSubgroup</strong> - an identifier for the subset of your organic links that may be used within mobile apps. (Discuss this value with your Outbrain account manager.)
 *
 * @note The three mandatory properties (which must be provided to the __OBRequest__ constructor) are __url__, __idx__ and __widgetId__.
 *
 * @note Please see the "Outbrain Android SDK Programming Guide" for more detailed explanations about how to integrate with Outbrain.
 *
 *
 * @see fetchRecommendations
 * @see OBRecommendationsResponse
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public class OBRequest implements Serializable {
  private String url;
  private String widgetId;
  private int idx;
  private String externalID;

  private String externalSecondaryId;

  private String pubImpId;

  private String fab; // abTestVal

  private int lastCardIdx = 0; // lastCardIdx is the index of the last “child widget” inside the smartfeed
  private int lastIdx = 0; // lastIdx is the index of the last widget on the page (because we can load widgets async)
  private boolean isMultivac;

  public OBRequest() {
    this(null, null);
  }

  @SuppressWarnings("WeakerAccess")
  public OBRequest(String url, String widgetId) {
    this(url, 0, widgetId);
  }

  public OBRequest(String url, int pageViewIndex, String widgetId) {
    this.url = url;
    this.idx = pageViewIndex;
    this.widgetId = widgetId;
  }

  /**
   * @param url - the web URL that the user is currently viewing.
   * @brief Set the url property value.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @param idx - the page view widget index. You must assign unique, sequential numeric IDs to the widgets on your page, to be passed to Outbrain.
   * @brief Set the idx property value.
   */
  public void setWidgetIndex(int idx) {
    this.idx = idx;
  }


  /**
   * @param widgetId - a string ID (assigned by your account manager) for the widget.
   * @brief Set the widgetId property value.
   */
  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }



  public String getWidgetId() {
    return widgetId;
  }

  public int getIdx() {
    return idx;
  }

  public String getUrl() {
    return url;
  }

  public String getExternalID() {
    return externalID;
  }

  public String getExternalSecondaryId() {
    return externalSecondaryId;
  }

  public String getPubImpId() {
    return pubImpId;
  }

  public void setExternalID(String externalID) {
    this.externalID = externalID;
  }

  public void setExternalSecondaryId(String externalSecondaryId) {
    this.externalSecondaryId = externalSecondaryId;
  }

  public void setPubImpId(String pubImpId) {
    this.pubImpId = pubImpId;
  }

  public boolean isMultivac() {
    return isMultivac;
  }

  public void setMultivac(boolean multivac) {
    isMultivac = multivac;
  }

  public int getLastCardIdx() {
    return lastCardIdx;
  }

  public void setLastCardIdx(int lastCardIdx) {
    this.lastCardIdx = lastCardIdx;
  }

  public int getLastIdx() {
    return lastIdx;
  }

  public void setLastIdx(int lastIdx) {
    this.lastIdx = lastIdx;
  }

  public String getFab() {
    return fab;
  }

  public void setFab(String fab) {
    this.fab = fab;
  }

  @Override
  public String toString() {
    return "WidgetId:" + getWidgetId() + "; WidgetIndex:" + getIdx() + "; URL:" + getUrl();
  }
}
