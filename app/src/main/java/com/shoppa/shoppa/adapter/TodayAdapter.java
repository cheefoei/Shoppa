package com.shoppa.shoppa.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayViewHolder> {

    private Context mContext;
    private List<Payment> paymentList;
    private List<Store> storeList;
    private DatabaseReference mReference;

    public TodayAdapter(Context mContext,
                        List<Payment> paymentList,
                        DatabaseReference mReference) {

        this.mContext = mContext;
        this.paymentList = paymentList;
        this.storeList = new ArrayList<>();
        this.mReference = mReference;
    }

    @Override
    public TodayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_today, viewGroup, false);
        return new TodayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodayViewHolder todayViewHolder, int i) {

        final TodayViewHolder holder = todayViewHolder;
        final int position = i;

        final Payment payment = paymentList.get(i);

        mReference = ShoppaApplication.mDatabase.getReference("store");

        Query query = mReference.child(payment.getStoreId());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Store store = dataSnapshot.getValue(Store.class);
                    assert store != null;
                    holder.store.setText(store.getName());
                    storeList.add(store);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        todayViewHolder.total.setText(String.format(
                Locale.getDefault(), "RM %.2f", payment.getAmount()));

        todayViewHolder.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Store store = storeList.get(position);

                AlertDialog.Builder builder
                        = new AlertDialog.Builder(mContext, R.style.DialogTheme)
                        .setTitle(store.getName())
                        .setMessage(payment.getDate() + "")
                        .setPositiveButton("OK", null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class TodayViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView store, total;

        TodayViewHolder(View view) {

            super(view);
            layout = (LinearLayout) view.findViewById(R.id.layout_today_item);
            store = (TextView) view.findViewById(R.id.tv_today_store_name);
            total = (TextView) view.findViewById(R.id.tv_today_store_total);
        }
    }
}
