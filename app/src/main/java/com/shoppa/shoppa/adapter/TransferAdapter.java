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

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> {

    private DatabaseReference mReference;
    private List<Transfer> transferList;

    public TransferAdapter(DatabaseReference mReference, List<Transfer> transferList) {
        this.mReference = mReference;
        this.transferList = transferList;
    }

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_transfer, viewGroup, false);
        return new TransferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransferViewHolder transferViewHolder, int i) {

        final TransferViewHolder holder = transferViewHolder;
        Transfer transfer = transferList.get(i);

        mReference = ShoppaApplication.mDatabase.getReference("user");

        Query query = mReference.child(transfer.getUserTo());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    holder.receiver.setText((String) dataSnapshot.child("name").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Date date = new Date(transfer.getDate());
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        transferViewHolder.time.setText(simpleDateFormat.format(date));
        transferViewHolder.amount.setText(String.format(
                Locale.getDefault(), "%.2f", transfer.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transferList.size();
    }

    class TransferViewHolder extends RecyclerView.ViewHolder {

        TextView receiver, time, amount;

        TransferViewHolder(View view) {

            super(view);
            receiver = (TextView) view.findViewById(R.id.tv_receiver_name);
            time = (TextView) view.findViewById(R.id.tv_transfer_time);
            amount = (TextView) view.findViewById(R.id.tv_transfer_amount);
        }
    }

}
