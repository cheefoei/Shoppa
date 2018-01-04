package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private String email, password;

    private User loginUser;

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

//        TextView tvForgot = (TextView) view.findViewById(R.id.tv_forgot);
//        tvForgot.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

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

        DatabaseReference mReference = ShoppaApplication.mDatabase.getReference("user");

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Logging in ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final Query query = mReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                boolean valid = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    loginUser = childSnapshot.getValue(User.class);
                    assert loginUser != null;
                    if (loginUser.getPassword().equals(getEncryptedPassword())) {
                        loginUser.setId(childSnapshot.getKey());
                        valid = true;
                    }
                }

                query.removeEventListener(this);

                if (!valid) {
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                            .setTitle("Error")
                            .setMessage("Email or password is incorrect")
                            .setPositiveButton("OK", null);
                    builder.show();
                } else {
                    doSuccessLogin();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

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

    private void doSuccessLogin() {

        //Opening User sqlite database
        UserDA userDA = new UserDA(getActivity());

        //Saving user data into sqlite database
        User user = userDA.insertUser(
                loginUser.getId(),
                loginUser.getName(),
                loginUser.getGender(),
                loginUser.getEmail(),
                loginUser.getProfile(),
                loginUser.getPocketMoney()
        );

        //Closing sqlite database
        userDA.close();

        if (user != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }
}
