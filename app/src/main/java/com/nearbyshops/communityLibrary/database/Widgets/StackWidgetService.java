package com.nearbyshops.communityLibrary.database.Widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by sumeet on 28/7/16.
 */

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}
