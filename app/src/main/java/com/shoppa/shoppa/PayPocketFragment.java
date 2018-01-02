package com.shoppa.shoppa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.da.CartDA;
import com.shoppa.shoppa.db.da.CartDetailDA;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Cart;
import com.shoppa.shoppa.db.entity.CartDetail;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PayPocketFragment extends Fragment {

    private TextView tvPocketMoney;
    private TextView tvRemain;

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private User user;
    private Cart cart;

    private double remain = 0;
    private String storeId;

    public PayPocketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        storeId = getArguments().getString("SHOP_ID");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_pocket, container, false);

        CartDA cartDA = new CartDA(getActivity());
        cart = cartDA.getCart();
        cartDA.close();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        TextView tvTotalAmount = (TextView) view.findViewById(R.id.tv_total_amount);
        tvTotalAmount.setText(String.format(
                Locale.getDefault(), "RM %.2f", cart.getTotal()));

        tvPocketMoney = (TextView) view.findViewById(R.id.tv_pocket_money);
        tvRemain = (TextView) view.findViewById(R.id.tv_remain_pocket);

        Button btnPayByPocket = (Button) view.findViewById(R.id.btn_pay_by_pocket);
        btnPayByPocket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (remain > 0) {
                    doPayment();
                } else {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                            .setTitle("Error")
                            .setMessage("Your pocket money not enough to pay")
                            .setPositiveButton("OK", null);
                    builder.show();
                }
            }
        });

        retrievePocketMoney();

        return view;
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

                remain = money - cart.getTotal();
                tvRemain.setText(String.format(Locale.getDefault(), "%.2f", remain));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

    private void doPayment() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                mProgressDialog.setMessage("Paying your cart ...");
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                mReference = ShoppaApplication.mDatabase.getReference("cart");

                String cartId = mReference.push().getKey();
                mReference.child(cartId).setValue(cart);

                CartDetailDA cartDetailDA = new CartDetailDA(getActivity());
                List<CartDetail> cartDetailList = cartDetailDA.getCartDetail();

                for (CartDetail cartDetail : cartDetailList) {
                    String cartDetailId = mReference.push().getKey();
                    mReference.child(cartId).child(cartDetailId).setValue(cartDetail);
                }

                mReference = ShoppaApplication.mDatabase.getReference("payment");

                Payment payment = new Payment(new Date().getTime(), "Pocket Money", cart.getTotal(),
                        storeId, "", cartId, user.getId());
                String paymentId = mReference.push().getKey();
                mReference.child(paymentId).setValue(payment);

                mReference = ShoppaApplication.mDatabase.getReference("user");
                mReference.child(user.getId()).child("pocketMoney").setValue(remain);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle("Successful Message")
                        .setMessage("Your items is paid")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent();
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getActivity().finish();
                            }
                        });
                builder.show();
            }
        }.execute();

    }
}
