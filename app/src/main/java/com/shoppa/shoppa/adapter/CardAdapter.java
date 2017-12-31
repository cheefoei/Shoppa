package com.shoppa.shoppa.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.db.entity.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private List<Card> cardList;
    private OnItemChangedListener onItemChangedListener;
    private DatabaseReference mReference;

    public CardAdapter(Context mContext,
                       ProgressDialog mProgressDialog,
                       List<Card> cardList,
                       OnItemChangedListener onItemChangedListener,
                       DatabaseReference mReference) {

        this.mContext = mContext;
        this.mProgressDialog = mProgressDialog;
        this.cardList = cardList;
        this.onItemChangedListener = onItemChangedListener;
        this.mReference = mReference;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {

        final int position = i;
        final Card card = cardList.get(i);

        String[] cardMonthArr = mContext.getResources().getStringArray(R.array.month);
        String cardMonth = cardMonthArr[card.getMonth() - 1];

        cardViewHolder.type.setText(card.getType());
        cardViewHolder.number.setText(card.getCardNumber());
        cardViewHolder.date.setText(cardMonth + " " + card.getYear());
        cardViewHolder.name.setText(card.getHolderName());

        cardViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme)
                        .setTitle("Confirmation")
                        .setMessage("Remove " + card.getCardNumber() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                removeCard(card);

                                cardList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());

                                onItemChangedListener.onItemChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    private void removeCard(final Card card) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                mProgressDialog.setMessage("Removing card ...");
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                mReference.child(card.getId()).removeValue();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView type, number, date, name;
        ImageButton btnDelete;

        CardViewHolder(View view) {

            super(view);
            type = (TextView) view.findViewById(R.id.tv_card_type);
            number = (TextView) view.findViewById(R.id.tv_card_number);
            date = (TextView) view.findViewById(R.id.tv_card_date);
            name = (TextView) view.findViewById(R.id.tv_card_holder_name);
            btnDelete = (ImageButton) view.findViewById(R.id.btn_delete_card);
        }
    }

    public interface OnItemChangedListener {
        void onItemChanged();
    }
}
