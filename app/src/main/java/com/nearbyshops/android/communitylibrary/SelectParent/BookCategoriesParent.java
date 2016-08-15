package com.nearbyshops.android.communitylibrary.SelectParent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookCategoryEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookCategoriesParent extends AppCompatActivity
        implements  BookCategoriesParentAdapter.requestSubCategory, BookCategoriesParentAdapter.NotificationReceiver{


    // data
    public static Map<Integer,BookCategory> excludeList = new HashMap<>();
    ArrayList<BookCategory> dataset = new ArrayList<>();

    @State boolean menuVisible = true;
    @State boolean instructionsVisible = false;

    @Inject
    BookCategoryService bookCategoryService;

    @State
    BookCategory currentCategory = null;


//    int currentCategoryID = 1; // the ID of root category is always supposed to be 1



//    private boolean isRootCategory = true;
//    private ArrayList<String> categoryTree = new ArrayList<>();




    // views
    RecyclerView itemCategoriesList;
    BookCategoriesParentAdapter listAdapter;
    GridLayoutManager layoutManager;

    @BindView(R.id.show_hide_instructions)
    TextView showHideInstructions;

    @BindView(R.id.usage_instructions)
    TextView usageInstructions;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.assign_parent)
    TextView assignParent;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;



    // for scrolling
    private int limit = 10;
    @State int offset = 0;
    @State int item_count = 0;

    Unbinder unbinder ;


    public BookCategoriesParent() {
        super();

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);

        currentCategory = new BookCategory();
        currentCategory.setBookCategoryID(1);
        currentCategory.setParentCategoryID(-1);
        currentCategory.setBookCategoryName("ROOT");
//        currentCategory.setBookCaDescription("The root category.");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categories_parent);

        unbinder = ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        itemCategoriesList = (RecyclerView) findViewById(R.id.recyclerViewItemCategories);
        setupRecyclerView();


        if(savedInstanceState==null)
        {
            makeRequestRetrofit();
        }
    }




    @OnClick(R.id.show_hide_instructions)
    void clickShowHideInstructions()
    {
        if(instructionsVisible)
        {
            usageInstructions.setVisibility(View.GONE);

            instructionsVisible = false;

        }else
        {
            usageInstructions.setVisibility(View.VISIBLE);

            instructionsVisible = true;
        }

    }


    void setupRecyclerView()
    {
        listAdapter = new BookCategoriesParentAdapter(dataset,this,this,this);

        itemCategoriesList.setAdapter(listAdapter);

        layoutManager = new GridLayoutManager(this,1);

        itemCategoriesList.setLayoutManager(layoutManager);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        layoutManager.setSpanCount(metrics.widthPixels/350);

        itemCategoriesList.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(layoutManager.findLastVisibleItemPosition()==dataset.size()-1)
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if(dy < -20)
                {

                    boolean previous = menuVisible;

                    menuVisible = true;

                    if(menuVisible !=previous)
                    {
                        // changed
//                        options.setVisibility(View.INVISIBLE);
                        Log.d("scrolllog","show");
//                        options.animate().translationX(metrics.widthPixels-10);

                        appBarLayout.setVisibility(View.VISIBLE);
                        assignParent.setVisibility(View.VISIBLE);



                    }

                }else if(dy > 20)
                {

                    boolean previous = menuVisible;

                    menuVisible = false;



                    if(menuVisible !=previous)
                    {
                        // changed
//                        options.setVisibility(View.VISIBLE);
//                        options.animate().translationX(0);
                        Log.d("scrolllog","hide");


                        appBarLayout.setVisibility(View.GONE);
                        assignParent.setVisibility(View.GONE);
                    }
                }

            }
        });

    }







    public void makeRequestRetrofit()
    {

//        Call<List<ItemCategory>> itemCategoryCall = itemCategoryService
//                .getItemCategories(currentCategory.getItemCategoryID());


        Call<BookCategoryEndpoint> endPointCall = bookCategoryService.getBookCategories(
                currentCategory.getBookCategoryID(),null,limit,offset,null);


        endPointCall.enqueue(new Callback<BookCategoryEndpoint>() {
            @Override
            public void onResponse(Call<BookCategoryEndpoint> call, Response<BookCategoryEndpoint> response) {

                if(response.body()!=null)
                {
                    if(currentCategory.getBookCategoryID()==1 && offset == 0)
                    {
                        dataset.add(0,currentCategory);
                    }


                    item_count = response.body().getItemCount();

                    // the entities in the exclude list should not be added into the dataset
                    for(BookCategory itemCategory : response.body().getResults())
                    {
                        // if item does not exist in the exclude list then only add it.
                        if(!excludeList.containsKey(itemCategory.getBookCategoryID()))
                        {
                            dataset.add(itemCategory);
                        }
                    }

                }

                listAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<BookCategoryEndpoint> call, Throwable t) {

                showToastMessage("Network request failed. Please check your connection !");
            }
        });

    }



    private void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onResume() {

        super.onResume();

//        makeRequestRetrofit();

        Log.d("applog","curent category ID " + currentCategory.getBookCategoryID());
    }





    private void insertTab(String categoryName)
    {
        if(tabLayout.getVisibility()==View.GONE)
        {
            tabLayout.setVisibility(View.VISIBLE);
        }

        tabLayout.addTab(tabLayout.newTab().setText(" : : " + categoryName + " : : "));
        tabLayout.setScrollPosition(tabLayout.getTabCount()-1,0,true);

    }

    private void removeLastTab()
    {
        if(tabLayout.getTabCount()>0)
        {
            tabLayout.removeTabAt(tabLayout.getTabCount()-1);
            tabLayout.setScrollPosition(tabLayout.getTabCount()-1,0,true);
        }


        if(tabLayout.getTabCount()==0)
        {
            tabLayout.setVisibility(View.GONE);
        }
    }





    @Override
    public void notifyRequestSubCategory(BookCategory itemCategory) {


        if(itemCategory.getBookCategoryID()==1)
        {
            return;
        }

        BookCategory temp = currentCategory;
        currentCategory = itemCategory;
        currentCategory.setParentCategory(temp);

//        categoryTree.add(currentCategory.getCategoryName());

        insertTab(currentCategory.getBookCategoryName());

//        currentCategoryID = itemCategory.getItemCategoryID();

//        if(isRootCategory) {
//
//            isRootCategory = false;
//
//        }else
//        {
//            boolean isFirst = true;
//        }

        offset = 0; // reset the offset
        dataset.clear();
        makeRequestRetrofit();


        appBarLayout.setVisibility(View.VISIBLE);
        assignParent.setVisibility(View.VISIBLE);

    }


    @Override
    public void onBackPressed() {


        Integer currentCategoryID = null;

        if(currentCategory!=null)
        {

//            if(categoryTree.size()>0) {
//
//                categoryTree.remove(categoryTree.size() - 1);
//
//            }

            removeLastTab();

            if(currentCategory.getParentCategory()!= null) {

                currentCategory = currentCategory.getParentCategory();
                currentCategoryID = currentCategory.getBookCategoryID();
            }
            else
            {
                currentCategoryID = currentCategory.getParentCategoryID();
            }


            if(currentCategoryID!=-1)
            {


                dataset.clear();
                offset = 0; // reset the offset
                makeRequestRetrofit();



                appBarLayout.setVisibility(View.VISIBLE);
                assignParent.setVisibility(View.VISIBLE);

                listAdapter.clearSelection();
            }
        }

        if(currentCategoryID == -1)
        {
            super.onBackPressed();
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }

    @Override
    public void notifyItemSelected() {

        assignParent.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyItemDeleted() {

        offset = 0; // reset the offset
        dataset.clear();
        makeRequestRetrofit();
    }




    @OnClick(R.id.assign_parent)
    void assignParentClick()
    {
        if(listAdapter.getSelection()==null)
        {
            showToastMessage("No item selected. Please make a selection !");
            return;
        }


        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",listAdapter.getSelection());
        setResult(Activity.RESULT_OK,returnIntent);

        // reset the static variable to null so that the data could be garbage collected.
        clearExcludeList();

        finish();

    }




    static public void clearExcludeList()
    {
        BookCategoriesParent.excludeList.clear();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
        outState.putParcelableArrayList("dataset",dataset);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        if (savedInstanceState != null) {

            Icepick.restoreInstanceState(this, savedInstanceState);

            ArrayList<BookCategory> tempList = savedInstanceState.getParcelableArrayList("dataset");

            dataset.clear();
            dataset.addAll(tempList);

            listAdapter.notifyDataSetChanged();
        }


    }


}
