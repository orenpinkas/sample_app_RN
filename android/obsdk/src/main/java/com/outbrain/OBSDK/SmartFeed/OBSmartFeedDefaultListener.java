package com.outbrain.OBSDK.SmartFeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Outbrain;

import java.lang.ref.WeakReference;

public class OBSmartFeedDefaultListener implements OBSmartFeedListener {

    private final WeakReference<Context> contextWeakReference;

    public OBSmartFeedDefaultListener(Context ctx) {
        this.contextWeakReference = new WeakReference<>(ctx.getApplicationContext());
    }

    @Override
    public void userTappedOnRecommendation(OBRecommendation rec) {
        String url = Outbrain.getUrl(rec);
        openUrl(url);
    }

    @Override
    public void userTappedOnAdChoicesIcon(String url) {
        openUrl(url);
    }

    @Override
    public void userTappedOnAboutOutbrain() {
        String url = Outbrain.getOutbrainAboutURL();
        openUrl(url);
    }

    @Override
    public void userTappedOnVideo(String url) {
        openUrl(url);
    }

    private void openUrl(String url) {
        Context ctx = this.contextWeakReference.get();
        if (ctx != null) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // fix for https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
            ctx.startActivity(i);
        }
        else {
            Log.e("OBSDK", "OBSmartFeedDefaultListener is missing the app context");
        }

    }
}
