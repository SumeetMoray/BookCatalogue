package com.nearbyshops.communityLibrary.database.BooksByCategory.BookCategories;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;
import com.nearbyshops.communityLibrary.database.Model.BookCategory;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.RetrofitRestContract.BookCategoryService;
import com.nearbyshops.communityLibrary.database.SelectParent.BookCategoriesParent;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sumeet on 19/12/15.
 */


public class BookCategoriesAdapter extends RecyclerView.Adapter<BookCategoriesAdapter.ViewHolder>{


    Map<Integer,BookCategory> selectedItems = new HashMap<>();


    @Inject
    BookCategoryService bookCategoryService;

    List<BookCategory> dataset;

    Context context;
    BookCategoriesFragment activity;


    BookCategory requestedChangeParent = null;


    ReceiveNotificationsFromAdapter notificationReceiver;


    final String IMAGE_ENDPOINT_URL = "/api/Images";

    public BookCategoriesAdapter(List<BookCategory> dataset, Context context, BookCategoriesFragment activity, ReceiveNotificationsFromAdapter notificationReceiver) {


        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);


        this.notificationReceiver = notificationReceiver;
        this.dataset = dataset;
        this.context = context;
        this.activity = activity;

        if(this.dataset == null)
        {
            this.dataset = new ArrayList<BookCategory>();
        }

    }

    @Override
    public BookCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book_category,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookCategoriesAdapter.ViewHolder holder, final int position) {


//        holder.bookCategoryName.setText(String.valueOf(position+1) + ". " + dataset.get(position).getBookCategoryName());

        holder.bookCategoryName.setText(dataset.get(position).getBookCategoryName());

        holder.itemCount.setText(String.valueOf(dataset.get(position).getRt_book_count()) + " " + "Books");

        if(selectedItems.containsKey(dataset.get(position).getBookCategoryID()))
        {
            holder.list_item_book_category.setBackgroundColor(context.getResources().getColor(R.color.gplus_color_2));
        }
        else
        {
            holder.list_item_book_category.setBackgroundColor(context.getResources().getColor(R.color.white));
        }



        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getImageURL();


        String imageURL = dataset.get(position).getImageURL();


        Picasso.with(context).load(imagePath)
                .placeholder(R.drawable.book_placeholder_image)
                .into(holder.bookCategoryImage);
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    @Override
    public int getItemCount() {

        return dataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {


        @BindView(R.id.book_category_image)
        ImageView bookCategoryImage;

        @BindView(R.id.book_category_name)
        TextView bookCategoryName;

        @BindView(R.id.item_count)
        TextView itemCount;

        @BindView(R.id.more_vert)
        ImageView more_options;

        @BindView(R.id.list_item_book_category)
        RelativeLayout list_item_book_category;



        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }



        @OnLongClick(R.id.list_item_book_category)
        public boolean listItemLongClick()
        {

//            showToastMessage("Long Click !");


            if(selectedItems.containsKey(
                    dataset.get(getLayoutPosition())
                            .getBookCategoryID()
            )
                    )

            {
                selectedItems.remove(dataset.get(getLayoutPosition()).getBookCategoryID());

            }else
            {

                selectedItems.put(dataset.get(getLayoutPosition()).getBookCategoryID(),dataset.get(getLayoutPosition()));

                notificationReceiver.notifyItemCategorySelected();
            }


            notifyItemChanged(getLayoutPosition());

//            showToastMessage(String.valueOf(selectedItems.size()));


//            itemCategoryListItem.setBackgroundColor(context.getResources().getColor(R.color.cyan900));

            return true;
        }



        @OnClick(R.id.list_item_book_category)
        public void itemCategoryListItemClick()
        {

            if (dataset == null) {

                return;
            }

            if(dataset.size()==0)
            {
                return;
            }

            notificationReceiver.notifyRequestSubCategory(dataset.get(getLayoutPosition()));
            selectedItems.clear();

        }



        public void deleteItemCategory()
        {


            Call<ResponseBody> call = bookCategoryService.deleteItemCategory(dataset.get(getLayoutPosition()).getBookCategoryID());

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                    if(response.code()==200)
                    {
                        notifyDelete();

                        showToastMessage("Removed !");

                    }else if(response.code()==304)
                    {
                        showToastMessage("Delete failed !");

                    }else
                    {
                        showToastMessage("Server Error !");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    showToastMessage(context.getString(R.string.network_not_available));
                }
            });
        }


        @OnClick(R.id.more_vert)
        void optionsOverflowClick(View v)
        {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.book_category_item_overflow, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.action_remove:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle(R.string.alert_title_delete_book_category)
                            .setMessage(R.string.alert_message_delete_book_category)
                            .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteItemCategory();
                                }
                            })
                            .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    showToastMessage(context.getString(R.string.alert_dialog_cancelled));
                                }
                            })
                            .show();


                    break;

                case R.id.action_edit:

                    Intent intent = new Intent(context,EditBookCategory.class);
                    intent.putExtra(EditBookCategory.ITEM_CATEGORY_INTENT_KEY,dataset.get(getLayoutPosition()));
                    context.startActivity(intent);

                    break;


                case R.id.action_change_parent:


//                    showToastMessage("Change parent !");
                    Intent intentParent = new Intent(context, BookCategoriesParent.class);

                    requestedChangeParent = dataset.get(getLayoutPosition());

                    // add the selected item category in the exclude list so that it does not get showed up as an option.
                    // This is required to prevent an item category to assign itself or its children as its parent.
                    // This should not happen because it would be erratic.

                    BookCategoriesParent.clearExcludeList(); // it is a safe to clear the list before adding any items in it.
                    BookCategoriesParent.excludeList
                            .put(requestedChangeParent.getBookCategoryID(),requestedChangeParent);

                    activity.startActivityForResult(intentParent,1,null);


                    break;




                default:

                    break;

            }

            return false;
        }



    }// ViewHolder Class declaration ends




    void showToastMessage(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    public void notifyDelete()
    {
        activity.notifyDelete();

    }


    public interface ReceiveNotificationsFromAdapter
    {
        // method for notifying the list object to request sub category
        public void notifyRequestSubCategory(BookCategory itemCategory);
        public void notifyItemCategorySelected();

    }


    public void setRequestedChangeParent(BookCategory requestedChangeParent) {
        this.requestedChangeParent = requestedChangeParent;
    }

    public BookCategory getRequestedChangeParent() {
        return requestedChangeParent;
    }
}