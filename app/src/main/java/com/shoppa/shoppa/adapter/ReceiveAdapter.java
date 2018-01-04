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
import com.shoppa.shoppa.db.entity.Transfer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ReceiveViewHolder> {

    private DatabaseReference mReference;
    private List<Transfer> receiveList;

    public ReceiveAdapter(DatabaseReference mReference, List<Transfer> receiveList) {
        this.mReference = mReference;
        this.receiveList = receiveList;
    }

    @Override
    public ReceiveViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_receive, viewGroup, false);
        return new ReceiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReceiveViewHolder receiveViewHolder, int i) {

        final ReceiveViewHolder holder = receiveViewHolder;
        Transfer transfer = receiveList.get(i);

        mReference = ShoppaApplication.mDatabase.getReference("user");

        Query query = mReference.child(transfer.getUserFrom());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    holder.sender.setText((String) dataSnapshot.child("name").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Date date = new Date(transfer.getDate());
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        receiveViewHolder.time.setText(simpleDateFormat.format(date));
        receiveViewHolder.amount.setText(String.format(
                Locale.getDefault(), "%.2f", transfer.getAmount()));
    }

    @Override
    public int getItemCount() {
        return receiveList.size();
    }

    class ReceiveViewHolder extends RecyclerView.ViewHolder {

        TextView sender, time, amount;

        ReceiveViewHolder(View view) {

            super(view);
            sender = (TextView) view.findViewById(R.id.tv_sender_name);
            time = (TextView) view.findViewById(R.id.tv_receive_time);
            amount = (TextView) view.findViewById(R.id.tv_receive_amount);
        }
    }

}
