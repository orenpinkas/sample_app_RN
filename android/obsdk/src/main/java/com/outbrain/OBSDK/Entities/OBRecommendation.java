package com.outbrain.OBSDK.Entities;

import java.io.Serializable;
import java.util.Date;

/** @brief An interface that represents a single content recommendation.
 *
 * The OBRecommendationsResponse object that you receive in response to a __fetchRecommendations__ request contains a list of OBRecommendation objects.\n
 * Each OBRecommendation contains the following properties:\n
 * <ul>
 *    <li><strong>isVideo</strong> - is there a video embedded in the recommended article.
 *    <li><strong>isPaid</strong> - true if this a 3rd-party (paid) recommendation, and false if this an organic link to your own site/app.
 *    <li><strong>thumbnail</strong> - a thumbnail image related to the recommendation.
 *    <li><strong>content</strong> - the recommendation's title.
 *    <li><strong>author</strong> - the author of the recommended content.
 *    <li><strong>url</strong> - the recommendation URL.
 *    <li><strong>publishDate</strong> - the date the recommended content was published.
 *    <li><strong>advertiserName</strong> - the name of the content advertiser.
 *    <li><strong>paidContentId</strong> - in case of a paid recommendation, the publisher's ID for the recommendation.
 *    <li><strong>isSameSource</strong> - is the recommendation from the same source as the one the user is currently viewing.
 *    <li><strong>sourceName</strong> - the name of the recommendation's source.
 * </ul>
 *
 * @note Please see the "Outbrain Android SDK Programming Guide" for more detailed explanations about how to integrate with Outbrain.
 *
 * @see OBThumbnail
 */
@SuppressWarnings("unused")
public interface OBRecommendation extends Serializable {
  /** @brief The position of the recommendation. */
  String getPosition();

  /** @brief Is there a video embedded in the recommended article. */
  boolean isVideo();

  /** @brief True if this a 3rd-party (paid) recommendation, and false if this an internal link to your own site/app. */
  boolean isPaid();

  /** @brief A thumbnail image to be displayed with the recommendation.
   *
   * @see OBThumbnail
   */
  OBThumbnail getThumbnail();

  /** @brief In case of recommendation with logo - OBThumbnail logo. */
  OBThumbnail getLogo();

  /** @brief The recommendation's title. */
  String getContent();
  /**
   * @brief The recommendation's description (if available). */
  String getDescription();

  /** @brief The author of the recommended content. */
  String getAuthor();

  /** @brief The date the recommended content was published. */
  Date getPublishDate();

  /** @brief The name of the content advertiser. */
  String getAdvertiserName();

  /** @brief In case of a paid recommendation, the publisher's ID for the recommendation. */
  String getPaidContentId();

  /** @brief Is the recommendation from the same source as the one the user is currently viewing. */
  boolean isSameSource();

  /** @brief The name of the recommendation's source. */
  String getSourceName();

  /** @brief In Smartfeed if rec is in special card - this is the category name (trending in category). */
  String getCategoryName();

  /** @brief True if this recommendation is an RTB (paid) recommendation, and false if this a regular paid recommendation */
  @Deprecated // As of release 3.0.x, replaced by shouldDisplayDisclosureIcon()
  boolean isRTB();

  /** @brief True if app developer should show the disclosure icon for this specific rec */
  boolean shouldDisplayDisclosureIcon();

  /** @brief In case of a paid RTB recommendation - this will contain the "Ad Choices" icon url and click url */
  OBDisclosure getDisclosure();

  /** @brief The audience campaigns label - null if not audience campaigns */
  String getAudienceCampaignsLabel();

  /** @brief The CTA text */
  String getCtaText();

  /** @brief The request ID associated with the recommendation. */
  String getReqID();
}
