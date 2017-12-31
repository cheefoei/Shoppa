package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Card;

public class AddCardActivity extends AppCompatActivity {

    private TextView tvCardType;
    private EditText etCardNumber, etCardCVV, etCardName;
    private Spinner spinnerYear, spinnerMonth;

    private String cardNumber, cardName, cardType;
    private int cardCVV, cardYear, cardMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        tvCardType = (TextView) findViewById(R.id.tv_new_card_type);

        etCardNumber = (EditText) findViewById(R.id.et_card_number);
        etCardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String num = s.toString();
                if (num.equals("")) {
                    tvCardType.setText("");
                } else {
                    char firstNum = s.toString().charAt(0);
                    if (firstNum == '4') {
                        tvCardType.setText(getString(R.string.string_visa_card));
                    } else if (firstNum == '5') {
                        tvCardType.setText(getString(R.string.string_master_card));
                    } else {
                        tvCardType.setText(getString(R.string.string_not_card));
                    }
                }
            }
        });

        etCardCVV = (EditText) findViewById(R.id.et_card_cvv);
        etCardName = (EditText) findViewById(R.id.et_card_holder_name);

        spinnerYear = (Spinner) findViewById(R.id.spinner_card_year);
        spinnerMonth = (Spinner) findViewById(R.id.spinner_card_month);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.done_add_card) {
            if (isValid()) {
                addNewCard();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValid() {

        boolean isValid = true;

        cardNumber = etCardNumber.getText().toString();
        cardName = etCardName.getText().toString();
        cardYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        cardMonth = spinnerMonth.getSelectedItemPosition() + 1;
        cardType = tvCardType.getText().toString();

        if (cardName.equals("")) {
            etCardName.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        if (etCardCVV.getText().toString().equals("")) {
            etCardCVV.setError(getString(R.string.error_required_field));
            isValid = false;
        } else {
            cardCVV = Integer.parseInt(etCardCVV.getText().toString());
        }

        if (cardNumber.equals("")) {
            etCardNumber.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        if (cardType.equals(getString(R.string.string_not_card))) {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(AddCardActivity.this, R.style.DialogTheme)
                    .setTitle("Error")
                    .setMessage("Your credit card is not supported now")
                    .setPositiveButton("OK", null);
            builder.show();
            isValid = false;
        }

        return isValid;
    }

    private void addNewCard() {

        new AsyncTask<Void, Void, Void>() {

            private ProgressDialog mProgressDialog;
            private DatabaseReference mReference;

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                mReference = ShoppaApplication.mDatabase.getReference("card");

                mProgressDialog = new ProgressDialog(AddCardActivity.this);
                mProgressDialog.setMessage("Adding new card ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                UserDA userDA = new UserDA(AddCardActivity.this);
                String userId = userDA.getUser().getId();

                String cardId = mReference.push().getKey();
                Card card = new Card(
                        cardName, cardNumber, cardCVV, cardYear, cardMonth, cardType, userId);
                mReference.child(cardId).setValue(card);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(AddCardActivity.this, R.style.DialogTheme)
                        .setTitle("Successful Message")
                        .setMessage("You added a new card")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddCardActivity.this.finish();
                            }
                        });
                builder.show();
            }
        }.execute();

    }
}
