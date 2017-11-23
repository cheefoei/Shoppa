package com.shoppa.shoppa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        Button btnRegister = (Button) view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RegisterFragment registerFragment = new RegisterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.trans_fragment_enter,
                        R.anim.trans_fragment_exit,
                        R.anim.trans_fragment_pop_enter,
                        R.anim.trans_fragment_pop_exit
                );
                fragmentTransaction.replace(R.id.login_container, registerFragment);
                fragmentTransaction.addToBackStack(null); // Press back key to go back
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
