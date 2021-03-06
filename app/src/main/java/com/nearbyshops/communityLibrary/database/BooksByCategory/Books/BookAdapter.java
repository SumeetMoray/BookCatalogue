package com.nearbyshops.communityLibrary.database.BooksByCategory.Books;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.BookDetails.BookDetail;
import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;
import com.nearbyshops.communityLibrary.database.Model.Book;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.RetrofitRestContract.BookService;
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


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{


    Map<Integer,Book> selectedItems = new HashMap<>();


    @Inject
    BookService itemCategoryService;

    List<Book> dataset;

    Context context;
    BookFragment activity;


    Book requestedChangeParent = null;


    NotificationReceiver notificationReceiver;


    final String IMAGE_ENDPOINT_URL = "/api/Images";

    public BookAdapter(List<Book> dataset, Context context, BookFragment activity, BookAdapter.NotificationReceiver notificationReceiver) {


        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);


        this.notificationReceiver = notificationReceiver;
        this.dataset = dataset;
        this.context = context;
        this.activity = activity;

        if(this.dataset == null)
        {
            this.dataset = new ArrayList<Book>();
        }

    }

    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder holder, final int position) {

//        holder.categoryName.setText(dataset.get(position).getItemName());
//        holder.categoryDescription.setText(dataset.get(position).getItemDescription());

        int rating_count = (int)dataset.get(position).getRt_rating_count();


        if(!dataset.get(position).getBookName().equals("null"))
        {
            holder.bookTitle.setText(dataset.get(position).getBookName());
        }else
        {
            holder.bookTitle.setText(R.string.book_title_not_available);

        }


        if(rating_count == 0)
        {
            holder.bookRating.setText(R.string.book_ratings_not_available);

        }else
        {
            //String.valueOf(dataset.get(position).getRt_rating_avg()

            holder.bookRating.setText("Rating : " + String.format("%.1f",dataset.get(position).getRt_rating_avg()) + " ("
                    + String.valueOf(rating_count) + " ratings)");
        }


        if(selectedItems.containsKey(dataset.get(position).getBookID()))
        {
            holder.bookListItem.setBackgroundColor(context.getResources().getColor(R.color.gplus_color_2));
        }
        else
        {
            holder.bookListItem.setBackgroundColor(context.getResources().getColor(R.color.white));
        }



        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getBookCoverImageURL();

//        if(!dataset.get(position).getBookCoverImageURL().equals(""))
//        {
            Picasso.with(context).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(holder.bookImage);
//        }


//        Log.d("applog",imagePath);

    }


    @Override
    public int getItemCount() {

        return dataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {


        private TextView categoryName,categoryDescription;

        @BindView(R.id.list_item_book)
        CardView bookListItem;

        @BindView(R.id.book_title) TextView bookTitle;


        @BindView(R.id.book_image) ImageView bookImage;

        @BindView(R.id.book_rating) TextView bookRating;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }


        @OnClick(R.id.list_item_book)
        public void listItemClick()
        {

            if(dataset.size()==0)
            {
                return;
            }

            Intent intent = new Intent(context, BookDetail.class);

            // the timestamp is not parcelable hence we save timestamp into long

            if(dataset.get(getLayoutPosition()).getDateOfPublish()!=null)
            {
                dataset.get(getLayoutPosition())
                        .setDateOfPublishInMillis(
                                dataset.get(getLayoutPosition()).getDateOfPublish().getTime()
                        );
            }

            intent.putExtra(BookDetail.BOOK_DETAIL_INTENT_KEY,dataset.get(getLayoutPosition()));
            context.startActivity(intent);
        }


        @OnLongClick(R.id.list_item_book)
        public boolean listItemLongClick()
        {

//            showToastMessage("Long Click !");


            if(selectedItems.containsKey(
                    dataset.get(getAdapterPosition())
                            .getBookID()
            )
                    )

            {
                selectedItems.remove(dataset.get(getAdapterPosition()).getBookID());

            }else
            {

                selectedItems.put(dataset.get(getAdapterPosition()).getBookID(),dataset.get(getAdapterPosition()));

                notificationReceiver.notifyItemCategorySelected();
            }


            notifyItemChanged(getAdapterPosition());

//            showToastMessage(String.valueOf(selectedItems.size()));


//            itemCategoryListItem.setBackgroundColor(context.getResources().getColor(R.color.cyan900));

            return true;

        }



        public void deleteItemCategory()
        {


//            Call<ResponseBody> call = itemCategoryService.deleteItemCategory(dataset.get(getLayoutPosition()).getItemCategoryID());

            if(dataset.size()==0)
            {
                return;
            }

            Call<ResponseBody> call = itemCategoryService.deleteBook(dataset.get(getLayoutPosition()).getBookID());


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
            inflater.inflate(R.menu.book_item_overflow, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.action_remove:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle(R.string.alert_title_confirm_delete_book)
                            .setMessage(R.string.alert_message_delete_book)
                            .setPositiveButton(context.getString(R.string.alert_dialog_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteItemCategory();
                                }
                            })
                            .setNegativeButton(context.getString(R.string.alert_dialog_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    showToastMessage(context.getString(R.string.alert_dialog_cancelled));
                                }
                            })
                            .show();


                    break;

                case R.id.action_edit:

//                    Intent intent = new Intent(context,EditBookMeetup.class);
//                    intent.putExtra(EditBookMeetup.ITEM_CATEGORY_INTENT_KEY,dataset.get(getLayoutPosition()));
//                    context.startActivity(intent);

                    if(dataset.size()!=0)
                    {
                        Intent intentEdit = new Intent(context,EditBook.class);


                        // the timestamp is not parcelable hence we save timestamp into long
/*

                        if(dataset.get(getLayoutPosition()).getDateOfPublish()!=null)
                        {
                            dataset.get(getLayoutPosition())
                                    .setDateOfPublishInMillis(
                                            dataset.get(getLayoutPosition()).getDateOfPublish().getTime()
                                    );
                        }

                        intentEdit.putExtra(BookDetail.BOOK_DETAIL_INTENT_KEY,dataset.get(getLayoutPosition()));
*/

                        intentEdit.putExtra(EditBook.ITEM_INTENT_KEY,dataset.get(getLayoutPosition()));
                        context.startActivity(intentEdit);
                    }

                    break;


                case R.id.action_change_parent:


//                    showToastMessage("Change parent !");

                    Intent intentParent = new Intent(context, BookCategoriesParent.class);

                    requestedChangeParent = dataset.get(getLayoutPosition());

                    // add the selected item category in the exclude list so that it does not get showed up as an option.
                    // This is required to prevent an item category to assign itself or its children as its parent.
                    // This should not happen because it would be erratic.

                    BookCategoriesParent.clearExcludeList(); // it is a safe to clear the list before adding any items in it.

//                    ItemCategoriesParent.excludeList
//                            .put(requestedChangeParent.getItemID(),requestedChangeParent);


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


    public interface NotificationReceiver
    {
        // method for notifying the list object to request sub category
//        public void notifyRequestSubCategory(ItemCategory itemCategory);

        public void notifyItemCategorySelected();

    }



    public Book getRequestedChangeParent() {
        return requestedChangeParent;
    }


    public void setRequestedChangeParent(Book requestedChangeParent) {
        this.requestedChangeParent = requestedChangeParent;
    }
}