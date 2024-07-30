package com.outbrain.OBSDK.Utilities;

import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public final class OBAdvertiserIdFetcher {

  private static AdvertisingIdClient.Info adClientInfo;

  public static AdvertisingIdClient.Info getAdvertisingIdInfo(Context context) {
    AdvertisingIdClient.Info adInfo = null;
    try {
      adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);

    } catch (IOException e) {
      // Unrecoverable error connecting to Google Play services (e.g.,
      // the old version of the service doesn't support getting AdvertisingId).
    } catch (GooglePlayServicesRepairableException e) {
      // Encountered a recoverable error connecting to Google Play services.

    } catch (GooglePlayServicesNotAvailableException e) {
      // Google Play services is not available entirely.
    }
    adClientInfo = adInfo;
    return adInfo;
  }

  public static AdvertisingIdClient.Info getAdClientInfo() {
    return adClientInfo;
  }
}
