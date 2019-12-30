package com.syedsadman16.grouptracker.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Activities.SignUp;
import com.syedsadman16.grouptracker.R;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    Button signOutBtn;

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
        signOutBtn = view.findViewById(R.id.signOutBtn);
        // Setting up sign in Button (temporary)
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignUp.class));
            }
        });
    }


}
