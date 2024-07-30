package com.outbrain.OBSDK.SmartFeed;

import java.util.ArrayList;

public interface OBSmartFeedServiceListener {

    void notifyNewItems(ArrayList<SFItemData> sfItemsList, boolean shouldUpdateUI);

    void notifyNoNewItemsToAdd();
}
