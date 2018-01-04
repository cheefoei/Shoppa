package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Card;
import com.shoppa.shoppa.db.entity.User;

import java.util.ArrayList;
import java.util.List;

public class AddMoneyActivity extends AppCompatActivity {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private List<Card> cardList;

    private TextView tvCardHolderName;
    private EditText etAmount;
    private Spinner mSpinnerCard;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pocket_money);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        cardList = new ArrayList<>();

        mSpinnerCard = (Spinner) findViewById(R.id.spinner_pocket_card_chooser);
        tvCardHolderName = (TextView) findViewById(R.id.tv_pocket_card_holder_name);
        etAmount = (EditText) findViewById(R.id.et_add_money_amount);

        Button btnPayNow = (Button) findViewById(R.id.btn_add_money);
        btnPayNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (etAmount.getText().toString().equals("")) {
                    etAmount.setError(getString(R.string.error_required_field));
                } else {
                    addMoney();
                }
            }
        });

        checkCardExist();
    }

    private void checkCardExist() {

        mProgressDialog.setMessage("Checking your card info ...");
        mProgressDialog.show();

        UserDA userDA = new UserDA(this);
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
                    tvCardHolderName.setText("");
                } else {

                    tvCardHolderName.setText(cardList.get(0).getHolderName());

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            AddMoneyActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            cardNumberList);

                    mSpinnerCard.setAdapter(arrayAdapter);
                    mSpinnerCard.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent,
                                                           View view,
                                                           int position, long id) {
                                    tvCardHolderName.setText(cardList.get(position).getHolderName());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }

    private void addMoney() {

        final double amount = Double.parseDouble(etAmount.getText().toString());

        if (amount > 0) {

            mReference = ShoppaApplication.mDatabase.getReference("user");

            final Query query = mReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mProgressDialog.dismiss();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        User u = childSnapshot.getValue(User.class);
                        assert u != null;
                        user.setPocketMoney(u.getPocketMoney());
                    }

                    query.removeEventListener(this);

                    double addedAmount = amount + user.getPocketMoney();

                    mReference.child(user.getId()).child("pocketMoney").setValue(addedAmount);

                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(AddMoneyActivity.this, R.style.DialogTheme)
                            .setTitle("Successful")
                            .setMessage("Your pocket money is updated")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    builder.show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                }
            });

        } else {
            etAmount.setError(getString(R.string.error_zero_amount));
        }
    }
}
