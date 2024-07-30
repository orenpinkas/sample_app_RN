/*
 The OBSDK (OutBrain Software Developer's Kit) package contains classes that allow you to use the Outbrain content discovery product.

 The Outbrain product enables you to recommend high-quality content that is interesting to your users, from within
 your mobile app. This allows you to drive more traffic within your own app, and/or to create a new monetization
 option by collecting pay-per-click revenue from 3rd-party content publishers.

 The package's main interface is implemented in the Outbrain class.

 @note Please see the "Outbrain Android SDK Programming Guide" for more detailed explanations about how to integrate with Outbrain.

 @see Outbrain
 */
package com.outbrain.OBSDK;

import android.content.Context;
import android.view.ViewGroup;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.Viewability.OBFrameLayout;
import com.outbrain.OBSDK.Viewability.SFViewabilityService;

/**
 *  @brief OBSDK's main interface; for fetching content recommendations and reporting clicks.
 *
 *	The Outbrain class is a singleton that must be initialized and registered (by calling __register__) when your app is initialized.\n
 *	Call __fetchRecommendations__ to retrieve content recommendations.\n
 *	Call __getUrl__ to retrieve the paid.outbrain redirect URL for paid recommendations
 *                  and the original url for organic recommendation (with click registration behin the scene to traffic.outbrain.com
 *
 */
public class Outbrain {

    public static final String SDK_VERSION = "4.30.10";
    
    private static final String LOG_TAG = "Outbrain";

    /**
     @brief Reads the Outbrain configuration file and stores its settings for your app.\n

     (Place the configuration file in the "assets" folder under your project folder.)\n
     This method also stores the session's UDID (Unique Device Identifier), allowing content recommendations to be personalized per user.\n

     @param applicationContext - an object that implements the android.content.Context interface, i.e - the ApplicationContext.
     @param appKey             - The app key provided to you by your Account Manager.
     @note Call this method once during your app's initialization, before calling any other method. The best practice is to call it \n
     in the onCreate function of the first Activity, or when generating the ApplicationContext, if your app has one.

     @throws OutbrainException
     @see OutbrainException
     */
    public static void register(Context applicationContext, String appKey) throws OutbrainException {
        OutbrainService.getInstance().register(applicationContext.getApplicationContext(), appKey);
    }

    /**
     * @brief configure Viewablity on a per-listing basis for regular widget only
     *
     * By configuring the "view" param for VPL (Viewability per listing) the SDK will take care of monitoring and reporting the viewability event on a per listing basis.
     *
     * @param outbrainRecContainer - The OBFrameLayout container holding the recommendation content
     * @param rec - The associated OBRecommendation for which the content is displayed.
     **/
    public static void configureViewabilityPerListingFor(OBFrameLayout outbrainRecContainer, OBRecommendation rec) {
        SFViewabilityService.getInstance().startReportViewability(2000); // 2 seconds interval
        SFViewabilityService.registerOBFrameLayout(outbrainRecContainer, rec);
    }

    /**
     * @brief configure Viewablity on a per-listing basis for regular widget only
     *
     * By configuring the "view" param for VPL (Viewability per listing) the SDK will take care of monitoring and reporting the viewability event on a per listing basis.
     *
     * @param viewGroup - The ViewGroup container holding the recommendation content.
     * @param rec - The associated OBRecommendation for which the content is displayed.
     **/
    public static void configureViewabilityPerListingFor(ViewGroup viewGroup, OBRecommendation rec) {
        SFViewabilityService.getInstance().startReportViewability(2000); // 2 seconds interval
        SFViewabilityService.registerViewGroup(viewGroup, rec);
    }


    /**
     * @brief Retrieves content recommendations.

     * @note Although the __fetchRecommendations__ requests are asynchronous, they are all stored
     * in the same queue, so they are handled in the order in which they were called.
     *
     * @note You must call __fetchRecommendations__ to retrieve the original URL, both for your own organic links and for paid 3rd-party links.
     *
     * @param request - the request object.
     * @param handler - an event handler that handles the recommendation list that is fetched.
     *
     * @see OBRequest
     * @see RecommendationsListener
     * @see com.outbrain.OBSDK.Entities.OBRecommendationsResponse
     */
    public static void fetchRecommendations(OBRequest request, RecommendationsListener handler) {
        OutbrainService.getInstance().fetchRecommendations(request, handler);
    }

    /**
     * @brief Maps the given recommendation to the re-direct paid.outbrain.com URL and for organic recommendation to the original URL + register the click to traffic.outbrain "behind the scene".
     *
     * This function maps the given recommendation to the re-direct paid.outbrain.com URL and
     * for organic recommendation to the original URL + register the click to traffic.outbrain "behind the scene".
     * Open paid links in a web view or external browser.\n
     * In the case of an organic link, translate the web URL into a mobile URL (if necessary) and show the content natively. \n
     * (See the Outbrain Journal sample app for an example of how to do this.)
     *
     * @param rec - the recommendation that has been clicked.
     * @note It is recommended that your app hold the OBRecommendationsResponse object as an instance variable in the Activity.
     *
     * @return The web URL to redirect to.
     * @note If it's necessary to map the web URL to a mobile URL, this must be done in your code.
     * @see OBRecommendation
     */
    public static String getUrl(OBRecommendation rec) {
        return OutbrainService.getInstance().getUrl(rec);
    }

    /**
     * @brief Activates/deactivates Outbrain test mode.
     *
     * Activate test mode while developing and testing your app. \n
     * This prevents Outbrain from performing operational actions such as reporting and billing.
     *
     * @note The default value of the test mode is "false"; i.e. the mode is operational unless you call setTestMode(true).
     *
     * @param testMode - a boolean flag; set to true to activate test mode, or false to deactivate test mode.
     */
    public static void setTestMode(boolean testMode) {
        OutbrainService.getInstance().setTestMode(testMode);
    }

    /**
     * @brief Activates/deactivates RTB recs simultation
     *
     * Setting this flag to "true" will force every Outbrain response (ODB) to include at least 1 RTB rec
     *
     * @note The default value of the testRTB is "false"; i.e. the mode is operational unless you call testRTB(true).
     *
     * @param testRTB - a boolean flag; set to true to activate test mode, or false to deactivate test mode.
     */
    public static void testRTB(boolean testRTB) {
        OutbrainService.getInstance().testRTB(testRTB);
    }

    /**
     * @brief Simulate Geo by setting location (country code, for example: "us")
     *
     * Setting location will cause all ODB requests to be sent with the "location" param set to this value
     *
     * @param location - country code, for example: "us"
     */
    public static void testLocation(String location) {
        OutbrainService.getInstance().testLocation(location);
    }

    /**
     * @brief Get the URL you should open in an external browser when the user taps on Outbrain logo
     *
     * This function returns the URL to be opened in an external browser when the user taps on Outbrain logo.
     * The URL contains the user Advertiser ID param which is mandatory for Ad Choices opt-out compliance.
     *
     * @deprecated use {@link #getOutbrainAboutURL()} instead.
     * @param   ctx - an object that implements the android.content.Context interface, i.e - the ApplicationContext.
     * @return  The URL to be opened in an external browser
     **/
    @Deprecated
    public static String getOutbrainAboutURL(Context ctx) {
        return OutbrainService.getInstance().getOutbrainAboutURL();
    }

    /**
     * @brief Get the URL you should open in an external browser when the user taps on Outbrain logo
     *
     * This function returns the URL to be opened in an external browser when the user taps on Outbrain logo.
     * The URL contains the user Advertiser ID param which is mandatory for Ad Choices opt-out compliance.
     *
     * @return  The URL to be opened in an external browser
     **/
    public static String getOutbrainAboutURL() {
        return OutbrainService.getInstance().getOutbrainAboutURL();
    }

    /**
     * @brief app developer can check whether the SDK was already initilized.
     *
     * @return "true" if SDK was initilized
     **/
    public static boolean SDKInitialized(){
        return OutbrainService.getInstance().wasInitialized();
    }

    /**
     * @brief app developer should call this method if publisher app is running on Aura (iron source platform)
     * @note This method must be called BEFORE the call to Outbrain.register()
     **/
    public static void setIronSourceIntegration() {
        OutbrainService.getInstance().setIronSourceIntegration(true);
    }
}
