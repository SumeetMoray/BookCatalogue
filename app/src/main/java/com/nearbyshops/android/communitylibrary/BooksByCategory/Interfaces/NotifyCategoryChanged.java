package com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces;

import com.nearbyshops.android.communitylibrary.Model.BookCategory;

/**
 * Created by sumeet on 4/7/16.
 */

public interface NotifyCategoryChanged {

    void categoryChanged(BookCategory currentCategory, boolean isBackPressed);

    void notifySwipeToRight();
}
