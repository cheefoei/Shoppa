package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private String email, password;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = (EditText) view.findViewById(R.id.et_email);
        etPassword = (EditText) view.findViewById(R.id.et_password);

        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    attemptLogin();
                }
            }
        });

        Button btnRegister = (Button) view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RegisterFragment registerFragment = new RegisterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.trans_fragment_enter,
                        R.anim.trans_fragment_exit,
                        R.anim.trans_fragment_pop_enter,
                        R.anim.trans_fragment_pop_exit
                );
                fragmentTransaction.replace(R.id.login_container, registerFragment);
                fragmentTransaction.addToBackStack(null); // Press back key to go back
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private boolean isValid() {

        boolean isValid = true;

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (password.equals("")) {
            etPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        if (email.equals("")) {
            etEmail.setError(getString(R.string.error_required_field));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        return isValid;
    }

    private void attemptLogin() {

        new AsyncTask<Void, Void, Void>() {

            DatabaseReference mReference;
            ProgressDialog mProgressDialog;

            boolean valid = false;

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                mReference = ShoppaApplication.mDatabase.getReference("user");
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
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            if (user.getPassword().equals(getEncryptedPassword())) {
                                valid = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();

                if (!valid) {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                            .setTitle("Error")
                            .setMessage("Email or password is incorrect")
                            .setPositiveButton("OK", null);
                    builder.show();
                } else {
                    getActivity().finish();
                }
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
