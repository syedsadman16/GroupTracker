package com.syedsadman16.grouptracker.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.syedsadman16.grouptracker.R;

public class MapsFragment extends Fragment {


    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    // After View has been inflated, reference all the methods that need to be called
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


}
