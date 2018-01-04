package com.shoppa.shoppa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private ImageView mImageProfile;
    private EditText etName, etOldPassword, etNewPassword, etNewConfirmPassword;
    private Spinner mSpinnerGender;
    private AlertDialog dialogChangePassword;

    private String password;

    private User user;

    private final static int PICK_IMG_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mReference = ShoppaApplication.mDatabase.getReference("user");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        mImageProfile = (ImageView) findViewById(R.id.img_profile);
        mImageProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(
                        intent, "Select Picture"), PICK_IMG_REQUEST_CODE);

            }
        });

        mSpinnerGender = (Spinner) findViewById(R.id.spinner_gender);
        mSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String gender = mSpinnerGender.getSelectedItem().toString();
                if (!gender.equals(user.getGender())) {
                    mReference.child(user.getId()).child("gender").setValue(gender);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etName = (EditText) findViewById(R.id.et_profile_name);
        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().equals(user.getName())
                        && !s.toString().equals("")) {
                    mReference.child(user.getId()).child("name").setValue(s.toString());
                }
            }
        });

        TextView tvChangePassword = (TextView) findViewById(R.id.tv_change_password);
        tvChangePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                etOldPassword.requestFocus();
                etOldPassword.setText("");
                etNewPassword.setText("");
                etNewConfirmPassword.setText("");

                dialogChangePassword.show();
            }
        });

        ImageButton btnLogout = (ImageButton) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(ProfileActivity.this, R.style.DialogTheme)
                        .setTitle("Confirmation")
                        .setMessage("Confirm to logout?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                UserDA userDA = new UserDA(ProfileActivity.this);
                                userDA.removeAll();
                                userDA.close();

                                AlertDialog.Builder builder
                                        = new AlertDialog.Builder(ProfileActivity.this, R.style.DialogTheme)
                                        .setTitle("Successful")
                                        .setMessage("You already logout")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(
                                                        ProfileActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                builder.show();
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        UserDA userDA = new UserDA(this);
        user = userDA.getUser();
        userDA.close();

        getUserData();

        initChangePasswordDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMG_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                mReference.child(user.getId()).child("profile").setValue(encoded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(
                ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initChangePasswordDialog() {

        View dialogView = View.inflate(
                ProfileActivity.this, R.layout.dialog_change_password, null);

        dialogChangePassword
                = new AlertDialog.Builder(ProfileActivity.this, R.style.DialogTheme)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialogChangePassword.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialogChangePassword.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (isValid()) {

                            mProgressDialog.setMessage("Updating password ...");
                            mProgressDialog.show();

                            mReference.child(user.getId()).child("password")
                                    .setValue(getEncryptedPassword(password));

                            mProgressDialog.dismiss();

                            AlertDialog.Builder builder
                                    = new AlertDialog.Builder(ProfileActivity.this, R.style.DialogTheme)
                                    .setTitle("Successful")
                                    .setMessage("Your password is updated")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialogChangePassword.dismiss();
                                        }
                                    });
                            builder.show();
                        }
                    }
                });
            }
        });

        etOldPassword = (EditText) dialogView.findViewById(R.id.et_profile_old_password);
        etNewPassword = (EditText) dialogView.findViewById(R.id.et_profile_new_password);
        etNewConfirmPassword = (EditText) dialogView.findViewById(R.id.et_profile_confirm_new_password);
    }

    private boolean isValid() {

        boolean isValid = true;

        String oldPassword = etOldPassword.getText().toString();
        password = etNewPassword.getText().toString();
        String confirmPassword = etNewConfirmPassword.getText().toString();

        if (confirmPassword.equals("")) {
            etNewConfirmPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (password.equals("")) {
            etNewPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (oldPassword.equals("")) {
            etOldPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        } else {
            String p = getEncryptedPassword(oldPassword);
            String up = user.getPassword();
            if (!p.equals(up)) {
                etOldPassword.setError(getString(R.string.error_wrong_password));
                isValid = false;
            }
        }

        if (!password.equals("") && !confirmPassword.equals("")) {
            if (!password.equals(confirmPassword)) {
                etNewConfirmPassword.setError(getString(R.string.error_password_not_match));
                etNewPassword.setError(getString(R.string.error_password_not_match));
                isValid = false;
            }
        }

        return isValid;
    }

    private void getUserData() {

        mProgressDialog.setMessage("Getting profile information ...");
        mProgressDialog.show();

        mReference = ShoppaApplication.mDatabase.getReference("user");

        Query query = mReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    user = childSnapshot.getValue(User.class);
                    assert user != null;
                    user.setId(childSnapshot.getKey());
                }

                etName.setText(user.getName());
                etName.setSelection(etName.getText().length());
                etName.clearFocus();

                if (user.getGender() != null) {
                    if (user.getGender().equals("Female")) {
                        mSpinnerGender.setSelection(1);
                    }
                }

                if (user.getProfile() != null) {
                    byte[] decodedString = Base64.decode(user.getProfile(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory
                            .decodeByteArray(decodedString, 0, decodedString.length);
                    mImageProfile.setImageBitmap(decodedByte);
                } else {
                    mImageProfile.setImageResource(R.drawable.user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }

    private String getEncryptedPassword(String p) {

        String encryptedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(p.getBytes());
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
