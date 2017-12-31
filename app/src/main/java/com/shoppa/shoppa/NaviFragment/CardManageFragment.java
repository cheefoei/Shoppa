package com.shoppa.shoppa.NaviFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoppa.shoppa.AddCardActivity;
import com.shoppa.shoppa.R;
import com.shoppa.shoppa.ShoppaApplication;
import com.shoppa.shoppa.adapter.CardAdapter;
import com.shoppa.shoppa.db.da.UserDA;
import com.shoppa.shoppa.db.entity.Card;
import com.shoppa.shoppa.db.entity.User;

import java.util.ArrayList;
import java.util.List;

public class CardManageFragment extends Fragment implements CardAdapter.OnItemChangedListener {

    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;

    private TextView tvEmptyCard;
    private CardAdapter adapter;
    private List<Card> cardList;

    private User user;

    public CardManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.fragment_card));

        // Enable menu in toolbar
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_manage, container, false);

        mReference = ShoppaApplication.mDatabase.getReference("card");
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        tvEmptyCard = (TextView) view.findViewById(R.id.tv_card_empty);

        cardList = new ArrayList<>();
        adapter = new CardAdapter(
                getActivity(), mProgressDialog, cardList, this, mReference);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_card);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        UserDA userDA = new UserDA(getActivity());
        user = userDA.getUser();
        userDA.close();

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        retrieveCards();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_manage_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_card) {
            Intent intent = new Intent(getActivity(), AddCardActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemChanged() {

        if (cardList.isEmpty()) {
            tvEmptyCard.setVisibility(View.VISIBLE);
        } else {
            tvEmptyCard.setVisibility(View.GONE);
        }
    }

    private void retrieveCards() {

        mProgressDialog.setMessage("Retrieving your card ...");
        mProgressDialog.show();

        Query query = mReference.orderByChild("userId").equalTo(user.getId());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressDialog.dismiss();
                cardList.clear();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Card card = childSnapshot.getValue(Card.class);
                    assert card != null;
                    card.setId(childSnapshot.getKey());
                    cardList.add(card);
                }

                adapter.notifyDataSetChanged();
                onItemChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
            }
        });
    }
}
