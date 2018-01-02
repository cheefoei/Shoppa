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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.da.CartDA;
import com.shoppa.shoppa.db.da.CartDetailDA;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Card;
import com.shoppa.shoppa.db.entity.Cart;
import com.shoppa.shoppa.db.entity.CartDetail;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PayCardFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private List<Card> cardList;

    private LinearLayout layoutAddCard, layoutPayCard;
    private Spinner mSpinnerCard;
    private TextView tvCardName, tvTotalAmount;

    private User user;
    private Cart cart;
    private Card card;

    private String storeId;

    public PayCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        storeId = getArguments().getString("SHOP_ID");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_card, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        cardList = new ArrayList<>();

        layoutAddCard = (LinearLayout) view.findViewById(R.id.layout_add_card);
        layoutPayCard = (LinearLayout) view.findViewById(R.id.layout_payment_card);
        mSpinnerCard = (Spinner) view.findViewById(R.id.spinner_card_chooser);
        tvCardName = (TextView) view.findViewById(R.id.tv_card_holder_name);
        tvTotalAmount = (TextView) view.findViewById(R.id.tv_total_amount);

        Button btnAddCard = (Button) view.findViewById(R.id.btn_add_credit_card_now);
        btnAddCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCardActivity.class);
                startActivity(intent);
            }
        });

        Button btnPayNow = (Button) view.findViewById(R.id.btn_pay_now);
        btnPayNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int index = mSpinnerCard.getSelectedItemPosition();
                card = cardList.get(index);
                doPayment();
            }
        });

        checkCardExist();

        return view;
    }

    private void checkCardExist() {

        mProgressDialog.setMessage("Checking your card info ...");
        mProgressDialog.show();

        UserDA userDA = new UserDA(getActivity());
        user = userDA.getUser();
        userDA.close();

        mReference = ShoppaApplication.mDatabase.getReference("card");

        Query query = mReference.orderByChild("userId").equalTo(user.getId());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                List<String> cardNumberList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Card card = childSnapshot.getValue(Card.class);
                    assert card != null;
                    card.setId(childSnapshot.getKey());
                    cardList.add(card);
                    cardNumberList.add(card.getCardNumber());
                }

                if (cardList.isEmpty()) {
                    layoutPayCard.setVisibility(View.GONE);
                } else {
                    layoutAddCard.setVisibility(View.GONE);

                    tvCardName.setText(cardList.get(0).getHolderName());

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cardNumberList);
                    mSpinnerCard.setAdapter(arrayAdapter);
                    mSpinnerCard.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent,
                                                           View view,
                                                           int position, long id) {
                                    tvCardName.setText(cardList.get(position).getHolderName());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                    CartDA cartDA = new CartDA(getActivity());
                    cart = cartDA.getCart();
                    cartDA.close();

                    tvTotalAmount.setText(String.format(
                            Locale.getDefault(), "RM %.2f", cart.getTotal()));
                }
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

                Payment payment = new Payment(
                        new Date().getTime(), "Credit Card", cart.getTotal(), storeId,
                        card.getId(), cartId, user.getId());
                String paymentId = mReference.push().getKey();
                mReference.child(paymentId).setValue(payment);

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
