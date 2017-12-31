package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.User;

public class PayCardFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public PayCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_card, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        if (isCardExist()) {
            LinearLayout layoutAddCard = (LinearLayout) view.findViewById(R.id.layout_add_card);
            layoutAddCard.setVisibility(View.GONE);
        } else {
            LinearLayout layoutPayCard = (LinearLayout) view.findViewById(R.id.layout_payment_card);
            layoutPayCard.setVisibility(View.GONE);
        }

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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle("Successful Message")
                        .setMessage("Your items is paid")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        return view;
    }

    private boolean isCardExist() {

        mProgressDialog.setMessage("Checking your card info ...");
        mProgressDialog.show();

        final boolean[] isCardAndMoneyExist = {false};

        UserDA userDA = new UserDA(getActivity());
        User user = userDA.getUser();
        userDA.close();

        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("card");

        Query query = mReference.orderByChild("userId").equalTo(user.getId());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                isCardAndMoneyExist[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

        return isCardAndMoneyExist[0];
    }

}
