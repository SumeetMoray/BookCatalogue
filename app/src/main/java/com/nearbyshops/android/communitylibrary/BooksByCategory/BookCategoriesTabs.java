package com.nearbyshops.android.communitylibrary.BooksByCategory;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyBackPressed;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyCategoryChanged;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.FragmentsNotificationReceiver;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.NotifyPagerAdapter;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookCategoriesTabs extends AppCompatActivity implements FragmentsNotificationReceiver,NotifyPagerAdapter{



    private PagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;

    Unbinder unbinder;


    private NotifyBackPressed notificationReceiver;
    private NotifyCategoryChanged tabsNotificationReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_by_category);
        unbinder = ButterKnife.bind(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager());


        getSupportActionBar().setTitle("Book Categories");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

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
        }

        return super.onOptionsItemSelected(item);
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
    public void itemCategoryChanged(BookCategory currentCategory) {

        Log.d("applog","Item Category Changed : " + currentCategory.getBookCategoryName() + " : " + String.valueOf(currentCategory.getBookCategoryID()));


        getSupportActionBar().setTitle(currentCategory.getBookCategoryName());

        if(tabsNotificationReceiver!=null)
        {
            tabsNotificationReceiver.categoryChanged(currentCategory,false);
        }

    }

    @Override
    public void showAppBar() {

        appBar.setVisibility(View.VISIBLE);


//      mViewPager.setTranslationY(-appBar.getHeight());

    }

    @Override
    public void hideAppBar() {

//

        appBar.setVisibility(View.GONE);


//        appBar.setTranslationY(0);

//      mViewPager.setTranslationY(0);
    }

    @Override
    public void translationZ(int dy) {

        dy = -dy;

//        appBar.setTranslationY(appBar.getTranslationY() - dy);

        ViewGroup.LayoutParams params=mViewPager.getLayoutParams();


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


        }







        Log.d("appbar",String.valueOf(appBar.getTranslationY()));
        if(appBar.getTranslationY()==-appBar.getHeight())
        {
            appBar.setVisibility(View.GONE);
        }

        if(appBar.getTranslationY()==0)
        {
//            appBar.setVisibility(View.VISIBLE);
        }

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

        mViewPager.setCurrentItem(1);
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



    public interface ReceiveNotificationFromTabsForItems {

        void itemCategoryChanged(BookCategory currentCategory);
    }


    //        mSectionsPagerAdapter.setTitle("",tabPosition);

}

