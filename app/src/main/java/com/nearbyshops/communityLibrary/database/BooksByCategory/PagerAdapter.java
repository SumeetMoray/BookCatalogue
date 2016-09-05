package com.nearbyshops.communityLibrary.database.BooksByCategory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nearbyshops.communityLibrary.database.BooksByCategory.BookCategories.BookCategoriesFragment;
import com.nearbyshops.communityLibrary.database.BooksByCategory.Books.BookFragment;

/**
 * Created by sumeet on 9/8/16.
 */

public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

    private BookCategoriesFragment bookCategoriesFragment;
    BookFragment bookFragment;

    @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


        if(position == 0)
            {
                bookCategoriesFragment = new BookCategoriesFragment();

//            activity.setNotificationReceiver(bookCategoriesFragment);

                return bookCategoriesFragment;
            }
            else if (position == 1)
            {

//                bookFragment = new BookFragment();

//                return bookFragment;

                bookFragment = new BookFragment();

                return bookFragment;

            }



            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {


                case 0:
                    return titleCategories;
                case 1:
                    return titleItems;
                case 2:
                    return titleDetachedItemCategories;
                case 3:
                    return titleDetachedItems;

            }
            return null;
        }



    private String titleCategories = "Categories (0)";
    private String titleItems = "Books (0)";
    private String titleDetachedItemCategories = "Detached Item-Categories (0/0)";
    private String titleDetachedItems = "Detached Items (0/0)";


    public void setTitle(String title, int tabPosition)
    {
        if(tabPosition == 0){

            titleCategories = title;
        }
        else if (tabPosition == 1)
        {

            titleItems = title;

        }else if(tabPosition == 2)
        {
            titleDetachedItemCategories = title;

        }else if(tabPosition == 3)
        {
            titleDetachedItems = title;
        }


        notifyDataSetChanged();
    }

}
