package com.outbrain.OBSDK.GDPRUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GDPRUtils {
    private static final String CCPA_STRING = "IABUSPrivacy_String";
    private static final String GDPR_V1_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String GDPR_V2_CONSENT_STRING = "IABTCF_TCString";
    private static final String CMP_PRESENT = "IABConsent_CMPPresent";
    private static final String EMPTY_DEFAULT_STRING = "";
    private static final String IABGPP_HDR_SectionsKey = "IABGPP_HDR_Sections";
    private static final String IABGPP_HDR_GppStringKey = "IABGPP_HDR_GppString";

    /**
     * Returns the websafe base64-encoded gdpr v1 consent String stored in the SharedPreferences
     *
     * @param context Context used to access the SharedPreferences
     * @return the stored websafe base64-encoded consent String
     */
    public static String getGdprV1ConsentString(Context context) {
        return getSharedPrefs(context).getString(GDPR_V1_CONSENT_STRING, EMPTY_DEFAULT_STRING);
    }

    /**
     * Returns the websafe base64-encoded gdpr v2 consent String stored in the SharedPreferences
     *
     * https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details
     * @param context Context used to access the SharedPreferences
     * @return the stored websafe base64-encoded consent String
     */
    public static String getGdprV2ConsentString(Context context) {
        return getSharedPrefs(context).getString(GDPR_V2_CONSENT_STRING, null);
    }

    /**
     * https://iabtechlab.com/wp-content/uploads/2019/10/CCPA_Compliance_Framework_US_Privacy_USER_SIGNAL_API_SPEC_IABTechLab_DRAFT_for_Public_Comment.pdf
     * Returns the iAB US Privacy String (CCPA) String stored in the SharedPreferences
     *
     * @param context Context used to access the SharedPreferences
     * @return the stored websafe base64-encoded consent String
     */
    public static String getCcpaString(Context context) {
        return getSharedPrefs(context).getString(CCPA_STRING, EMPTY_DEFAULT_STRING);
    }

    /**
     * Returns the CMP present boolean stored in the SharedPreferences
     *
     * @return {@code true} if a CMP implementing the iAB specification is present in the application, otherwise {@code false};
     */
    public static boolean getCmpPresentValue(Context context) {
        return getSharedPrefs(context).getBoolean(CMP_PRESENT, false);
    }

    public static String getIABGPP_HDR_SectionsKey(Context context) {
        return getSharedPrefs(context).getString(IABGPP_HDR_SectionsKey, EMPTY_DEFAULT_STRING);
    }

    public static String getIABGPP_HDR_GppStringKey(Context context) {
        return getSharedPrefs(context).getString(IABGPP_HDR_GppStringKey, EMPTY_DEFAULT_STRING);
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }
}