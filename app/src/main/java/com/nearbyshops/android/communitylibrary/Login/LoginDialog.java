package com.nearbyshops.android.communitylibrary.Login;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.MemberService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sumeet on 12/8/16.
 */

public class LoginDialog extends DialogFragment implements View.OnClickListener {


    ImageView dismiss_dialog_button;
    TextView signup_button;
    TextView login_button;
    EditText username;
    EditText password;

    @Inject
    MemberService memberService;


    public LoginDialog() {
        super();

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_login, container);

        dismiss_dialog_button = (ImageView) view.findViewById(R.id.dialog_dismiss_icon);
        signup_button = (TextView) view.findViewById(R.id.sign_up_button);
        login_button = (TextView) view.findViewById(R.id.login_button);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);

        signup_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
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
                Toast.makeText(getActivity(), R.string.dialog_dismissed,Toast.LENGTH_SHORT).show();

                break;

            case R.id.login_button:

                login_click();

                break;

            case R.id.sign_up_button:

                signUp_click();

                break;

            default:
                break;
        }

    }


    void login_click()
    {
        Call<Member> call = memberService.validateMember(password.getText().toString(),username.getText().toString());


        call.enqueue(new Callback<Member>() {

            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {


                if(response.body()!=null)
                {

//                    UtilityGeneral.saveUserID(response.body().getMemberID());
                    UtilityGeneral.saveUser(response.body(),getActivity());
                }

                if(response.code()== 401)
                {
                    showToastMessage(getString(R.string.toast_login_unauthorized));
                }else
                {
                    if(UtilityGeneral.getUser(getActivity())!=null)
                    {

                        showToastMessage(getString(R.string.toast_login_successful));

                        if(getActivity() instanceof NotifyAboutLogin)
                        {
                            ((NotifyAboutLogin) getActivity()).NotifyLogin();
                        }

                        dismiss();

                    }
                }


            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {


                showToastMessage(getString(R.string.network_not_available));

            }
        });




    }


    void showToastMessage(String message)
    {
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }


    void signUp_click()
    {
        Intent intent = new Intent(getContext(),SignUp.class);
        startActivity(intent);
    }

}
