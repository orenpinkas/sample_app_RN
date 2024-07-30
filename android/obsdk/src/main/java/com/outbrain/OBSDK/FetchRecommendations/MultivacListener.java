package com.outbrain.OBSDK.FetchRecommendations;

import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;

import java.util.ArrayList;

public interface MultivacListener {
    /**
     * @desc Called upon successful retrieval of multivac response.
     *
     * @param cardsResponseList - an object containing a list of "cards", each card is essentially OBRecommendationsResponse
     * @see OBRecommendationsResponse
     */
    void onMultivacSuccess(final ArrayList<OBRecommendationsResponse> cardsResponseList, int feedIdx, boolean hasMore);

    /**
     * @brief Called upon failure to retrieve multivac response.
     *
     * @note Errors are also logged to the Outbrain log.
     * @param ex - the exception that caused the failure.
     */
    void onMultivacFailure(final Exception ex);
}
