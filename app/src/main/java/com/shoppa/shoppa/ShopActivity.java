package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        if (getSupportActionBar() != null) {
            String label = getIntent().getStringExtra("SHOP_ID");
            getSupportActionBar().setTitle(label);
        }

        LinearLayout layoutCart = (LinearLayout) findViewById(R.id.layout_cart);
        if (layoutCart.getVisibility() != View.GONE) {
            initCartLayoutComponent();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_cart);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            }
        });
    }

    private void initCartLayoutComponent() {

        TextView tvCartEmpty = (TextView) findViewById(R.id.tv_cart_empty);
        tvCartEmpty.setVisibility(View.GONE);

        ImageButton btnProductDelete = (ImageButton) findViewById(R.id.btn_product_delete);
        btnProductDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Remove tomato sauce?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        Button btnProductEdit = (Button) findViewById(R.id.btn_product_edit);
        btnProductEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        Button btnCheckout = (Button) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

}
