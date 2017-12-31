package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

public class CheckoutActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private String state = "card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        String storeId = getIntent().getStringExtra("SHOP_ID");
        final Bundle bundle = new Bundle();
        bundle.putString("SHOP_ID", storeId);

        fragmentManager = getSupportFragmentManager();
        Fragment mFragment = new PayCardFragment();
        mFragment.setArguments(bundle);
        final FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.container_payment, mFragment, state);
        mFragmentTransaction.commit();

        RadioGroup radioPayOption = (RadioGroup) findViewById(R.id.radio_pay_option);
        radioPayOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                Fragment mFragment = null;

                if (checkedId == R.id.radio_credit && !state.equals("card")) {
                    mFragment = new PayCardFragment();
                    mFragment.setArguments(bundle);
                    state = "card";
                } else if (checkedId == R.id.radio_pocket && !state.equals("pocket")) {
                    mFragment = new PayPocketFragment();
                    mFragment.setArguments(bundle);
                    state = "pocket";
                }

                FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(
                        R.anim.trans_fragment_enter,
                        R.anim.trans_fragment_exit,
                        R.anim.trans_fragment_pop_enter,
                        R.anim.trans_fragment_pop_exit
                );
                mFragmentTransaction.replace(R.id.container_payment, mFragment, state);
                mFragmentTransaction.commit();
            }
        });
    }
}
