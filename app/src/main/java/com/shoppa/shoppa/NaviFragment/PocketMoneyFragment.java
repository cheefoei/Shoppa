package com.shoppa.shoppa.NaviFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.User;

import java.util.Locale;

public class PocketMoneyFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private TextView tvPocketMoney;

    private User user;

    public PocketMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.fragment_pocket));

        // Enable menu in toolbar
        setHasOptionsMenu(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pocket_money, container, false);

        tvPocketMoney = (TextView) view.findViewById(R.id.tv_current_pocket_money);

        retrievePocketMoney();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_pocket_money, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.send_money) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                    .setTitle("Send Pocket Money")
                    .setView(R.layout.dialog_send_money)
                    .setPositiveButton("Confirm", null)
                    .setNegativeButton("Cancel", null);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrievePocketMoney() {

        mProgressDialog.setMessage("Checking your pocket money ...");
        mProgressDialog.show();

        UserDA userDA = new UserDA(getActivity());
        user = userDA.getUser();
        userDA.close();

        mReference = ShoppaApplication.mDatabase.getReference("user");

        Query query = mReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                double money = Double.NaN;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    User u = childSnapshot.getValue(User.class);
                    assert u != null;
                    money = u.getPocketMoney();
                }
                tvPocketMoney.setText(String.format(Locale.getDefault(), "%.2f", money));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }
}
