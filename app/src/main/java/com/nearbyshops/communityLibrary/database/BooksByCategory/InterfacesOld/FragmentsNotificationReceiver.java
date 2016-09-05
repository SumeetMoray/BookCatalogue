package com.nearbyshops.communityLibrary.database.BooksByCategory.InterfacesOld;

import com.nearbyshops.communityLibrary.database.Model.BookCategory;

/**
 * Created by sumeet on 27/6/16.
 */
public interface FragmentsNotificationReceiver {


        void itemCategoryChanged(BookCategory currentCategory,boolean backPressed);

        void showAppBar();

        void hideAppBar();

        void translationZ(int dy);



        void insertTab(String categoryName);

        void removeLastTab();

        void notifySwipeToright();
}
