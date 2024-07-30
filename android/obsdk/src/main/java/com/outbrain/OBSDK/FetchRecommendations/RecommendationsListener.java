package com.outbrain.OBSDK.FetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
/**
 * @brief An interface that must be implemented by the event handler your code provides when calling __fetchRecommendations__.
 *
 * @see onOutbrainRecommendationsSuccess
 * @see onOutbrainRecommendationsFailure
 */
@SuppressWarnings("UnusedParameters")
public interface RecommendationsListener {
  /**
   * @brief Called upon successful retrieval of content recommendations.
   *
   * @param recommendations - an object containing content recommendations.
   * @see OBRecommendationsResponse
   */
  void onOutbrainRecommendationsSuccess(final OBRecommendationsResponse recommendations);

  /**
   * @brief Called upon failure to retrieve content recommendations.
   *
   * @note Errors are also logged to the Outbrain log.
   * @param ex - the exception that caused the failure.
   */
  void onOutbrainRecommendationsFailure(final Exception ex);
}