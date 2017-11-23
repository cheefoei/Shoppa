package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Button btnPayNow = (Button) findViewById(R.id.btn_pay_now);
        btnPayNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this, R.style.DialogTheme)
                        .setTitle("Successful Message")
                        .setMessage("Your items is paid")
                        .setPositiveButton("OK", null);
                builder.show();
            }
        });
    }
}
