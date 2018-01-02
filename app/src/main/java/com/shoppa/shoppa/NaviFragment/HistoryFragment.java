package com.shoppa.shoppa.NaviFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.adapter.HistoryAdapter;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private TextView tvHistoryDate, tvEmptyHistory;

    private HistoryAdapter adapter;
    private List<Payment> paymentList;

    // Calendar, Date formatter, variables
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat simpleDateFormat
            = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private int yearOfCalendar = 0;
    private int monthOfYear = 0;
    private int dayOfMonth = 0;

    private User user;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.fragment_history));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        tvEmptyHistory = (TextView) view.findViewById(R.id.tv_empty_history);
        tvHistoryDate = (TextView) view.findViewById(R.id.tv_history_date);
        tvHistoryDate.setText(simpleDateFormat.format(calendar.getTime()));

        Button btnCalendar = (Button) view.findViewById(R.id.btn_cal);
        btnCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Dialog dialog = onCreateCalendarDialog();
                dialog.create();
                dialog.show();
            }
        });

        paymentList = new ArrayList<>();
        adapter = new HistoryAdapter(paymentList, mReference);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_history);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        //Assign the variable to current date
        yearOfCalendar = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        UserDA userDA = new UserDA(getActivity());
        user = userDA.getUser();
        userDA.close();

        return view;
    }

    private Dialog onCreateCalendarDialog() {

        // Creates new date picker
        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(),
                R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        calendar.set(year, month, day);

                        yearOfCalendar = year;
                        monthOfYear = month;
                        dayOfMonth = day;

                        tvHistoryDate.setText(simpleDateFormat.format(calendar.getTime()));
                        onDateChanged();
                    }
                }, yearOfCalendar, monthOfYear, dayOfMonth);

        // Disable the future dates
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Return the date picker
        return mDatePicker;
    }

    private void onDateChanged() {

        Calendar date = new GregorianCalendar();
        date.set(yearOfCalendar, monthOfYear, dayOfMonth);

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long startTime = date.getTimeInMillis();

        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        long endTime = date.getTimeInMillis();

        mReference = ShoppaApplication.mDatabase.getReference("payment");

        mProgressDialog.setMessage("Getting your history ...");
        mProgressDialog.show();

        Query query = mReference.orderByChild("date").startAt(startTime).endAt(endTime);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                paymentList.clear();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Payment payment = childSnapshot.getValue(Payment.class);
                    assert payment != null;
                    if (payment.getUserId().equals(user.getId())) {
                        paymentList.add(payment);
                    }
                }
                adapter.notifyDataSetChanged();
                onHistoryListChange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }

    private void onHistoryListChange() {

        if (paymentList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
        }
    }
}
