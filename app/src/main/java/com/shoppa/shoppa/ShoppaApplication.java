package com.shoppa.shoppa;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;

public class ShoppaApplication extends Application {

    public static FirebaseDatabase mDatabase;

    @Override
    public void onCreate() {

        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public static boolean isInternetConnected(Context mContext) {

        ConnectivityManager cm
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
