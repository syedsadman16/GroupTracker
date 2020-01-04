package com.syedsadman16.grouptracker.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

public class ViewEventsFragment extends Fragment {

    Button sign_out_btn;

    public ViewEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_events, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sign_out_btn = view.findViewById(R.id.signOutBtn);
        // Setting up sign in Button (temporary)
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EventsFragment", "User has signed out");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });
    }

}
