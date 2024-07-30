package com.outbrain.OBSDK;

import com.outbrain.OBSDK.Entities.OBRecommendation;

public interface OBClickListener {

    /**
     * Called when a user taps on a specific recommendation in the feed.
     * @param rec The OBRecommendation instance containing the rec data.
     */
    void userTappedOnRecommendation(OBRecommendation rec);

    /**
     * Called when a user taps on the ad choices icon within an OPA rec.
     * Handling clicks on "ad choices" icon is mandatory for compliance with OPA.
     * @param url The "ad choices" click url to be opened in a new browser.
     */
    void userTappedOnAdChoicesIcon(String url);

    /**
     * Called when a user taps on the Outbrain or Smartfeed logo.
     * App developer should handle the click by calling:
     * String url = Outbrain.getOutbrainAboutURL(ctx);
     * and open the url in an external browser
     */
    void userTappedOnAboutOutbrain();

    /**
     * Called when a user taps on a video being played in the Smartfeed.
     * @param url The video "click url" to be opened in a new browser.
     */
    void userTappedOnVideo(String url);
}
