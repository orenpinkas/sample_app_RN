package com.outbrain.OBSDK.Utilities;

import com.outbrain.OBSDK.Entities.OBOperation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.OBUtils;
import com.outbrain.OBSDK.SFWebView.OutbrainBusProvider;

import java.util.HashMap;


public class RecommendationsTokenHandler {

  private final HashMap<String, String> tokensMap = new HashMap<>();


  public String getTokenForRequest(OBRequest request) {
    String requestUrl = OBUtils.getUrlFromOBRequest(request);
    if (request.getIdx() == 0 && !request.isMultivac()) {
        return null;
    } else if (tokensMap.containsKey(requestUrl)) {
        return tokensMap.get(requestUrl);
    }

    return null;
  }

  public void setTokenForResponse(OBOperation operation) {
    OBRequest request = operation.getRequest();
    String requestUrl = OBUtils.getUrlFromOBRequest(request);
    OBRecommendationsResponse response = operation.getResponse();
    String responseToken = response.getRequest().getToken();
    tokensMap.put(requestUrl, responseToken);

    // Notify SDK-Bridge widget if exists about the "t" param
    OutbrainBusProvider.TParamsEvent event = new OutbrainBusProvider.TParamsEvent(responseToken);
    OutbrainBusProvider.getInstance().post(event);
  }
}