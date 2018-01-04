package com.shoppa.shoppa.NaviFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.AddMoneyActivity;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Transfer;
import com.shoppa.shoppa.db.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PocketMoneyFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private TextView tvPocketMoney;
    private AlertDialog dialogSendMoney;
    private EditText etReceiver, etAmount, etPassword;

    private String receiverStr, amountStr, password;

    private User user;
    private User receiver;

    public PocketMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.fragment_pocket));

        // Enable menu in toolbar
        setHasOptionsMenu(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pocket_money, container, false);

        tvPocketMoney = (TextView) view.findViewById(R.id.tv_current_pocket_money);

        ViewPagerAdapter viewPagerAdapter
                = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MoneyTransferFragment(), "Transfer");
        viewPagerAdapter.addFragment(new MoneyReceiveFragment(), "Receive");

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager_pocket_money);
        mViewPager.setAdapter(viewPagerAdapter);

        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.tab_pocket_money);
        mTabLayout.setupWithViewPager(mViewPager);

        initSendMoneyDialog();

        retrievePocketMoney();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_pocket_money, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_money) {

            Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
            getActivity().startActivity(intent);

        } else if (id == R.id.send_money) {

            etReceiver.requestFocus();
            etReceiver.setText("");
            etAmount.setText("");
            etPassword.setText("");

            dialogSendMoney.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSendMoneyDialog() {

        View dialogView = View.inflate(getActivity(), R.layout.dialog_send_money, null);

        dialogSendMoney = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setTitle("Send Pocket Money")
                .setView(dialogView)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialogSendMoney.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialogSendMoney.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (isValid() && isAmountValid() && isPasswordValid()) {
                            checkReceiverIsValid();
                        }
                    }
                });
            }
        });

        etReceiver = (EditText) dialogView.findViewById(R.id.et_send_money_receiver);
        etAmount = (EditText) dialogView.findViewById(R.id.et_send_money_amount);
        etPassword = (EditText) dialogView.findViewById(R.id.et_send_money_password);
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
                    user = childSnapshot.getValue(User.class);
                    assert user != null;
                    user.setId(childSnapshot.getKey());
                    money = user.getPocketMoney();
                }
                tvPocketMoney.setText(String.format(Locale.getDefault(), "%.2f", money));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });

    }

    private void checkReceiverIsValid() {

        mProgressDialog.setMessage("Verifying receiver ...");
        mProgressDialog.show();

        mReference = ShoppaApplication.mDatabase.getReference("user");

        final Query query = mReference.orderByChild("email").equalTo(receiverStr);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        receiver = childSnapshot.getValue(User.class);
                        receiver.setId(childSnapshot.getKey());
                    }
                    query.removeEventListener(this);
                    sendMoney();
                } else {
                    etReceiver.setError(getString(R.string.error_invalid_receiver));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void sendMoney() {

        final double amount = Double.parseDouble(amountStr);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                mProgressDialog.setMessage("Sending pocket money ...");
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                double remainMoney = user.getPocketMoney() - amount;
                double addedMoney = receiver.getPocketMoney() + amount;

                mReference = ShoppaApplication.mDatabase.getReference("user");
                mReference.child(user.getId()).child("pocketMoney").setValue(remainMoney);
                mReference.child(receiver.getId()).child("pocketMoney").setValue(addedMoney);

                mReference = ShoppaApplication.mDatabase.getReference("transfer");

                String transferId = mReference.push().getKey();
                Transfer transfer = new Transfer(
                        user.getId(), receiver.getId(), new Date().getTime(), amount);
                mReference.child(transferId).setValue(transfer);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle("Successful")
                        .setMessage("Money is sent to " + receiver.getName())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogSendMoney.dismiss();
                            }
                        });
                builder.show();
            }
        }.execute();


    }

    private boolean isValid() {

        boolean isValid = true;

        receiverStr = etReceiver.getText().toString();
        amountStr = etAmount.getText().toString();
        password = etPassword.getText().toString();

        if (password.equals("")) {
            etPassword.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        if (amountStr.equals("")) {
            etAmount.setError(getString(R.string.error_required_field));
            isValid = false;
        }

        if (receiverStr.equals("")) {
            etReceiver.setError(getString(R.string.error_required_field));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(receiverStr).matches()) {
            etReceiver.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        return isValid;
    }

    private boolean isAmountValid() {

        double amount = Double.parseDouble(amountStr);
        if (amount > user.getPocketMoney()) {
            etAmount.setError(getString(R.string.error_over_amount));
        }
        return amount <= user.getPocketMoney();
    }

    private boolean isPasswordValid() {

        String encryptedPassword = getEncryptedPassword();
        if (!encryptedPassword.equals(user.getPassword())) {
            etPassword.setError(getString(R.string.error_wrong_password));
        }
        return encryptedPassword.equals(user.getPassword());
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

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
