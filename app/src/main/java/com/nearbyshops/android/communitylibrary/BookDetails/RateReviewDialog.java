package com.nearbyshops.android.communitylibrary.BookDetails;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Login.NotifyAboutLogin;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookReviewService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.MemberService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nearbyshops.android.communitylibrary.R.id.login_button;
import static com.nearbyshops.android.communitylibrary.R.id.ratingBar;

/**
 * Created by sumeet on 12/8/16.
 */

public class RateReviewDialog extends DialogFragment{


    @BindView(R.id.dialog_dismiss_icon)
    ImageView dismiss_dialog_button;

    @BindView(R.id.submit_button)
    TextView submit_button;

    @BindView(R.id.cancel_button)
    TextView cancel_button;

    @BindView(R.id.review_text)
    EditText review_text;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.review_title)
    TextView review_title;

    @BindView(R.id.member_name)
    TextView member_name;

    @BindView(R.id.member_profile_image)
    ImageView member_profile_image;

    int book_id;


    BookReview review_for_edit;
    boolean isModeEdit = false;


    @Inject
    BookReviewService bookReviewService;


    @Inject
    MemberService memberService;


    public RateReviewDialog() {
        super();

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }



    Unbinder unbinder;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_rate_review, container);

        unbinder =  ButterKnife.bind(this,view);

        if(isModeEdit && review_for_edit!=null)
        {
            submit_button.setText("Update");
            cancel_button.setText("Delete");

            review_title.setText(review_for_edit.getReviewTitle());
            review_text.setText(review_for_edit.getReviewText());

            member_name.setText(review_for_edit.getRt_member_profile().getMemberName());

            ratingBar.setRating(review_for_edit.getRating());

            String imagePath = UtilityGeneral.getImageEndpointURL(getActivity())
                    + review_for_edit.getRt_member_profile().getProfileImageURL();

            Picasso.with(getActivity()).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(member_profile_image);


        }



        if(!isModeEdit)
        {
            setMember();
        }

//        setMember();


//        dismiss_dialog_button = (ImageView) view.findViewById(R.id.dialog_dismiss_icon);

        return view;
    }




    @OnClick(R.id.dialog_dismiss_icon)
    void dismiss_dialog()
    {
        dismiss();
        showToastMessage("Dismissed !");
    }



    void cancel_button()
    {
        dismiss();
        showToastMessage("Cancelled !");
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



    void showToastMessage(String message)
    {
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(unbinder!=null)
        {
            unbinder.unbind();
        }
    }


    public void setMode(BookReview reviewForUpdate,boolean isModeEdit, int book_id)
    {

        this.book_id = book_id;
        review_for_edit = reviewForUpdate;
        this.isModeEdit = isModeEdit;
    }



    void setMember()
    {

        Call<Member> call = memberService.getMember(UtilityGeneral.getUser(getActivity()).getMemberID());


        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {


                if(response.body()!=null)
                {
                    member_name.setText(" by " + response.body().getMemberName());


                    String imagePath = UtilityGeneral.getImageEndpointURL(getActivity())
                            + response.body().getProfileImageURL();

                    Picasso.with(getActivity()).load(imagePath)
                            .placeholder(R.drawable.book_placeholder_image)
                            .into(member_profile_image);

                }

            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

            }
        });


    }


    @OnClick(R.id.submit_button)
    void update_submit_click()
    {

        if(isModeEdit)
        {
            updateReview();
        }else
        {
            submitReview();
        }
    }


    @OnClick(R.id.cancel_button)
    void cancel_delete_click()
    {
        if(isModeEdit)
        {
            // delete the review

            Call<ResponseBody> call = bookReviewService.deleteBookReview(review_for_edit.getBookReviewID());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(response.code()==200)
                    {
                        showToastMessage("Deleted !");


                        if(getActivity() instanceof NotifyReviewUpdate)
                        {
                            ((NotifyReviewUpdate)getActivity()).notifyReviewDeleted();
                        }

                        dismiss();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
        else
        {
            cancel_button();
        }

    }


    void submitReview()
    {
        BookReview bookReview = new BookReview();
//        bookReview.setReviewDate(new java.sql.Date(System.currentTimeMillis()));
        bookReview.setRating((int) ratingBar.getRating());
        bookReview.setReviewTitle(review_title.getText().toString());
        bookReview.setReviewText(review_text.getText().toString());
        bookReview.setBookID(book_id);
        bookReview.setMemberID(UtilityGeneral.getUser(getActivity()).getMemberID());

        Call<BookReview> call = bookReviewService.insertBookReview(bookReview);

        call.enqueue(new Callback<BookReview>() {
            @Override
            public void onResponse(Call<BookReview> call, Response<BookReview> response) {

                if(response.code()==201)
                {
                    showToastMessage("Submitted !");

                    if(getActivity() instanceof NotifyReviewUpdate)
                    {
                        ((NotifyReviewUpdate)getActivity()).notifyReviewSubmitted();
                    }

                    dismiss();

                }
            }

            @Override
            public void onFailure(Call<BookReview> call, Throwable t) {


                showToastMessage("Failed !");

            }
        });
    }




    void updateReview()
    {
        if(review_for_edit!=null)
        {

            review_for_edit.setRating((int)ratingBar.getRating());
            review_for_edit.setReviewTitle(review_title.getText().toString());
            review_for_edit.setReviewText(review_text.getText().toString());


            long date = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(date);

//            review_for_edit.setReviewDate(new java.sql.Date(date));

            Call<ResponseBody> call = bookReviewService.updateBookReview(review_for_edit,review_for_edit.getBookReviewID());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code()==200)
                    {
                        showToastMessage(getString(R.string.udate_successful_api_response));

                        if(getActivity() instanceof NotifyReviewUpdate)
                        {
                            ((NotifyReviewUpdate)getActivity()).notifyReviewUpdated();
                        }

                        dismiss();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    showToastMessage(getString(R.string.api_response_no_item_updated));
                }
            });

        }
    }



}
