package com.shoppa.shoppa.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.Store;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Payment> paymentList;
    private DatabaseReference mReference;

    public HistoryAdapter(List<Payment> paymentList,
                          DatabaseReference mReference) {

        this.paymentList = paymentList;
        this.mReference = mReference;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_history, viewGroup, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder historyViewHolder, int i) {

        final HistoryViewHolder holder = historyViewHolder;

        Payment payment = paymentList.get(i);

        mReference = ShoppaApplication.mDatabase.getReference("store");

        Query query = mReference.child(payment.getStoreId());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Store store = dataSnapshot.getValue(Store.class);
                    assert store != null;
                    holder.store.setText(store.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        historyViewHolder.total.setText(String.format(
                Locale.getDefault(), "RM %.2f", payment.getAmount()));

        Date date = new Date(payment.getDate());
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        historyViewHolder.time.setText(simpleDateFormat.format(date));
        historyViewHolder.method.setText(payment.getType());
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView store, total, time, method;

        HistoryViewHolder(View view) {

            super(view);
            store = (TextView) view.findViewById(R.id.tv_history_store);
            total = (TextView) view.findViewById(R.id.tv_history_spent);
            time = (TextView) view.findViewById(R.id.tv_history_time);
            method = (TextView) view.findViewById(R.id.tv_history_method);
        }
    }

}
