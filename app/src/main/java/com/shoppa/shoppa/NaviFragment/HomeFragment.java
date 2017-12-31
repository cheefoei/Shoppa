package com.shoppa.shoppa.NaviFragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ScannerActivity;
import com.shoppa.shoppa.ShopActivity;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Store;
import com.shoppa.shoppa.db.entity.User;

public class HomeFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    private final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.app_name));

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        FloatingActionButton fabShop = (FloatingActionButton) v.findViewById(R.id.fab_shop);
        fabShop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_ACCESS_CAMERA);
                } else {
                    startScan();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                String storeId = scanningResult.getContents();
                checkStore(storeId);
            }
        } else {
            Toast.makeText(getActivity(), "No scan data received!", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(
                        getActivity(),
                        "Shoppa need permission to scan QR code",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startScan() {

            IntentIntegrator integrator = new IntentIntegrator(getActivity())
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                    .setPrompt("Scan shopper QR code")
                    .setCaptureActivity(ScannerActivity.class)
                    .setOrientationLocked(false);
            integrator.initiateScan();
//            AlertDialog.Builder builder
//                    = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
//                    .setTitle("Error")
//                    .setMessage("Please add new credit card to continue")
//                    .setPositiveButton("OK", null);
//            builder.show();

    }

    private void checkStore(final String storeId) {

        mProgressDialog.setMessage("Finding store ...");
        mProgressDialog.show();

        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("store");

        Query query = mReference.child(storeId);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                Store store = null;

                if (dataSnapshot.exists()) {
                    store = new Store(
                            dataSnapshot.getKey(),
                            (String) dataSnapshot.child("name").getValue()
                    );
                }

                if (store == null) {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                            .setTitle("Error")
                            .setMessage("Store not found")
                            .setPositiveButton("OK", null);
                    builder.show();
                } else {
                    Intent intent = new Intent(getActivity(), ShopActivity.class);
                    intent.putExtra("SHOP_ID", store.getId());
                    intent.putExtra("SHOP_NAME", store.getName());
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }

}
