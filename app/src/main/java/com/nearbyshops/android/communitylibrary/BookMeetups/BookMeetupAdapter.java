package com.nearbyshops.android.communitylibrary.BookMeetups;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.Model.BookMeetup;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sumeet on 19/12/15.
 */


public class BookMeetupAdapter extends RecyclerView.Adapter<BookMeetupAdapter.ViewHolder>{


    List<BookMeetup> dataset;
    Context context;

    public BookMeetupAdapter(List<BookMeetup> dataset, Context context) {

        this.dataset = dataset;
        this.context = context;
    }

    @Override
    public BookMeetupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book_meetup,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookMeetupAdapter.ViewHolder holder, final int position) {

        BookMeetup meetup = dataset.get(position);

        holder.meetupName.setText(meetup.getMeetupName());
        holder.distance.setText(String.format("%.2f",meetup.getRt_distance()) + " Km | ");
        holder.venueText.setText(meetup.getVenue());
        holder.meetupPurpose.setText(meetup.getMeetupPurpose());

        if(meetup.getDateAndTime()!=null)
        {
//            holder.dateAndTime.setText(meetup.getDateAndTime().toString());

//                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM ''yyyy");

                //"EEE, MMM d, ''yy"
                //"yyyy-MM-dd"

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(meetup.getDateAndTime().getTime());
            holder.dateAndTime.setText(calendar.getTime().toLocaleString());
        }else
        {
            holder.dateAndTime.setText("Date Not Available !");
        }

        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getPosterURL();

            Picasso.with(context).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(holder.poster);
    }


    @Override
    public int getItemCount() {

        return dataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.poster)
        ImageView poster;

        @BindView(R.id.meetup_name)
        TextView meetupName;

        @BindView(R.id.meetup_purpose)
        TextView meetupPurpose;

        @BindView(R.id.distance)
        TextView distance;

        @BindView(R.id.venue)
        TextView venueText;

        @BindView(R.id.date_and_time)
        TextView dateAndTime;



        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }


    }// ViewHolder Class declaration ends




    void showToastMessage(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}