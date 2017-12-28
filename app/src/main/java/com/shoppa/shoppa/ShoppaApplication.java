package com.shoppa.shoppa;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ShoppaApplication extends Application {

    public static FirebaseDatabase mDatabase;

    @Override
    public void onCreate() {

        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
    }
}
