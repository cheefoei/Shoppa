package com.shoppa.shoppa;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.NaviFragment.CardManageFragment;
import com.shoppa.shoppa.NaviFragment.HistoryFragment;
import com.shoppa.shoppa.NaviFragment.HomeFragment;
import com.shoppa.shoppa.NaviFragment.PocketMoneyFragment;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView mImageProfile;
    private TextView tvUsername;

    private FragmentTransaction mFragmentTransaction;
    private Fragment mFragment;

    private User user;

    private final int LOG_IN_REQUEST_CODE = 1;

    private String currentFragment = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState(); //Show animation of hamburger

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeader = navigationView.getHeaderView(0);

        tvUsername = (TextView) navigationHeader.findViewById(R.id.tv_username);

        mImageProfile = (ImageView) navigationHeader.findViewById(R.id.img_profile);
        mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("item");
//
//        String id = mReference.push().getKey();
//        Item s = new Item("Photo Creater", "A seasonal magazine for photography lover", 10.60, "9771511636002","", "-L1Xc4l66XwgQPe1_Kvv");
//        mReference.child(id).setValue(s);

        if (!ShoppaApplication.isInternetConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("Error")
                    .setMessage("Connect to internet for using all features")
                    .setPositiveButton("I understand", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        } else {
            // Adding home fragment into frame as default
            FragmentManager fragmentManager = getSupportFragmentManager();
            mFragment = new HomeFragment();
            mFragmentTransaction = fragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.content_frame, mFragment);
            mFragmentTransaction.commit();
        }

        checkLogged();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOG_IN_REQUEST_CODE) {
            checkLogged();
        } else {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkLogged() {

        //Reading user data
        UserDA userDA = new UserDA(this);
        user = userDA.getUser();

        //Close user database
        userDA.close();

        if (user != null) {
            syncUserData();
        } else {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            startActivityForResult(intent, LOG_IN_REQUEST_CODE);
            finish();
        }
    }

    private void syncUserData() {

        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("user");

        Query query = mReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    user = childSnapshot.getValue(User.class);
                    assert user != null;
                    user.setId(childSnapshot.getKey());
                }

                if (user.getProfile() != null) {
                    byte[] decodedString = Base64.decode(user.getProfile(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory
                            .decodeByteArray(decodedString, 0, decodedString.length);
                    mImageProfile.setImageBitmap(decodedByte);
                } else {
                    mImageProfile.setImageResource(R.drawable.user);
                }
                tvUsername.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home && !currentFragment.equals("HOME")) {

            mFragment = new HomeFragment();
            currentFragment = "HOME";
        } else if (id == R.id.nav_card && !currentFragment.equals("CARD")) {

            mFragment = new CardManageFragment();
            currentFragment = "CARD";
        } else if (id == R.id.pocket_money && !currentFragment.equals("POCKET")) {

            mFragment = new PocketMoneyFragment();
            currentFragment = "POCKET";
        } else if (id == R.id.nav_history && !currentFragment.equals("HISTORY")) {

            mFragment = new HistoryFragment();
            currentFragment = "HISTORY";
        }

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.content_frame, mFragment);
        mFragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
