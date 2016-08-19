package com.nearbyshops.android.communitylibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategoriesTabs;
import com.nearbyshops.android.communitylibrary.Login.LoginDialog;
import com.nearbyshops.android.communitylibrary.Login.NotifyAboutLogin;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotifyAboutLogin {


    private Unbinder unbinder;

    @BindView(R.id.option_all_books)
    RelativeLayout optionAllBooks;

    @BindView(R.id.option_books_by_category)
    RelativeLayout optionBooksByCategory;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkLogin();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showLoginDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.show(fm,"login");
    }



    void checkLogin()
    {

        /*if(UtilityGeneral.getUserID(this)==-1)
        {
            // No user Logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Login");
        }else
        {
            // user already logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Logout !");
        }*/

        if(UtilityGeneral.getUser(this)==null)
        {
            // No user Logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Login");


        }else
        {
            // user already logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Logout !");

        }


        setNavigationHeader();

    }

    @Override
    public void NotifyLogin()
    {
        navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Logout !");
//        showToastMessage("User ID : " + String.valueOf(UtilityGeneral.getUserID(this)));

//        UtilityGeneral.saveUser(null,this);

            setNavigationHeader();
    }


    void setNavigationHeader()
    {
        View headerLayout = navigationView.getHeaderView(0);

        ImageView profileImage = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView userName = (TextView) headerLayout.findViewById(R.id.member_name);


        Member member = UtilityGeneral.getUser(this);


        if(member!=null)
        {
            String imagePath = UtilityGeneral.getImageEndpointURL(this)
                    + member.getProfileImageURL();

            Picasso.with(this).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(profileImage);

            userName.setText(member.getMemberName());
        }else
        {
            profileImage.setImageDrawable(null);
            userName.setText("");
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

//            showToastMessage("Login");


            if(UtilityGeneral.getUser(this)==null)
            {
                showLoginDialog();

            }else
            {
                UtilityGeneral.saveUser(null,this);
                UtilityGeneral.saveUserID(-1);


                if(UtilityGeneral.getUser(this)==null)
                {
                    Log.d("login","User NULL");

                }else
                {
                    Log.d("login","User NOT NULL");
                }


                showToastMessage("Logged Out !");
                item.setTitle("Login");
                setNavigationHeader();
            }

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @OnClick(R.id.option_all_books)
    void allBooksClick()
    {
        showToastMessage("All Books Click");
    }


    @OnClick(R.id.option_books_by_category)
    void booksByCategoryClick()
    {
        Intent intent = new Intent(this, BookCategoriesTabs.class);
        startActivity(intent);
    }


    void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();

    }
}
