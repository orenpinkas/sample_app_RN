package com.outbrain.OBSDK.Entities;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @brief Represents a thumbnail image to be displayed with a recommendation.
 * <p>
 * It contains the following properties:
 * <ul>
 * <li><strong>url</strong> - a URL pointing to the image file.
 * <li><strong>width</strong> - the image width pixels.
 * <li><strong>height</strong> - the image height in pixels.
 * </ul>
 */
@SuppressWarnings("unused")
public class OBThumbnail extends OBBaseEntity implements Serializable {
    private static final String URL = "url";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String IMAGE_TYPE_KEY = "imageImpressionType";
    private static final String IMAGE_TYPE_GIF = "DOCUMENT_ANIMATED_IMAGE";

    /**
     * @brief Get the image URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @brief Get the image width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @brief Get the image height in pixels.
     */
    public int getHeight() {
        return height;
    }

    public boolean isGif() {
        return isGif;
    }

    private String url;
    private int width;
    private int height;
    private boolean isGif;

    public OBThumbnail(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject == null) {
            return;
        }
        String imageUrl = jsonObject.optString(URL);
        if (isHttpUrl(imageUrl) || isHttpsUrl(imageUrl)) {
            url = imageUrl;
        }
        width = jsonObject.optInt(WIDTH);
        height = jsonObject.optInt(HEIGHT);
        if (IMAGE_TYPE_GIF.equals(jsonObject.optString(IMAGE_TYPE_KEY))) {
            isGif = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OBThumbnail thumbnail = (OBThumbnail) o;

        boolean imageUrlEquals;
        if (url != null) {
            imageUrlEquals = url.equals(thumbnail.url);
        }
        else {
            imageUrlEquals = (url == null) && (thumbnail.url == null);
        }

        return (width == thumbnail.width) && (height == thumbnail.height) && imageUrlEquals;

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    private static boolean isHttpUrl(String url) {
        return (null != url) &&
                (url.length() > 6) &&
                url.substring(0, 7).equalsIgnoreCase("http://");
    }

    /**
     * @return True iff the url is an https: url.
     */
    private static boolean isHttpsUrl(String url) {
        return (null != url) &&
                (url.length() > 7) &&
                url.substring(0, 8).equalsIgnoreCase("https://");
    }
}
