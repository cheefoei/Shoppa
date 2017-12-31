package com.shoppa.shoppa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppa.shoppa.R;
import com.shoppa.shoppa.db.entity.Payment;

import java.util.List;
import java.util.Locale;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayViewHolder> {

    private Context mContext;
    private List<Payment> paymentList;

    public TodayAdapter(Context mContext,
                        List<Payment> paymentList) {

        this.mContext = mContext;
        this.paymentList = paymentList;
    }

    @Override
    public TodayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_today, viewGroup, false);
        return new TodayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodayViewHolder todayViewHolder, int i) {

        Payment payment = paymentList.get(i);

        todayViewHolder.store.setText(payment.getStoreId());
        todayViewHolder.total.setText(String.format(
                Locale.getDefault(), "RM %.2f", payment.getAmount()));
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class TodayViewHolder extends RecyclerView.ViewHolder {

        TextView store, total;

        TodayViewHolder(View view) {

            super(view);
            store = (TextView) view.findViewById(R.id.tv_today_store_name);
            total = (TextView) view.findViewById(R.id.tv_today_store_total);
        }
    }
}
