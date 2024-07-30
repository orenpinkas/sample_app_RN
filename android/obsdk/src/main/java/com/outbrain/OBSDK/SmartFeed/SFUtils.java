package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBSettings;
import com.outbrain.OBSDK.OBClickListener;
import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.Theme.SFThemeImpl;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainSingleItemViewHolder;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;


public class SFUtils {

    static HashMap<String, Boolean> imageLoadedOnceMap = new HashMap<>();
    public static boolean displaySourceOnOrganicRec = false;

    public static boolean isRtlText(String text) {
        if (text == null) {
            return false;
        }
        char[] chars = text.toCharArray();
        for(char c: chars){
            if(c >= 0x5D0 && c <= 0x6ff){
                return true;
            }
        }
        return false;
    }

    public static void setTextViewDirection(TextView tv, String text) {
        tv.setTextDirection(isRtlText(text) ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
    }

    public static void setStripThumbnailItemDirection(LinearLayout stripThumbnailLL, boolean isRtl) {
        if (stripThumbnailLL == null) {
            return;
        }
        stripThumbnailLL.setLayoutDirection(isRtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
    }

    public static void setItemTitleDirection(RelativeLayout titleRL, boolean isRtl) {
        TextView titleTV = titleRL.findViewById(R.id.ob_title_text_view);
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) titleTV.getLayoutParams();
        titleParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        titleParams.removeRule(isRtl ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        titleTV.setLayoutParams(titleParams);
    }

    public static void bindPaidLabelTV (TextView paidLabelTV, SFItemData sfItem, boolean isPaid){
        if (isPaid && !("").equals(sfItem.getPaidLabelText())) {
            paidLabelTV.setVisibility(View.VISIBLE);
            paidLabelTV.setText(sfItem.getPaidLabelText());
            if (!sfItem.isCustomUI()) {
                String paidLabelTextColor = sfItem.getPaidLabelTextColor();
                String paidLabelBackgroundColor = sfItem.getPaidLabelBackgroundColor();
                try {
                    paidLabelTV.setTextColor(Color.parseColor(("").equals(paidLabelTextColor) ? "#ffffff" : paidLabelTextColor));
                    // color is a valid color
                } catch (IllegalArgumentException iae) {
                    // This color string is not valid
                    paidLabelTV.setTextColor(Color.parseColor("#ffffff"));
                }
                try {
                    paidLabelTV.setBackgroundColor(Color.parseColor(("").equals(paidLabelBackgroundColor) ? "#666666" : paidLabelBackgroundColor));
                    // color is a valid color
                } catch (IllegalArgumentException iae) {
                    // This color string is not valid
                    paidLabelTV.setBackgroundColor(Color.parseColor("#666666"));
                }
            }
        } else {
            paidLabelTV.setVisibility(View.INVISIBLE);
        }
    }

    public static String generateCustomUIMapKeyForWidgetID(String widgetID, int viewType) {
        return String.format("widgetID_%s&viewType_%s", widgetID, viewType);
    }

    public static String generateCustomUIMapKeyForSFItemType(SFItemData.SFItemType itemType, int viewType){
        return String.format("itemType_%s&viewType_%s", itemType.ordinal(), viewType);
    }

    public static String getRecSourceText(OBRecommendation rec, OBSettings obSettings) {
        String sourceName = rec.getSourceName();
        if (rec.isPaid()) {
            if (!("".equals(obSettings.getPaidSourceFormat() ))) {
                if (obSettings.getPaidSourceFormat().contains("$SOURCE") && sourceName != null) {
                    return obSettings.getPaidSourceFormat().replace("$SOURCE", sourceName);
                }
                else {
                    return obSettings.getPaidSourceFormat();
                }
            }
        }
        else { // Organic rec
            if (!("".equals(obSettings.getOrganicSourceFormat() ))) {
                if (obSettings.getOrganicSourceFormat().contains("$SOURCE") && sourceName != null) {
                    return obSettings.getOrganicSourceFormat().replace("$SOURCE", sourceName);
                }
                else {
                    return obSettings.getOrganicSourceFormat();
                }
            }
        }
        return sourceName;
    }

    public static void onBindItemHeader(
            RelativeLayout headerRL,
            TextView titleTV,
            SFItemData sfItem,
            boolean isRTL) {

        if (sfItem.getTitle() == null) {
            headerRL.setVisibility(View.GONE);
            return;
        }
        titleTV.setText(sfItem.getTitle());

        String titleTextColor = sfItem.getTitleTextColor();
        int defaultWidgetTextColor = SFThemeImpl.getInstance().recTitleTextColor(true);
        int chosenWidgetTextColor = titleTextColor == null ? defaultWidgetTextColor : Color.parseColor(titleTextColor);
        titleTV.setTextColor(chosenWidgetTextColor);

        headerRL.setVisibility(View.VISIBLE);
        if (!sfItem.isCustomUI()) {
            SFUtils.setItemTitleDirection(headerRL, isRTL);
        }
    }

    private static int convertPxToDp(int px, Context ctx) {
        return Math.round(
                px * (ctx.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT)
        );
    }

    public static void resetImageLoadedMap() {
        imageLoadedOnceMap = new HashMap<>();
    }

    private static void setCardViewColors(CardView cardView) {
        cardView.setCardBackgroundColor(SFThemeImpl.getInstance().primaryColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            int shadowColor = SFThemeImpl.getInstance().cardShadowColor();
            cardView.setOutlineAmbientShadowColor(shadowColor);
            cardView.setOutlineSpotShadowColor(shadowColor);
        }
    }

    public static void onBindSingleRec(
            final OBClickListener obClickListener,
            final SFSingleRecView sfSingleRecView,
            final OBRecommendation rec,
            Context ctx,
            SFItemData sfItem) {

        final WeakReference<OBClickListener> recListenerReference = new WeakReference<>(obClickListener);
        sfSingleRecView.recTitleTV.setText(rec.getContent());

        if (sfSingleRecView.recSourceTV != null) {
            sfSingleRecView.recSourceTV.setText(SFUtils.getRecSourceText(rec, sfItem.getSettings()));
        }

        if (sfSingleRecView.cardView != null) {
            setCardViewColors(sfSingleRecView.cardView);
        }

        boolean isBrandedItem = sfItem.itemType() == SFItemData.SFItemType.BRANDED_CAROUSEL_ITEM || sfItem.itemType() == SFItemData.SFItemType.BRANDED_APP_INSTALL;
        boolean isWeeklyHighlightsItem = sfItem.itemType() == SFItemData.SFItemType.WEEKLY_UPDATE_ITEM;
        if (!sfItem.isCustomUI() && !isBrandedItem && !isWeeklyHighlightsItem) {
            if (sfSingleRecView.recSourceTV != null) {
                sfSingleRecView.recSourceTV.setVisibility(rec.isPaid() || displaySourceOnOrganicRec ? View.VISIBLE : View.GONE);
            }
            sfSingleRecView.recTitleTV.setTextColor(SFThemeImpl.getInstance().recTitleTextColor(rec.isPaid()));

            SFUtils.setTextViewDirection(sfSingleRecView.recTitleTV, rec.getContent());
            SFUtils.setTextViewDirection(sfSingleRecView.recSourceTV, rec.getSourceName());
        }
        else if (isBrandedItem) {
            sfSingleRecView.recTitleTV.setTextColor(SFThemeImpl.getInstance().recTitleTextColor(rec.isPaid()));
        }

        configureAbTestsIfExist(sfSingleRecView, rec, sfItem);

        sfSingleRecView.recWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recListenerReference.get() != null) {
                    recListenerReference.get().userTappedOnRecommendation(rec);
                }
            }
        });

        if (sfSingleRecView.disclosureImageView != null) {
            if (rec.isPaid() && rec.shouldDisplayDisclosureIcon()) {
                // Set the RTB disclosure icon image
                Picasso.get().load(rec.getDisclosure().getIconUrl()).into(sfSingleRecView.disclosureImageView);
                sfSingleRecView.disclosureImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recListenerReference.get() != null) {
                            final String url = rec.getDisclosure().getClickUrl();
                            recListenerReference.get().userTappedOnAdChoicesIcon(url);
                        }
                    }
                });
                sfSingleRecView.disclosureImageView.setVisibility(View.VISIBLE);
            } else {
                sfSingleRecView.disclosureImageView.setVisibility(View.GONE);
            }
        }
        if (sfSingleRecView.logoImageView != null) {
            if (rec.getLogo().getUrl() != null && !rec.isPaid()){
                if (!sfItem.isCustomUI()) {
                    sfSingleRecView.logoImageView.getLayoutParams().height = convertPxToDp(rec.getLogo().getHeight(), ctx);
                    sfSingleRecView.logoImageView.getLayoutParams().width = convertPxToDp(rec.getLogo().getWidth(), ctx);
                }
                sfSingleRecView.logoImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(rec.getLogo().getUrl()).into(sfSingleRecView.logoImageView);
            } else {
                sfSingleRecView.logoImageView.setVisibility(View.GONE);
            }
        }

        if (sfSingleRecView.paidLabelTV != null) {
            SFUtils.bindPaidLabelTV(
                    sfSingleRecView.paidLabelTV,
                    sfItem,
                    rec.isPaid()
            );
        }
        if (!sfItem.isCustomUI()) {
            LinearLayout stripThumbnailLL = sfSingleRecView.recWrapper.findViewById(R.id.ob_strip_thumbnail_linear_layout);
            SFUtils.setStripThumbnailItemDirection(stripThumbnailLL, SFUtils.isRtlText(rec.getContent()));
        }
        if (!sfItem.isCustomUI() && sfItem.itemType() == SFItemData.SFItemType.STRIP_THUMBNAIL_ITEM) {
            sfSingleRecView.seperatorLine.setVisibility(sfItem.isLastInWidget() ? View.GONE : View.VISIBLE);
        }
    }

    private static void configureAbTestsIfExist(SFSingleRecView sfSingleRecView, OBRecommendation rec, SFItemData sfItem) {
        final String[] recModeForSmallFont = new String[] {"sdk_sfd_2_columns","sdk_sfd_3_columns","sdk_sfd_thumbnails"};
        final boolean useSmallerFontSize = Arrays.asList(recModeForSmallFont).contains(sfItem.getSettings().getRecMode());
        final boolean shouldAnimate = sfItem.getSettings().getAbImageFadeAnimation();
        final int animationDuration = sfItem.getSettings().getAbImageFadeDuration();
        final int abTitleFontSize = sfItem.getSettings().getAbTitleFontSize();
        final int abSourceFontSize = sfItem.getSettings().getAbSourceFontSize();
        final int abTitleFontStyle = sfItem.getSettings().getAbTitleFontStyle();
        final String abSourceFontColor = sfItem.getSettings().getAbSourceFontColor();

        boolean isWeeklyHighlightsItem = sfItem.itemType() == SFItemData.SFItemType.WEEKLY_UPDATE_ITEM;
        // for custom ui or for weekly highlights item we don't want to manipulate the font size, color or style in any way
        if (sfItem.isCustomUI() || isWeeklyHighlightsItem) {
            loadImageWithSettings(sfSingleRecView.recImageView, rec, shouldAnimate, animationDuration);
            return;
        }

        sfSingleRecView.recTitleTV.setTypeface(null, abTitleFontStyle == 1 ? Typeface.BOLD : Typeface.NORMAL); // == 1 --> is bold from sdk optimization

        if (abTitleFontSize > 10 && abTitleFontSize < 20) {
            sfSingleRecView.recTitleTV.setTextSize(abTitleFontSize);
        }
        else {
            sfSingleRecView.recTitleTV.setTextSize(useSmallerFontSize ? 16 : 18);
        }

        if (sfSingleRecView.recSourceTV != null) {
            if (abSourceFontSize >= 10 && abSourceFontSize < 16) {
                sfSingleRecView.recSourceTV.setTextSize(abSourceFontSize);
            }
            else {
                sfSingleRecView.recSourceTV.setTextSize(useSmallerFontSize ? 12 : 14); //14sp is the default
            }

            if (abSourceFontColor != null) {
                try {
                    sfSingleRecView.recSourceTV.setTextColor(Color.parseColor(abSourceFontColor));
                }
                catch (IllegalArgumentException ex) {
                    sfSingleRecView.recSourceTV.setTextColor(SFThemeImpl.getInstance().recSourceTextColor());
                }
            }
            else {
                sfSingleRecView.recSourceTV.setTextColor(SFThemeImpl.getInstance().recSourceTextColor());
            }
        }

        loadImageWithSettings(sfSingleRecView.recImageView, rec, shouldAnimate, animationDuration);
    }

    private static void loadImageWithSettings(final ImageView recImageView, final OBRecommendation rec, boolean shouldAnimate, final int animationDuration) {
        if (rec.getThumbnail() == null) {
            Picasso.get().load(R.drawable.placeholder_image).into(recImageView);
            Log.e("OBSDK", "loadImageWithSettings - rec.getThumbnail() == null - for rec at pos: " + rec.getPosition() + " reqID: " + rec.getReqID());
            return;
        }
        if (!shouldAnimate) {
            Picasso.get()
                    .load(rec.getThumbnail().getUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(recImageView);
        }
        else {
            Picasso.get()
                    .load(rec.getThumbnail().getUrl())
                    .noFade()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(recImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (!imageLoadedOnceMap.containsKey(rec.getThumbnail().getUrl())) {
                                recImageView.setAlpha(0f);
                                recImageView.animate().setDuration(animationDuration).alpha(1f).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageLoadedOnceMap.put(rec.getThumbnail().getUrl(), true);
                                    }
                                }).start();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }

    public static void handleAndBindCtaViewOnSingleRec(OutbrainSingleItemViewHolder holder, String ctaText) {
        Context context = holder.cardView.getContext();

        RelativeLayout cardRelativeLayout = (RelativeLayout) holder.cardView.getChildAt(0);

        if (cardRelativeLayout.findViewById(R.id.ob_rec_cta_tv) != null) {
            // make sure the view is visible (for reuse view holder)
            ((TextView) holder.cardView.findViewById(R.id.ob_rec_cta_tv)).setVisibility(View.VISIBLE);
            ((TextView) holder.cardView.findViewById(R.id.ob_rec_cta_tv)).setText(ctaText);
            return; // View already added to layout
        }

        TextView recTitleTV = holder.recTitleTV;

        // Remove original title text view
        ((ViewManager) recTitleTV.getParent()).removeView(recTitleTV);

        // Add new linear layout to hold the title text view and the cta view
        LinearLayout newLinearLayout = new LinearLayout(holder.cardView.getContext());
        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams newLinearLayoutParams= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newLinearLayoutParams.addRule(RelativeLayout.BELOW, R.id.ob_rec_image_layout);
        newLinearLayout.setLayoutParams(newLinearLayoutParams);
        int id = ViewCompat.generateViewId();
        newLinearLayout.setId(id);

        // Set new  layout params to title text view
        ViewGroup.MarginLayoutParams currentTitleLayoutParams = (ViewGroup.MarginLayoutParams) recTitleTV.getLayoutParams();
        LinearLayout.LayoutParams titleNewLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        titleNewLayoutParams.setMargins(currentTitleLayoutParams.leftMargin, currentTitleLayoutParams.topMargin, currentTitleLayoutParams.rightMargin, currentTitleLayoutParams.bottomMargin);
        recTitleTV.setLayoutParams(titleNewLayoutParams);

        // Set CTA view
        TextView ctaView = new TextView(holder.cardView.getContext());
        ctaView.setId(R.id.ob_rec_cta_tv);
        LinearLayout.LayoutParams ctaViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ctaViewParams.setMargins(
                0,
                convertDpToPx(context, 6), // Top
                convertDpToPx(context, 8), // Right
                0
        );
        ctaView.setPadding(
                convertDpToPx(context, 5), // Left
                convertDpToPx(context, 3), // Top
                convertDpToPx(context, 5), // Right
                convertDpToPx(context, 3)); // Bottom
        ctaView.setText(ctaText);
        ctaView.setTextColor(Color.parseColor("#5295e3"));
        ctaView.setLayoutParams(ctaViewParams);
        ctaView.setBackgroundResource(R.drawable.rounded_blue_border);

        // Add views to the new linear layout
        newLinearLayout.addView(recTitleTV);
        newLinearLayout.addView(ctaView);

        // Add the linear layout to the card relative layout
        cardRelativeLayout.addView(newLinearLayout);

        // Update layout params for rec source text view
        RelativeLayout.LayoutParams recSourceParams= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recSourceParams.addRule(RelativeLayout.BELOW, id);
        ViewGroup.MarginLayoutParams recSourceCurrentParams = (ViewGroup.MarginLayoutParams) holder.recSourceTV.getLayoutParams();
        recSourceParams.setMargins(recSourceCurrentParams.leftMargin, recSourceCurrentParams.topMargin, recSourceCurrentParams.rightMargin, recSourceCurrentParams.bottomMargin);
        holder.recSourceTV.setLayoutParams(recSourceParams);
    }
    public static int convertDpToPx(Context ctx, int dp) {
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }
}
