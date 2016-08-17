package com.nearbyshops.android.communitylibrary.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BooksByCategory.PlaceholderFragment;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.MemberService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

/**
 * Created by sumeet on 12/8/16.
 */

public class SortFIlterBookDialog extends DialogFragment implements View.OnClickListener {


    ImageView dismiss_dialog_button;


    @BindView(R.id.sort_by_title)
    TextView sortByTitle;

    @BindView(R.id.sort_by_rating)
    TextView sortByRating;

    @BindView(R.id.sort_by_release_date)
    TextView sortByReleaseDate;

    @BindView(R.id.sort_descending)
    TextView sortDescending;

    @BindView(R.id.sort_ascending)
    TextView sortAscending;


    @Inject
    MemberService memberService;


    public final static int SORT_BY_TITLE = 1;
    public final static int SORT_BY_RATING = 2;
    public final static int SORT_BY_RELEASE_DATE = 3;

    @State int sort_by = SORT_BY_RATING;
    @State boolean whetherDescending = false;



    Unbinder unbinder;


    public SortFIlterBookDialog() {
        super();

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_sort_filter_book, container);

        unbinder = ButterKnife.bind(this,view);
        applyCurrentSort();


        dismiss_dialog_button = (ImageView) view.findViewById(R.id.dialog_dismiss_icon);

        dismiss_dialog_button.setOnClickListener(this);

        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.dialog_dismiss_icon:

                dismiss();
                Toast.makeText(getActivity(),"Dismissed !",Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }

    }


    @OnClick(R.id.sort_by_title)
    void sortByTitle_click()
    {

        sort_by = SORT_BY_TITLE;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByTitle.setTextColor(getActivity().getResources().getColor(R.color.white,null));
            sortByTitle.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByTitle.setTextColor(getActivity().getResources().getColor(R.color.white));
            sortByTitle.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha));
        }

    }

    @OnClick(R.id.sort_by_rating)
    void sortByRating_click()
    {
        sort_by = SORT_BY_RATING;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByRating.setTextColor(getActivity().getResources().getColor(R.color.white,null));
            sortByRating.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByRating.setTextColor(getActivity().getResources().getColor(R.color.white));
            sortByRating.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha));
        }
    }



    @OnClick(R.id.sort_by_release_date)
    void sortByReleaseDate_click()
    {

        sort_by = SORT_BY_RELEASE_DATE;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByReleaseDate.setTextColor(getActivity().getResources().getColor(R.color.white,null));
            sortByReleaseDate.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByReleaseDate.setTextColor(getActivity().getResources().getColor(R.color.white));
            sortByReleaseDate.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha));
        }
    }


    @OnClick(R.id.sort_ascending)
    void sortByAscending_Click()
    {

        whetherDescending = false;

        clearAscending();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortAscending.setTextColor(getActivity().getResources().getColor(R.color.white,null));
            sortAscending.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortAscending.setTextColor(getActivity().getResources().getColor(R.color.white));
            sortAscending.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha));
        }
    }



    @OnClick(R.id.sort_descending)
    void sortDescending_Click()
    {

        whetherDescending = true;

        clearAscending();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortDescending.setTextColor(getActivity().getResources().getColor(R.color.white,null));
            sortDescending.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortDescending.setTextColor(getActivity().getResources().getColor(R.color.white));
            sortDescending.setBackgroundColor(getActivity().getResources().getColor(R.color.Gray88Alpha));
        }
    }





    @OnClick(R.id.apply_button)
    void apply_click()
    {

        if(getActivity() instanceof NotifySort)
        {
            NotifySort notifySort = (NotifySort) getActivity();
            notifySort.applySort(sort_by,whetherDescending);

            showToastMessage("Applied !");
        }else
        {
            showToastMessage("Unable to apply !");
        }


        dismiss();

//        showToastMessage(String.valueOf(sort_by) + " : " + String.valueOf(whetherAscending));

    }

    @OnClick(R.id.cancel_button)
    void cancel_click()
    {

        showToastMessage("Cancelled !");
        dismiss();
    }




    void showToastMessage(String message)
    {
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }



    void clearSortOptions()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByTitle.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800,null));
            sortByRating.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800,null));
            sortByReleaseDate.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800,null));

            sortByRating.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option,null));
            sortByTitle.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option,null));
            sortByReleaseDate.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option,null));

        }else
        {
            sortByTitle.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800));
            sortByRating.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800));
            sortByReleaseDate.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800));

            sortByRating.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option));
            sortByTitle.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option));
            sortByReleaseDate.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option));
        }
    }


    void clearAscending()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortAscending.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800,null));
            sortDescending.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800,null));

            sortAscending.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option,null));
            sortDescending.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option,null));

        }else
        {
            sortAscending.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800));
            sortDescending.setTextColor(getActivity().getResources().getColor(R.color.blueGrey800));

            sortAscending.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option));
            sortDescending.setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey_sort_option));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(unbinder!=null)
        {
            unbinder.unbind();
        }
    }

    public interface NotifySort{

        void applySort(int sortBy, boolean whetherDescendingLocal);
    }


    public void setCurrentSort(int sort_by,boolean whetherDescending)
    {
        this.sort_by = sort_by;
        this.whetherDescending = whetherDescending;


//        clearAscending();
//        clearSortOptions();
    }


    void applyCurrentSort()
    {
        if(sort_by == SORT_BY_RATING)
        {
            sortByRating_click();

        }else if (sort_by == SORT_BY_TITLE)
        {
            sortByTitle_click();

        }else if (sort_by == SORT_BY_RELEASE_DATE)
        {
            sortByReleaseDate_click();
        }

        if(whetherDescending)
        {
            sortDescending_Click();
        }
        else
        {
            sortByAscending_Click();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this,outState);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Icepick.restoreInstanceState(this,savedInstanceState);
    }
}
