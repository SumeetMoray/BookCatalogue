package com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategoriesTabs;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyBackPressed;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.FragmentsNotificationReceiver;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.NotifyPagerAdapter;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookCategoryEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookCategoryService;
import com.nearbyshops.android.communitylibrary.SelectParent.BookCategoriesParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemCategoriesFragment extends Fragment
        implements BookCategoriesAdapter.ReceiveNotificationsFromAdapter, SwipeRefreshLayout.OnRefreshListener, NotifyBackPressed {



    ArrayList<BookCategory> dataset = new ArrayList<BookCategory>();

    RecyclerView itemCategoriesList;
    BookCategoriesAdapter listAdapter;

    GridLayoutManager layoutManager;



    int scrolly = 0;


    @State boolean show = true;

    @Inject
    BookCategoryService bookCategoryService;

//    @Bind(R.id.tablayout)
//    TabLayout tabLayout;

    @BindView(R.id.options)
    RelativeLayout options;

    @BindView(R.id.appbar)
    AppBarLayout appBar;


    FragmentsNotificationReceiver notificationReceiverFragment;
    NotifyPagerAdapter notifyPagerAdapter;



    int currentCategoryID = 1; // the ID of root category is always supposed to be 1


    @State
    BookCategory currentCategory = null;


    public ItemCategoriesFragment() {
        super();

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);

        currentCategory = new BookCategory();
        currentCategory.setBookCategoryName("Book Categories");
        currentCategory.setBookCategoryID(1);
        currentCategory.setParentCategoryID(-1);
    }


    int getMaxChildCount(int spanCount, int heightPixels)
    {
       return (spanCount * (heightPixels/250));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_book_categories, container, false);

        ButterKnife.bind(this,rootView);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemCategoriesList = (RecyclerView)rootView.findViewById(R.id.recyclerViewItemCategories);

        setupRecyclerView();
        setupSwipeContainer();



        if(getActivity() instanceof BookCategoriesTabs)
        {
            BookCategoriesTabs activity = (BookCategoriesTabs)getActivity();
            activity.setNotificationReceiver(this);
//            Log.d("applog","DetachedItemFragment: Fragment Recreated");
        }


        if(getActivity() instanceof FragmentsNotificationReceiver)
        {
            BookCategoriesTabs activity = (BookCategoriesTabs)getActivity();

            this.notificationReceiverFragment = (FragmentsNotificationReceiver) activity;
        }


        if(getActivity() instanceof NotifyPagerAdapter)
        {
            notifyPagerAdapter = (NotifyPagerAdapter)getActivity();
        }



        if(savedInstanceState==null)
        {
            // make request to the network only for the first time and not the second time or when the context is changed.

            // reset the offset before making request


            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);

                    try {


                        // make a network call
                        offset = 0;
                        dataset.clear();
                        makeRequestRetrofit(false);


                    } catch (IllegalArgumentException ex)
                    {
                        ex.printStackTrace();

                    }
                }
            });


        }


        return  rootView;

    }



    private int limit = 30;
    @State int offset = 0;

    @State int item_count = 0;



    void setupRecyclerView()
    {



        listAdapter = new BookCategoriesAdapter(dataset,getActivity(),this,this);

        itemCategoriesList.setAdapter(listAdapter);

        layoutManager = new GridLayoutManager(getActivity(),1, LinearLayoutManager.VERTICAL,false);
        itemCategoriesList.setLayoutManager(layoutManager);


        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position % 3 == 0 ? 2 : 1);
            }
        });
*/


        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        layoutManager.setSpanCount(metrics.widthPixels/450);

        itemCategoriesList.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

/*
                if(newState==RecyclerView.SCROLL_STATE_DRAGGING) {

                    if (options.getTranslationY() == 0) {

                        options.animate().translationY(options.getHeight());

                    } else
                    {
//                        options.setTranslationY(0);
                        options.animate().translationY(0);
                    }

                }*/


                if(layoutManager.findLastVisibleItemPosition()==dataset.size()-1)
                {
                    // trigger fetch next page

                    if((offset+limit)<=item_count)
                    {
                        offset = offset + limit;
                        makeRequestRetrofit(false);
                    }


                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                if(options.getY()<50)
//                {
//                    options.scrollBy(0,-dy);
//                    options.setY(options.getY() + dy);
//                }

//                options.setTranslationY(-dy);
//                options.setY(-dy);


//                options.setScrollY(-dy);




                /*if(dy > 0 && scrolly < 50)
                {
                    scrolly = scrolly + dy;

//                    options.scrollBy(0,dy);

                    Log.d("scroll",String.valueOf(scrolly));

                }

                if(dy < 0 && scrolly > 0)
                {
                    scrolly = scrolly + dy;

//                    options.scrollBy(0,-dy);

                    Log.d("scroll",String.valueOf(scrolly));
                }*/


                if(options.getY()<600 && options.getY()>500)
                {

                }

//                if(options.getY()<600 && dy>0)
//                {
//                    options.setY(options.getY() + dy);
//                }
//                else if (dy<0 && options.getY()>500)
//                {
//                    options.setY(options.getY() + dy);
//                }


                Log.d("scroll",String.valueOf(options.getTranslationY()));


//                Log.d("scroll",String.valueOf(dy));





//                notificationReceiverFragment.translationZ(dy);

/*

                if((options.getTranslationY()+dy) >= 0 && (options.getTranslationY() + dy) < options.getHeight())
                {
                    options.setTranslationY(options.getTranslationY() + dy);

                }
                else if((options.getTranslationY()+dy)<0)
                {
                    options.setTranslationY(0);

//                    notificationReceiverFragment.hideAppBar();
                }
                else if((options.getTranslationY()+dy)>options.getHeight())
                {
                    options.setTranslationY(options.getHeight());

//                    notificationReceiverFragment.showAppBar();
                }
*/




                if(dy < -20)
                {



                    boolean previous = show;

                    show = true ;

                    if(show!=previous)
                    {
                        // changed
                        Log.d("scrolllog","show");

//                        options.animate().translationX(metrics.widthPixels-10);
//                        options.animate().translationY(200);
//


//                        options.setVisibility(View.VISIBLE);
//                        notificationReceiverFragment.showAppBar();
                    }

                }else if(dy > 20)
                {

                    boolean previous = show;

                    show = false;



                    if(show!=previous)
                    {
                        // changed
//                        options.setVisibility(View.VISIBLE);
//                        options.animate().translationX(0);
                        Log.d("scrolllog","hide");

//                        options.animate().translationY(0);
//
//                        options.setVisibility(View.GONE);
//                        notificationReceiverFragment.hideAppBar();
                    }
                }


            }

        });

    }

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    void setupSwipeContainer()
    {

        if(swipeContainer!=null) {

            swipeContainer.setOnRefreshListener(this);
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

    }





    public void makeRequestRetrofit(final boolean notifyItemCategoryChanged)
    {

//        Call<List<ItemCategory>> itemCategoryCall2 = itemCategoryService
//                .getItemCategories(currentCategory.getItemCategoryID());


//        Call<List<ItemCategory>> itemCategoryCall = itemCategoryService.getItemCategories(
//                null,currentCategory.getItemCategoryID(),null,null,null,null,null,null,"id",limit,offset);

        Call<BookCategoryEndpoint> endPointCall = bookCategoryService.getBookCategories(
                currentCategory.getBookCategoryID(),null,limit,offset,false);






        Log.d("applog","DetachedTabs: Network call made !");



        endPointCall.enqueue(new Callback<BookCategoryEndpoint>() {

            @Override
            public void onResponse(Call<BookCategoryEndpoint> call, Response<BookCategoryEndpoint> response) {

                if(response.body()!=null)
                {
                    BookCategoryEndpoint endPoint = response.body();

                    item_count = endPoint.getItemCount();

                    dataset.addAll(endPoint.getResults());

                    Log.d("applog",String.valueOf(item_count) + " : " + endPoint.getResults().size());

                    listAdapter.notifyDataSetChanged();


                    if(notifyItemCategoryChanged)
                    {
                        if(currentCategory!=null)
                        {
                            notificationReceiverFragment.itemCategoryChanged(currentCategory);
                        }
                    }


                    if(notifyPagerAdapter!=null)
                    {
//                        notifyPagerAdapter.NotifyTitleChanged("Subcategories ( " +  String.valueOf(dataset.size())
//                                + " / " + item_count + " )",0);

                        notifyTitleChanged();
                    }

                }


                else
                {
                    Log.d("applog","body null" + " : " + response.errorBody().toString());

                }

                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<BookCategoryEndpoint> call, Throwable t) {


                showToastMessage("Network request failed. Please check your connection !");


                if(swipeContainer!=null)
                {
                    swipeContainer.setRefreshing(false);
                }
            }
        });

    }



    private void showToastMessage(String message)
    {
        if(getActivity()!=null)
        {
            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        }
    }



    void notifyDelete()
    {
        dataset.clear();
        offset = 0 ; // reset the offset
        makeRequestRetrofit(false);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//
//
//        swipeContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeContainer.setRefreshing(true);
//
//                try {
//
//                    makeRequestRetrofit();
//
//                } catch (IllegalArgumentException ex)
//                {
//                    ex.printStackTrace();
//
//                }
//
////                adapter.notifyDataSetChanged();
//            }
//        });
//    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                BookCategory parentCategory = data.getParcelableExtra("result");

                if(parentCategory!=null)
                {

                    listAdapter.getRequestedChangeParent().setParentCategoryID(parentCategory.getBookCategoryID());

                    makeUpdateRequest(listAdapter.getRequestedChangeParent());
                }
            }
        }

        if(requestCode == 2)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                BookCategory parentCategory = data.getParcelableExtra("result");

                if(parentCategory!=null)
                {

                    List<BookCategory> tempList = new ArrayList<>();

                    for(Map.Entry<Integer,BookCategory> entry : listAdapter.selectedItems.entrySet())
                    {
                        entry.getValue().setParentCategoryID(parentCategory.getBookCategoryID());
                        tempList.add(entry.getValue());
                    }

                    makeRequestBulk(tempList);
                }

            }
        }
    }



    void makeUpdateRequest(BookCategory bookCategory)
    {
        Call<ResponseBody> call = bookCategoryService.updateBookCategory(bookCategory,bookCategory.getBookCategoryID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200)
                {
                    showToastMessage("Change Parent Successful !");

                    dataset.clear();
                    offset = 0 ; // reset the offset
                    makeRequestRetrofit(false);

                }else
                {
                    showToastMessage("Change Parent Failed !");
                }

                listAdapter.setRequestedChangeParent(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showToastMessage("Network request failed. Please check your connection !");

                listAdapter.setRequestedChangeParent(null);
            }
        });
    }


    @OnClick(R.id.changeParentBulk)
    void changeParentBulk()
    {

        if(listAdapter.selectedItems.size()==0)
        {
            showToastMessage("No item selected. Please make a selection !");

            return;
        }

        // make an exclude list. Put selected items to an exclude list. This is done to preven a category to make itself or its
        // children its parent. This is logically incorrect and should not happen.

        BookCategoriesParent.clearExcludeList();
        BookCategoriesParent.excludeList.putAll(listAdapter.selectedItems);

        Intent intentParent = new Intent(getActivity(), BookCategoriesParent.class);
        startActivityForResult(intentParent,2,null);
    }



    void makeRequestBulk(final List<BookCategory> list)
    {
        Call<ResponseBody> call = bookCategoryService.updateBookCategoryBulk(list);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200)
                {
                    showToastMessage("Update Successful !");

                    clearSelectedItems();

                }else if (response.code() == 206)
                {
                    showToastMessage("Partially Updated. Check data changes !");

                    clearSelectedItems();

                }else if(response.code() == 304)
                {

                    showToastMessage("No item updated !");

                }else
                {
                    showToastMessage("Unknown server error or response !");
                }


                dataset.clear();
                offset = 0 ; // reset the offset
                makeRequestRetrofit(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                showToastMessage("Network Request failed. Check your internet / network connection !");

            }
        });

    }


    void clearSelectedItems()
    {
        // clear the selected items
        listAdapter.selectedItems.clear();
    }



    @Override
    public void notifyItemCategorySelected() {

        exitFullscreen();
    }


    void exitFullscreen()
    {


//                options.setVisibility(View.VISIBLE);
//                appBar.setVisibility(View.VISIBLE);
//                notificationReceiverFragment.showAppBar();


        options.setVisibility(View.VISIBLE);
        notificationReceiverFragment.showAppBar();

        if(show)
        {
            show= false;
        }else
        {
            show=true;
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    @OnClick(R.id.addItemCategory)
    void addItemCategoryClick()
    {
        Intent addIntent = new Intent(getActivity(), AddItemCategory.class);

        addIntent.putExtra(AddItemCategory.ADD_ITEM_CATEGORY_INTENT_KEY,currentCategory);

        startActivity(addIntent);
    }


    @Override
    public void onRefresh() {

        // reset the offset and make a network call
        offset = 0;

        dataset.clear();
        makeRequestRetrofit(false);
    }


//    private boolean isRootCategory = true;
//
    private ArrayList<String> categoryTree = new ArrayList<>();



    @Override
    public void notifyRequestSubCategory(BookCategory itemCategory) {

        BookCategory temp = currentCategory;

        currentCategory = itemCategory;

        currentCategoryID = itemCategory.getBookCategoryID();

        currentCategory.setParentCategory(temp);


        categoryTree.add(currentCategory.getBookCategoryName());

        if(notificationReceiverFragment!=null)
        {
            notificationReceiverFragment.insertTab(currentCategory.getBookCategoryName());
        }




//        if(isRootCategory) {
//
//            isRootCategory = false;
//
//        }else
//        {
//            boolean isFirst = true;
//        }





//        options.setVisibility(View.VISIBLE);
//        notificationReceiverFragment.showAppBar();
//        appBar.setVisibility(View.VISIBLE);



        dataset.clear();
        offset = 0 ; // reset the offset
        makeRequestRetrofit(true);

//        exitFullscreen();

//        notificationReceiverFragment.showAppBar();


        notificationReceiverFragment.notifySwipeToright();


    }


    @Override
    public boolean backPressed() {

        // clear the selected items when back button is pressed
        listAdapter.selectedItems.clear();

        if(currentCategory!=null) {

            if (categoryTree.size() > 0) {

                categoryTree.remove(categoryTree.size() - 1);

                if (notificationReceiverFragment != null) {
                    notificationReceiverFragment.removeLastTab();
                }
            }


            if (currentCategory.getParentCategory() != null) {

                currentCategory = currentCategory.getParentCategory();

                currentCategoryID = currentCategory.getBookCategoryID();

            } else {
                currentCategoryID = currentCategory.getParentCategoryID();
            }


            if (currentCategoryID != -1) {



//                options.setVisibility(View.VISIBLE);
//                appBar.setVisibility(View.VISIBLE);
//                notificationReceiverFragment.showAppBar();

//                exitFullscreen();

                dataset.clear();
                offset =0; // reset the offset
                makeRequestRetrofit(true);
            }

        }



        if(currentCategoryID == -1)
        {
//            super.onBackPressed();


            Log.d("backpressed","backPressedTrue");
            return  true;
        }else
        {
            Log.d("backpressed","backPressedFalse");
            return  false;
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        Icepick.saveInstanceState(this, outState);


        outState.putParcelableArrayList("dataset",dataset);
//        outState.putParcelable("currentCat",currentCategory);

    }



    void notifyTitleChanged()
    {
        if(notifyPagerAdapter!=null)
        {
            notifyPagerAdapter.NotifyTitleChanged("Subcategories (" +  String.valueOf(item_count) + ")",0);

//            notifyPagerAdapter.NotifyTitleChanged("Subcategories (" + String.valueOf(dataset.size()) + "/" + String.valueOf(item_count )+ ")",0);
        }

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState != null) {

            ArrayList<BookCategory> tempList = savedInstanceState.getParcelableArrayList("dataset");

            dataset.clear();
            dataset.addAll(tempList);

            notifyTitleChanged();

            listAdapter.notifyDataSetChanged();
//
//            currentCategory = savedInstanceState.getParcelable("currentCat");
        }

    }



}