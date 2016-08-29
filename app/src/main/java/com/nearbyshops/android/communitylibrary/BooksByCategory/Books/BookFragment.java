package com.nearbyshops.android.communitylibrary.BooksByCategory.Books;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategoriesTabs;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyCategoryChanged;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Interfaces.NotifyFABClick;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.FragmentsNotificationReceiver;
import com.nearbyshops.android.communitylibrary.BooksByCategory.InterfacesOld.NotifyPagerAdapter;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookService;
import com.nearbyshops.android.communitylibrary.SelectParent.BookCategoriesParent;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookFragment extends Fragment
        implements  BookAdapter.NotificationReceiver, SwipeRefreshLayout.OnRefreshListener, NotifyCategoryChanged, NotifyFABClick , SortFIlterBookDialog.NotifySort{

    public static final String ADD_ITEM_INTENT_KEY = "add_item_intent_key";

    ArrayList<Book> dataset = new ArrayList<>();
    RecyclerView itemCategoriesList;
    BookAdapter listAdapter;

    GridLayoutManager layoutManager;

//    @Inject
//    ItemCategoryDataRouter dataRouter;


    @State boolean show = true;
//    boolean isDragged = false;

    @Inject
    BookService itemService;

//    @Bind(R.id.tablayout)
//    TabLayout tabLayout;


//    @BindView(R.id.appbar)
//    AppBarLayout appBar;


    FragmentsNotificationReceiver notificationReceiverFragment;

    NotifyPagerAdapter notifyPagerAdapter;


    @State int sort_by = SortFIlterBookDialog.SORT_BY_RATING;
    @State boolean whether_descending = false;




    @State
    BookCategory notifiedCurrentCategory = null;


    // scroll variables
    private int limit = 30;
    @State int offset = 0;
    @State int item_count = 0;



    public BookFragment() {
        super();

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);

    }

/*
    int getMaxChildCount(int spanCount, int heightPixels)
    {
       return (spanCount * (heightPixels/250));
    }*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_book, container, false);

        ButterKnife.bind(this,rootView);

        itemCategoriesList = (RecyclerView)rootView.findViewById(R.id.recyclerViewItemCategories);

        setupRecyclerView();
        setupSwipeContainer();



        if(getActivity() instanceof BookCategoriesTabs)
        {
            BookCategoriesTabs activity = (BookCategoriesTabs)getActivity();
            activity.setTabsNotificationReceiver(this);
            activity.notifyFABClick_book = this;
            activity.notifySort = this;
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

             swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);

                        // set sort options before making the first request
                        sort_by = UtilityGeneral.getBookSortOptions(getActivity()).getSort_by();
                        whether_descending = UtilityGeneral.getBookSortOptions(getActivity()).isWhether_descending();

                        dataset.clear();
                        makeRequestRetrofit();
                }
            });

        }




        return  rootView;
    }



    void setupRecyclerView()
    {


        listAdapter = new BookAdapter(dataset,getActivity(),this,this);

        itemCategoriesList.setAdapter(listAdapter);

        layoutManager = new GridLayoutManager(getActivity(),1);
        itemCategoriesList.setLayoutManager(layoutManager);



        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        layoutManager.setSpanCount(metrics.widthPixels/200);


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

                if(layoutManager.findLastVisibleItemPosition() == dataset.size()-1)
                {
                    // trigger fetch next page

                    if((offset+limit)<=item_count)
                    {
                        offset = offset + limit;
                        makeRequestRetrofit();
                    }

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);



/*

                if((options.getTranslationY()+dy) >= 0 && (options.getTranslationY() + dy) < options.getHeight())
                {
                    options.setTranslationY(options.getTranslationY() + dy);
                }
                else if((options.getTranslationY()+dy)<0)
                {
                    options.setTranslationY(0);
                }
                else if((options.getTranslationY()+dy)>options.getHeight())
                {
                    options.setTranslationY(options.getHeight());

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

//                        options.setVisibility(View.VISIBLE);
                        notificationReceiverFragment.showAppBar();
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

//                        options.setVisibility(View.GONE);
                        notificationReceiverFragment.hideAppBar();
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





    public void makeRequestRetrofit()
    {

        /*
        if(notifiedCurrentCategory==null)
        {

            swipeContainer.setRefreshing(false);

            return;
        }*/


        int bookCategoryID = 1;

        if(notifiedCurrentCategory!=null)
        {
            bookCategoryID = notifiedCurrentCategory.getBookCategoryID();
        }





        String sort_string = "";

        if(sort_by == SortFIlterBookDialog.SORT_BY_RATING)
        {
            sort_string = "avg_rating";
        }
        else if(sort_by == SortFIlterBookDialog.SORT_BY_RELEASE_DATE)
        {
            sort_string = "DATE_OF_PUBLISH";
        }
        else if(sort_by == SortFIlterBookDialog.SORT_BY_TITLE)
        {
            sort_string = "BOOK_NAME";
        }


        if(whether_descending)
        {
            sort_string = sort_string + " " + "desc NULLS LAST";
        }


//        showToastMessage(sort_string);


        Call<BookEndpoint> endPointCall = itemService.getBooks(
                bookCategoryID,sort_string,
                limit,offset, null);

        endPointCall.enqueue(new Callback<BookEndpoint>() {
            @Override
            public void onResponse(Call<BookEndpoint> call, Response<BookEndpoint> response) {

                if(isFragmentStopped)
                {
                    return;
                }

                item_count = response.body().getItemCount();

                if(response.body()!=null) {

                    dataset.addAll(response.body().getResults());
                }

                swipeContainer.setRefreshing(false);
                listAdapter.notifyDataSetChanged();

                if(notifyPagerAdapter!=null)
                {
//                    notifyPagerAdapter.NotifyTitleChanged("Items (" + String.valueOf(item_count) + ")",1);
                    notifyTitleChanged();
                }

            }

            @Override
            public void onFailure(Call<BookEndpoint> call, Throwable t) {

                if(isFragmentStopped)
                {
                    return;
                }


                showToastMessage(getString(R.string.network_not_available));
                swipeContainer.setRefreshing(false);

            }
        });

    }


    boolean isFragmentStopped = false;


    @Override
    public void onStop() {
        super.onStop();

        isFragmentStopped = true;
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
//        dataset.clear();
//        offset = 0; // reset the offset
//        makeRequestRetrofit();

        onRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();

        // reset the flag
        isFragmentStopped = false;


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
//            }
//        });


        notificationReceiverFragment.showAppBar();



    }


//    private boolean isRootCategory = true;
//
//    private ArrayList<String> categoryTree = new ArrayList<>();




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

//                    listAdapter.getRequestedChangeParent().setParentCategoryID(parentCategory.getItemCategoryID());

                    listAdapter.getRequestedChangeParent().setBookCategoryID(parentCategory.getBookCategoryID());

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

                    /*if(parentCategory.getAbstractNode())
                    {
                        showToastMessage(parentCategory.getCategoryName()
                                + " is an abstract category you cannot add item to an abstract category");

                        return;
                    }
*/
                    List<Book> tempList = new ArrayList<>();

                    for(Map.Entry<Integer,Book> entry : listAdapter.selectedItems.entrySet())
                    {
                        entry.getValue().setBookCategoryID(parentCategory.getBookCategoryID());
                        tempList.add(entry.getValue());
                    }

                    makeRequestBulk(tempList);
                }

            }
        }
    }



    void makeUpdateRequest(Book book)
    {

//        Call<ResponseBody> call2 = itemCategoryService.updateItemCategory(itemCategory,itemCategory.getItemCategoryID());

        Call<ResponseBody> call = itemService.updateBook(book,book.getBookID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200)
                {
                    showToastMessage(getString(R.string.change_parent_successful));

//                    dataset.clear();
//                    offset = 0 ; // reset the offset
//                    makeRequestRetrofit();

                    onRefresh();

                }else
                {
                    showToastMessage(getString(R.string.change_parent_failed));
                }

                listAdapter.setRequestedChangeParent(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));

                listAdapter.setRequestedChangeParent(null);

            }
        });

    }


    void changeParentBulk()
    {

        if(listAdapter.selectedItems.size()==0)
        {
            showToastMessage(getString(R.string.change_parent_no_item_selected));

            return;
        }

        // make an exclude list. Put selected items to an exclude list. This is done to preven a category to make itself or its
        // children its parent. This is logically incorrect and should not happen.

        BookCategoriesParent.clearExcludeList();
//        ItemCategoriesParent.excludeList.putAll(listAdapter.selectedItems);

        Intent intentParent = new Intent(getActivity(), BookCategoriesParent.class);
        startActivityForResult(intentParent,2,null);
    }


    void makeRequestBulk(final List<Book> list)
    {
//        Call<ResponseBody> call = itemService.updateItemCategoryBulk(list);


        Call<ResponseBody> call = itemService.updateBookBulk(list);

//        Call<ResponseBody> call = null;
//
//
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200)
                {
                    showToastMessage(getString(R.string.udate_successful_api_response));

                    clearSelectedItems();

                }else if (response.code() == 206)
                {
                    showToastMessage(getString(R.string.api_response_partially_updated));

                    clearSelectedItems();

                }else if(response.code() == 304)
                {

                    showToastMessage(getString(R.string.api_response_no_item_updated));

                }else
                {
                    showToastMessage(getString(R.string.api_response_unknown_server_error));
                }



//                makeRequestRetrofit();

                onRefresh();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                showToastMessage(getString(R.string.network_not_available));

            }
        });

    }


    void clearSelectedItems()
    {
        // clear the selected items
        listAdapter.selectedItems.clear();
    }


    void exitFullScreen()
    {
//        options.setVisibility(View.VISIBLE);
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
    public void notifyItemCategorySelected() {

        exitFullScreen();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();


    }



    void addBookClick()
    {
        Intent addIntent = new Intent(getActivity(), AddBook.class);

        addIntent.putExtra(ADD_ITEM_INTENT_KEY,notifiedCurrentCategory);

        startActivity(addIntent);
    }


    @Override
    public void onRefresh() {

        dataset.clear();
        offset = 0 ; // reset the offset
        listAdapter.notifyDataSetChanged();

        makeRequestRetrofit();

        Log.d("applog","refreshed");
    }


    void onRefreshSwipeIndicator()
    {

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);

                onRefresh();
            }
        });
    }




    public void itemCategoryChanged(BookCategory currentCategory) {

//        dataset.clear();
//        makeRequestRetrofit();


        notifiedCurrentCategory = currentCategory;
        onRefresh();
    }



    void notifyTitleChanged()
    {
        if(notifyPagerAdapter!=null)
        {
            notifyPagerAdapter.NotifyTitleChanged("Books (" + String.valueOf(dataset.size()) + "/" + String.valueOf(item_count) + ")",1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this,outState);

        outState.putParcelable("currentCategory",notifiedCurrentCategory);

        outState.putParcelableArrayList("dataset",dataset);

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Icepick.restoreInstanceState(this,savedInstanceState);

        if(savedInstanceState!=null)
        {
            notifiedCurrentCategory = savedInstanceState.getParcelable("currentCategory");

            ArrayList<Book> tempCat = savedInstanceState.getParcelableArrayList("dataset");

            dataset.clear();
            dataset.addAll(tempCat);

            notifyTitleChanged();

            listAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void categoryChanged(BookCategory currentCategory, boolean isBackPressed) {

        notifiedCurrentCategory = currentCategory;
        onRefresh();

        if(!isBackPressed)
        {
            notificationReceiverFragment.notifySwipeToright();
        }
    }

    @Override
    public void notifySwipeToRight() {

    }

    @Override
    public void add() {

        addBookClick();
    }

    @Override
    public void changeParent() {

        changeParentBulk();
    }

    @Override
    public void applySort(int sortBy, boolean whetherDescendingLocal) {

//        showToastMessage("Applied Fragment !");
        sort_by = sortBy;
        whether_descending = whetherDescendingLocal;

        UtilityGeneral.saveSortBooks(sortBy,whetherDescendingLocal);
        onRefreshSwipeIndicator();
    }
}
