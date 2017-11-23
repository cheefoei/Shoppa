package com.shoppa.shoppa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ShopActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ShopActivity.this.checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_ACCESS_CAMERA);
                } else {
                    startScanBarcode();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {

            if (scanningResult.getContents() != null) {

//                String shopId = scanningResult.getContents();
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
                        .setTitle("Tomata Sauce")
                        .setView(R.layout.dialog_product_new)
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        } else {
            Toast.makeText(this, "No scan data received!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanBarcode();
            } else {
                Toast.makeText(this, "Shoppa need permission to scan item's barcode", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startScanBarcode() {

        IntentIntegrator integrator = new IntentIntegrator(ShopActivity.this)
                .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
                .setPrompt("Scan product barcode")
                .setCaptureActivity(ScannerActivity.class)
                .setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void initCartLayoutComponent() {

        TextView tvCartEmpty = (TextView) findViewById(R.id.tv_cart_empty);
        tvCartEmpty.setVisibility(View.GONE);

//        View alertLayout = View.inflate(this, R.layout.dialog_product_edit, null);
//        Spinner spinner = (Spinner) alertLayout.findViewById(R.id.spinner_product_quantity);

        ImageButton btnProductDelete = (ImageButton) findViewById(R.id.btn_product_delete);
        btnProductDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
                        .setTitle("Edit Quantity")
                        .setView(R.layout.dialog_product_edit)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        Button btnCheckout = (Button) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShopActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

}
