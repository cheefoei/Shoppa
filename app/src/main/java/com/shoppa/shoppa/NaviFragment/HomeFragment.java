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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.shoppa.shoppa.adapter.TodayAdapter;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.Store;
import com.shoppa.shoppa.db.entity.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private TextView tvTodaySpent, tvTodayEmpty;
    private TodayAdapter adapter;
    private List<Payment> paymentList;

    private User user;

    private final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.app_name));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        paymentList = new ArrayList<>();
        adapter = new TodayAdapter(getActivity(), paymentList, mReference);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_today);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        tvTodaySpent = (TextView) view.findViewById(R.id.tv_today_spent);
        tvTodayEmpty = (TextView) view.findViewById(R.id.tv_empty_today);

        FloatingActionButton fabShop = (FloatingActionButton) view.findViewById(R.id.fab_shop);
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

        retrieveTodayHistory();

        return view;
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

    private void retrieveTodayHistory() {

        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long startTime = date.getTimeInMillis();

        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        long endTime = date.getTimeInMillis();

        UserDA userDA = new UserDA(getActivity());
        user = userDA.getUser();
        userDA.close();

        mReference = ShoppaApplication.mDatabase.getReference("payment");

        mProgressDialog.setMessage("Getting your data ...");
        mProgressDialog.show();

        Query query = mReference.orderByChild("date").startAt(startTime).endAt(endTime);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                paymentList.clear();
                double todaySpent = 0;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Payment payment = childSnapshot.getValue(Payment.class);
                    assert payment != null;
                    if (payment.getUserId().equals(user.getId())) {
                        todaySpent += payment.getAmount();
                        paymentList.add(payment);
                    }
                }
                tvTodaySpent.setText(String.format(Locale.getDefault(), "RM %.2f", todaySpent));
                adapter.notifyDataSetChanged();

                onPaymentListChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

    private void onPaymentListChanged() {

        if (paymentList.isEmpty()) {
            tvTodayEmpty.setVisibility(View.VISIBLE);
        } else {
            tvTodayEmpty.setVisibility(View.GONE);
        }
    }

    private void startScan() {

        IntentIntegrator integrator = new IntentIntegrator(getActivity())
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setPrompt("Scan shopper QR code")
                .setCaptureActivity(ScannerActivity.class)
                .setOrientationLocked(false);
        integrator.initiateScan();

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
