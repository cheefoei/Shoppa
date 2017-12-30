package com.shoppa.shoppa;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shoppa.shoppa.adapter.CartAdapter;
import com.shoppa.shoppa.db.entity.CartDetail;
import com.shoppa.shoppa.db.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity implements CartAdapter.OnItemChangedListener {

    private CartAdapter adapter;
    private List<Item> itemList;
    private List<CartDetail> cartDetailList;

    private Item item;

    private String storeId;
    private final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        if (getSupportActionBar() != null) {
            String label = getIntent().getStringExtra("SHOP_NAME");
            getSupportActionBar().setTitle(label);
        }
        storeId = getIntent().getStringExtra("SHOP_ID");

        itemList = new ArrayList<>();
        cartDetailList = new ArrayList<>();
        adapter = new CartAdapter(this, itemList, cartDetailList, this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_cart);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Button btnCheckout = (Button) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShopActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

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
    public void onBackPressed() {

        if (itemList.isEmpty() && cartDetailList.isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
                    .setTitle("Warning")
                    .setMessage("Cancel shopping?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemList.clear();
                            cartDetailList.clear();
                            ShopActivity.this.onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                checkItem(scanningResult.getContents());
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

    private void checkItem(String barcode) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Finding item ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("item");

        Query query = mReference.orderByChild("barcode").equalTo(barcode);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Item tempItem = childSnapshot.getValue(Item.class);
                    assert tempItem != null;
                    if (tempItem.getStoreId().equals(storeId)) {
                        item = tempItem;
                    } else {
                        item = null;
                    }
                }

                if (item == null) {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
                            .setTitle("Error")
                            .setMessage("Item not found")
                            .setPositiveButton("OK", null);
                    builder.show();
                } else {
                    doShowResult();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

    private void doShowResult() {

        View dialogView = View.inflate(this, R.layout.dialog_product_new, null);

        final TextView tvPrice = (TextView) dialogView.findViewById(R.id.tv_dialog_product_price);
        tvPrice.setText(String.format(Locale.getDefault(), "RM %.2f", item.getPrice()));

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_product_quantity);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int quantity = Integer.parseInt(spinner.getSelectedItem().toString());
                double price = quantity * item.getPrice();
                tvPrice.setText(String.format(Locale.getDefault(), "RM %.2f", price));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        byte[] decodedString = Base64.decode(item.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory
                .decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imgProduct = (ImageView) dialogView.findViewById(R.id.img_dialog_product);
        imgProduct.setImageBitmap(decodedByte);

        AlertDialog.Builder builder
                = new AlertDialog.Builder(ShopActivity.this, R.style.DialogTheme)
                .setTitle(item.getName())
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        CartDetail cartDetail = new CartDetail(
                                Integer.parseInt(spinner.getSelectedItem().toString()),
                                item.getId()
                        );
                        itemList.add(item);
                        cartDetailList.add(cartDetail);

                        adapter.notifyDataSetChanged();
                        onItemChanged();
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onItemChanged() {

        TextView tvEmptyCart = (TextView) findViewById(R.id.tv_cart_empty);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);

        double total = 0.00;

        if (itemList.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            for (int i = 0; i < cartDetailList.size(); i++) {
                int quantity = cartDetailList.get(i).getQuantity();
                double price = itemList.get(i).getPrice();
                total += (quantity * price);
            }
        }

        tvTotalPrice.setText(String.format(Locale.getDefault(), "RM %.2f", total));
    }
}
