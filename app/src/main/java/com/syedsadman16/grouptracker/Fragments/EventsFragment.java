package com.syedsadman16.grouptracker.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.EventCreation;
import com.syedsadman16.grouptracker.Activities.EventViewer;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.R;

public class EventsFragment extends Fragment {
    Button sign_out_btn, join_button, create_button;

    // Required empty public constructor
    public EventsFragment() { }


    // Inflate the layout for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
        // don't attach to root since bottom navigation takes care of what is displayed
    }


    // After View has been inflated, reference all the methods that need to be called
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sign_out_btn = view.findViewById(R.id.signOutBtn);
        join_button = view.findViewById(R.id.join_button);
        create_button = view.findViewById(R.id.create_button);

        // Setting up sign in Button (temporary)
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EventsFragment", "User has signed out");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EventCreation.class));
            }
        });
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EventViewer.class));
            }
        });


    }


}
