package com.shoppa.shoppa.NaviFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shoppa.shoppa.R;

public class AddCardFragment extends Fragment {

    public AddCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Add New Card");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_card, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_add_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.done_add_card) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                    .setTitle("Successful Message")
                    .setMessage("You added a new card")
                    .setPositiveButton("OK", null);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
