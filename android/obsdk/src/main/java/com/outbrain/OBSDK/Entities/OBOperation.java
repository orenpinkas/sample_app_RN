package com.outbrain.OBSDK.Entities;

import com.outbrain.OBSDK.FetchRecommendations.OBRequest;

public class OBOperation {
  private OBRequest request;
  private final OBRecommendationsResponse response;

  public OBOperation(OBRequest request, OBRecommendationsResponse response) {
    this.request = request;
    this.response = response;
  }

  public OBRequest getRequest() {
    return request;
  }

  @SuppressWarnings("unused")
  public void setRequest(OBRequest request) {
    this.request = request;
  }

  public OBRecommendationsResponse getResponse() {
    return response;
  }

}
