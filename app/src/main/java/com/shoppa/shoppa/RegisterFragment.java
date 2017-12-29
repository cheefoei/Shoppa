package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private EditText etName,
            etEmail,
            etPassword,
            etConfirmPassword;
    private String name, email, password;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etName = (EditText) view.findViewById(R.id.et_name);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        etConfirmPassword = (EditText) view.findViewById(R.id.et_confirm_password);

        Button btnRegister = (Button) view.findViewById(R.id.btn_do_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValid()) {
                    checkUserExist();
                }
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

        mReference = ShoppaApplication.mDatabase.getReference("user");

        return view;
    }

    private boolean isValid() {

        boolean isValid = true;

        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        String confirmPassword = etConfirmPassword.getText().toString();

        if (password.equals("")) {
            etPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (confirmPassword.equals("")) {
            etConfirmPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (!password.equals("") && !confirmPassword.equals("")) {
            if (!password.equals(confirmPassword)) {
                etPassword.setError(getString(R.string.error_password_not_match));
                etConfirmPassword.setError(getString(R.string.error_password_not_match));
                isValid = false;
            }
        }

        if (email.equals("")) {
            etEmail.setError(getString(R.string.error_required_field));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (name.equals("")) {
            etName.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        return isValid;
    }

    private void checkUserExist() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Registering your new account ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                Query query = mReference.orderByChild("email").equalTo(email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            mProgressDialog.dismiss();

                            AlertDialog.Builder builder
                                    = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                                    .setTitle("Error")
                                    .setMessage("You already have an account")
                                    .setPositiveButton("OK", null);
                            builder.show();
                        } else {
                            attemptRegister();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                return null;
            }

        }.execute();
    }

    private void attemptRegister() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String userId = mReference.push().getKey();
                String encryptedPassword = getEncryptedPassword();
                User user = new User(name, email, encryptedPassword);
                mReference.child(userId).setValue(user);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle("Successful")
                        .setMessage("You registered an account")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        });
                builder.show();
            }
        }.execute();

    }

    private String getEncryptedPassword() {

        String encryptedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(String.format("%02X", aByte));
            }
            encryptedPassword = sb.toString();
        } catch (NoSuchAlgorithmException exc) {
            exc.printStackTrace();
        }

        return encryptedPassword;
    }
}
