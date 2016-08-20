package com.nearbyshops.android.communitylibrary.SelectParent;

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

import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories.EditBookCategory;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookCategoryService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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


public class BookCategoriesParentAdapter extends RecyclerView.Adapter<BookCategoriesParentAdapter.ViewHolder>{



    @Inject
    BookCategoryService itemCategoryService;


    List<BookCategory> dataset;

    Context context;
    BookCategoriesParent bookCategoriesParent;


    Integer selectedPosition = null;


    NotificationReceiver notificationReceiver;


    final String IMAGE_ENDPOINT_URL = "/api/Images";

    public BookCategoriesParentAdapter(List<BookCategory> dataset, Context context, BookCategoriesParent activity
                            , NotificationReceiver notificationReceiver) {


        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);

        this.notificationReceiver = notificationReceiver;
        this.dataset = dataset;
        this.context = context;
        this.bookCategoriesParent = activity;

        if(this.dataset == null)
        {
            this.dataset = new ArrayList<BookCategory>();
        }

    }

    @Override
    public BookCategoriesParentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book_category_parent,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookCategoriesParentAdapter.ViewHolder holder, final int position) {

        holder.bookCategoryName.setText(dataset.get(position).getBookCategoryName());
        holder.itemCount.setText(String.valueOf(dataset.get(position).getRt_book_count()) + " Books");

        if(selectedPosition!=null)
        {
            if(selectedPosition==position){

                holder.list_item_book_category.setBackgroundColor(context.getResources().getColor(R.color.gplus_color_2));

                notificationReceiver.notifyItemSelected();

            }

//            holder.itemCategoryListItem.animate().rotation(90);
        }else
        {
            holder.list_item_book_category.setBackgroundColor(context.getResources().getColor(R.color.white));

        }

        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getImageURL();

        Picasso.with(context).load(imagePath)
                .placeholder(R.drawable.book_placeholder_image)
                .into(holder.bookCategoryImage);

        Log.d("applog",imagePath);

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

            int previousPosition = -1;



            if(selectedPosition!=null)
            {
                previousPosition = selectedPosition;
            }


//            showToastMessage("Long Click !");

            selectedPosition = getLayoutPosition();


//
            if(previousPosition!=selectedPosition)
            {

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                // item Selected


            }
            else
            {
                selectedPosition = null;


                if(previousPosition!=-1)
                {
                    notifyItemChanged(previousPosition);
                }

            }

//            notifyDataSetChanged();



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



            selectedPosition = null;
            bookCategoriesParent.notifyRequestSubCategory(dataset.get(getLayoutPosition()));


//            if (dataset.get(getLayoutPosition()).getIsLeafNode()) {

//                Intent intent = new Intent(context, Items.class);
//
//                intent.putExtra(Items.ITEM_CATEGORY_INTENT_KEY,dataset.get(getLayoutPosition()));
//
//                context.startActivity(intent);
//            }
//            else
//            {
//
//            }


        }



        public void deleteItemCategory()
        {


            Call<ResponseBody> call = itemCategoryService
                    .deleteItemCategory(dataset.get(getLayoutPosition()).getBookCategoryID());

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                    if(response.code()==200)
                    {
//                        notifyDelete();
                        notificationReceiver.notifyItemDeleted();

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

                    showToastMessage("Network request failed ! Please check your connection!");
                }
            });
        }


        @OnClick(R.id.more_vert)
        void optionsOverflowClick(View v)
        {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_category_book_overflow_parent_selection, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.action_remove:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Confirm Delete Item Category !")
                            .setMessage("Do you want to delete this Item Category ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteItemCategory();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    showToastMessage("Cancelled !");
                                }
                            })
                            .show();


                    break;

                case R.id.action_edit:

                    Intent intent = new Intent(context,EditBookCategory.class);
                    intent.putExtra(EditBookCategory.ITEM_CATEGORY_INTENT_KEY,dataset.get(getLayoutPosition()));
                    context.startActivity(intent);

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

//        bookCategoriesParent.notifyDelete();

    }


    public interface requestSubCategory
    {
        // method for notifying the list object to request sub category
        public void notifyRequestSubCategory(BookCategory itemCategory);
    }




    public void clearSelection()
    {

        selectedPosition = null;
    }



    public BookCategory getSelection()
    {
        if(selectedPosition!=null)
        {
            return dataset.get(selectedPosition);
        }
        else
        {
            return null;
        }
    }


    interface NotificationReceiver{

        void notifyItemSelected();

        void notifyItemDeleted();
    }

}