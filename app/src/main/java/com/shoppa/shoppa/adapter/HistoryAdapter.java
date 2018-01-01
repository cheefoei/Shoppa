package com.shoppa.shoppa.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppa.shoppa.R;
import com.shoppa.shoppa.db.entity.Payment;
import com.shoppa.shoppa.db.entity.Store;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Payment> paymentList;
    private List<Store> storeList;


    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_history, viewGroup, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder historyViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity;

        HistoryViewHolder(View view) {

            super(view);
            name = (TextView) view.findViewById(R.id.tv_product_name);
            price = (TextView) view.findViewById(R.id.tv_product_price);
            quantity = (TextView) view.findViewById(R.id.tv_product_qty);
        }
    }

}
