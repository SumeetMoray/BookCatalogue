package com.nearbyshops.android.communitylibrary.BooksByCategory;

import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyBackPressed;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyCategoryChanged;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyFABClick;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.FragmentsNotificationReceiver;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.NotifyPagerAdapter;
import com.nearbyshops.android.communitylibrary.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class BookCategoriesTabs extends AppCompatActivity
        implements
        FragmentsNotificationReceiver, NotifyPagerAdapter,
        ViewPager.OnPageChangeListener, SortFIlterBookDialog.NotifySort{


    FloatingActionMenu fab_menu;

    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;

    @BindView(R.id.fab_change_parent)
    FloatingActionButton fab_change_parent;

    private PagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;

    Unbinder unbinder;

    @BindView(R.id.example_button)
    TextView exampleButton;


    private NotifyBackPressed notificationReceiver;
    private NotifyCategoryChanged tabsNotificationReceiver;

    public NotifyFABClick notifyFABClick_bookCategory;
    public NotifyFABClick notifyFABClick_book;

    public SortFIlterBookDialog.NotifySort notifySort;


    @State int current_sort_by;
    @State boolean current_whether_descending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_by_category);
        unbinder = ButterKnife.bind(this);

        setDefaultSort();

        // assign background to the FAB's
        fab_add.setImageResource(R.drawable.fab_add);
        Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_low_priority_black_24px, getTheme());
        fab_change_parent.setImageDrawable(drawable);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        fab_menu = (FloatingActionMenu) findViewById(R.id.menu_red);

        getSupportActionBar().setTitle(R.string.title_activity_book_categories_tabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(this);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_books_by_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_sort)
        {

//            showToastMessage("Sort !");

            action_sort();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    void setDefaultSort()
    {
        current_sort_by = UtilityGeneral.getBookSortOptions(this).getSort_by();
        current_whether_descending = UtilityGeneral.getBookSortOptions(this).isWhether_descending();

    }



    void action_sort()
    {
        current_sort_by = UtilityGeneral.getBookSortOptions(this).getSort_by();
        current_whether_descending = UtilityGeneral.getBookSortOptions(this).isWhether_descending();

        FragmentManager fm = getSupportFragmentManager();
        SortFIlterBookDialog sortDialog = new SortFIlterBookDialog();
        sortDialog.setCurrentSort(current_sort_by,current_whether_descending);
        sortDialog.show(fm,"sort");
    }



    void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }


    public NotifyBackPressed getNotificationReceiver() {
        return notificationReceiver;
    }

    public void setNotificationReceiver(NotifyBackPressed notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    public NotifyCategoryChanged getTabsNotificationReceiver() {
        return tabsNotificationReceiver;
    }

    public void setTabsNotificationReceiver(NotifyCategoryChanged tabsNotificationReceiver) {
        this.tabsNotificationReceiver = tabsNotificationReceiver;
    }



    @BindView(R.id.appbar)
    AppBarLayout appBar;


    @Override
    public void itemCategoryChanged(BookCategory currentCategory, boolean backPressed) {

//        Log.d("applog","Item Category Changed : " + currentCategory.getBookCategoryName() + " : " + String.valueOf(currentCategory.getBookCategoryID()));


        getSupportActionBar().setTitle(currentCategory.getBookCategoryName());

        if(tabsNotificationReceiver!=null)
        {
            tabsNotificationReceiver.categoryChanged(currentCategory,backPressed);
        }

    }

    @Override
    public void showAppBar() {

//        appBar.setVisibility(View.VISIBLE);
        fab_menu.animate().translationY(0);


//      mViewPager.setTranslationY(-appBar.getHeight());

    }

    @Override
    public void hideAppBar() {

        fab_menu.animate().translationY(120);
//

//        appBar.setVisibility(View.GONE);


//        appBar.setTranslationY(0);

//      mViewPager.setTranslationY(0);
    }

    @Override
    public void translationZ(int dy) {

//        dy = -dy;

//        Log.d("translationz", String.valueOf(dy));


        /*if(exampleButton.getTranslationY()==0)
        {
            exampleButton.setTranslationY(exampleButton.getHeight());
        }
        else if(exampleButton.getTranslationY()==exampleButton.getHeight())
        {
            exampleButton.setTranslationY(0);
        }*/



        if((exampleButton.getTranslationY()+dy) >= 0 && (exampleButton.getTranslationY() + dy) < exampleButton.getHeight())
        {
            exampleButton.setTranslationY(exampleButton.getTranslationY() + dy);

        }
        else if((exampleButton.getTranslationY()+dy)<0)
        {
            exampleButton.setTranslationY(0);
        }
        else if((exampleButton.getTranslationY()+dy)>exampleButton.getHeight())
        {
            exampleButton.setTranslationY(exampleButton.getHeight());
        }

/*
        if(dy<0 )
        {
            fab_menu.setTranslationY(fab_menu.getTranslationY()+dy);
        }
        else if(dy>0)
        {

//            fab_menu.setTranslationY(fab_menu.getY());

            fab_menu.setTranslationY(fab_menu.getTranslationY()+dy);
        }*/


/*
        if(fab_menu.getTranslationY()<=0 && fab_menu.getTranslationY()>=fab_menu.getHeight())
        {
            fab_menu.setTranslationY(fab_menu.getTranslationY()+dy);
        }
*/

        if(dy>0)
        {
//            fab_menu.hideMenu(true);

        }else
        {
//            fab_menu.hideMenu(false);

//            fab_menu.showMenu(true);
        }



//        appBar.setTranslationY(appBar.getTranslationY() - dy);

   /*     ViewGroup.LayoutParams params=mViewPager.getLayoutParams();


        if((appBar.getTranslationY()+dy) < 0 && (appBar.getTranslationY() + dy) > -appBar.getHeight())
        {

            appBar.setTranslationY(appBar.getTranslationY() + dy);
//
//            params.height= params.height - (int)appBar.getTranslationY();
//            mViewPager.setLayoutParams(params);
//
//            mViewPager.setTranslationY(appBar.getTranslationY());


        }
        else if((appBar.getTranslationY()+dy)>0)
        {

            appBar.setVisibility(View.VISIBLE);
            appBar.setTranslationY(0);
        }
        else if((appBar.getTranslationY()+dy) < -appBar.getHeight())
        {

            appBar.setTranslationY(-appBar.getHeight());
//            mViewPager.setY(0);
//            mViewPager.setMinimumHeight(mViewPager.getHeight()+appBar.getHeight());

*/

//      }





/*

        Log.d("appbar",String.valueOf(appBar.getTranslationY()));
        if(appBar.getTranslationY()==-appBar.getHeight())
        {
            appBar.setVisibility(View.GONE);
        }

        if(appBar.getTranslationY()==0)
        {
//            appBar.setVisibility(View.VISIBLE);
        }*/

//        mViewPager.setY(appBar.getTranslationY()+dy);

    }


    @Override
    public void NotifyTitleChanged(String title, int tabPosition) {

        mSectionsPagerAdapter.setTitle(title,tabPosition);
    }


    @Override
    public void insertTab(String categoryName) {

        if(tabLayout==null)
        {
            return;
        }
/*
        if(tabLayout.getVisibility()==View.GONE)
        {
            tabLayout.setVisibility(View.VISIBLE);
        }

        tabLayout.addTab(tabLayout.newTab().setText("" + categoryName + " : : "));
        tabLayout.setScrollPosition(tabLayout.getTabCount()-1,0,true);*/
    }

    @Override
    public void removeLastTab() {

        if(tabLayout==null)
        {
            return;
        }
/*
        tabLayout.removeTabAt(tabLayout.getTabCount()-1);
        tabLayout.setScrollPosition(tabLayout.getTabCount()-1,0,true);

        if(tabLayout.getTabCount()==0)
        {
            tabLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void notifySwipeToright() {

        mViewPager.setCurrentItem(1,false);
    }

    @Override
    public void onBackPressed() {


        if(notificationReceiver!=null)
        {
            Log.d("backpressed","backpressedNOT-NULL");

            if(notificationReceiver.backPressed())
            {
                super.onBackPressed();
            }else
            {
                mViewPager.setCurrentItem(0,true);
            }
        }
        else
        {

            Log.d("backpressed","backpressedNull");

            super.onBackPressed();

        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    int currentPage = 0;

    @Override
    public void onPageSelected(int position) {

//        Log.d("applog","Page : " + String.valueOf(position));

        currentPage = position;



        if(position==0)
        {
            fab_add.setLabelText(getString(R.string.fab_label_add_book_category));
            fab_change_parent.setLabelText(getString(R.string.fab_label_change_parent_categories));

            fab_menu.setMenuButtonColorNormal(getResources().getColor(R.color.phonographyBlue));

        }else if(position == 1)
        {
            fab_add.setLabelText(getString(R.string.fab_label_add_book));
            fab_change_parent.setLabelText(getString(R.string.fab_label_change_parent_for_books));
            fab_menu.setMenuButtonColorNormal(getResources().getColor(R.color.orangeDark));

        }

    }




    @OnClick(R.id.fab_add)
    void fabClick_addButton()
    {
        if(currentPage==0)
        {
            if(notifyFABClick_bookCategory!=null)
            {
                notifyFABClick_bookCategory.add();
            }
        }
        else if(currentPage== 1)
        {
            if(notifyFABClick_book!=null)
            {
                notifyFABClick_book.add();
            }
        }
    }


    @OnClick(R.id.fab_change_parent)
    void fabClick_changeParentButton()
    {

        if(currentPage == 0)
        {
            if(notifyFABClick_bookCategory!=null)
            {
                notifyFABClick_bookCategory.changeParent();
            }
        }
        else if (currentPage == 1)
        {
            if(notifyFABClick_book!=null)
            {
                notifyFABClick_book.changeParent();
            }
        }
    }



    @Override
    public void onPageScrollStateChanged(int state) {

    }



    @Override
    public void applySort(int sortBy, boolean wheatherDescendingLocal) {

        if(notifySort!=null)
        {

//            showToastMessage("Applied Activity !");
            notifySort.applySort(sortBy,wheatherDescendingLocal);
            current_sort_by = sortBy;
            current_whether_descending = wheatherDescendingLocal;
        }
    }


    public interface ReceiveNotificationFromTabsForItems {

        void itemCategoryChanged(BookCategory currentCategory);
    }


    //        mSectionsPagerAdapter.setTitle("",tabPosition);


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this,outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Icepick.restoreInstanceState(this,savedInstanceState);
    }
}

