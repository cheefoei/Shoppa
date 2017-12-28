package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FirebaseFirestore mFireStore;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button btnRegister = (Button) view.findViewById(R.id.btn_do_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                attemptRegister();
            }
        });

        Button btnBack = (Button) view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Go back log in
                getActivity().onBackPressed();
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        return view;
    }

    private void attemptRegister() {

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("user_email", "ccc@ccc.com");
        mFireStore.collection("shoppa").document("user")
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

    }

}
