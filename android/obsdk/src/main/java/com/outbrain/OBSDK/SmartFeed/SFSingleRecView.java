package com.outbrain.OBSDK.SmartFeed;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class SFSingleRecView {
    public final View recWrapper;
    public View seperatorLine = null;
    public final CardView cardView;
    public final ImageView recImageView;
    public final ImageView disclosureImageView;
    public final ImageView logoImageView;
    public final TextView recSourceTV;
    public final TextView recTitleTV;
    public final TextView paidLabelTV;

    public SFSingleRecView(CardView recWrapper, ImageView recImageView, ImageView disclosureImageView, TextView recSourceTV, TextView recTitleTV, ImageView logoImageView, TextView paidLabelTV) {
        this.recWrapper = recWrapper;
        this.cardView = recWrapper;
        this.recImageView = recImageView;
        this.disclosureImageView = disclosureImageView;
        this.recSourceTV = recSourceTV;
        this.recTitleTV = recTitleTV;
        this.logoImageView = logoImageView;
        this.paidLabelTV = paidLabelTV;
    }

    public SFSingleRecView(View recWrapper, CardView cardView, ImageView recImageView, ImageView disclosureImageView, TextView recSourceTV, TextView recTitleTV, ImageView logoImageView, TextView paidLabelTV, View seperatorLine) {
        this.recWrapper = recWrapper;
        this.cardView = cardView;
        this.recImageView = recImageView;
        this.disclosureImageView = disclosureImageView;
        this.recSourceTV = recSourceTV;
        this.recTitleTV = recTitleTV;
        this.logoImageView = logoImageView;
        this.paidLabelTV = paidLabelTV;
        this.seperatorLine = seperatorLine;
    }

    public SFSingleRecView(CardView recWrapper, ImageView recImageView, TextView recTitleTV) {
        this.recWrapper = recWrapper;
        this.cardView = recWrapper;
        this.recImageView = recImageView;
        this.disclosureImageView = null;
        this.recSourceTV = null;
        this.recTitleTV = recTitleTV;
        this.logoImageView = null;
        this.paidLabelTV = null;
    }

    public SFSingleRecView(View recWrapper, CardView cardView, ImageView recImageView, TextView recTitleTV) {
        this.recWrapper = recWrapper;
        this.cardView = cardView;
        this.recImageView = recImageView;
        this.disclosureImageView = null;
        this.recSourceTV = null;
        this.recTitleTV = recTitleTV;
        this.logoImageView = null;
        this.paidLabelTV = null;
    }
}