package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mFragment = new PayCardFragment();
        mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.container_payment, mFragment);
        mFragmentTransaction.commit();
    }
}
