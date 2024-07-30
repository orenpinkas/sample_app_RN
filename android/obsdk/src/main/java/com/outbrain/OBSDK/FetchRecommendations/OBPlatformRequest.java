package com.outbrain.OBSDK.FetchRecommendations;

public class OBPlatformRequest extends OBRequest {

    /**
     * @brief The Bundle URL for the app that displays this widget - for platforms.
     */
    private final String bundleUrl;

    /**
     * @brief The Portal URL for the app that displays this widget - for platforms.
     */
    private final String portalUrl;

    /**
     * @brief Additional source breakdown available for platforms.
     */
    private String psub;

    /**
     * @brief For language breakdown of the sources
     */
    private final String lang;

    /**
     * @brief For Outbrain News - language of the news, for example "es"
     */
    private String newsFrom;

    /**
     * @brief For Outbrain News - list of sources, separated by comma ",", for example "IAB1,IAB3,IAB10"
     */
    private String news;


    public OBPlatformRequest(String widgetId, String bundleUrl, String portalUrl, String lang) {
        super(null, widgetId);
        this.bundleUrl = bundleUrl;
        this.portalUrl = portalUrl;
        this.lang = lang;
    }

    public String getBundleUrl() {
        return bundleUrl;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public String getPsub() {
        return psub;
    }

    public String getLang() {
        return lang;
    }

    public String getNewsFrom() {
        return newsFrom;
    }

    public String getNews() {
        return news;
    }

    public void setPsub(String psub) {
        this.psub = psub;
    }

    public void setNewsFrom(String newsFrom) {
        this.newsFrom = newsFrom;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
