package com.syedsadman16.grouptracker.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;


public class ProfileFragment extends Fragment {

    ImageView profileImageView;
    TextView nameTextView, emailTextView, passwordTextView, phoneTextView;
    Button signOutButton;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        profileImageView = view.findViewById(R.id.profile_image_view);
        nameTextView = view.findViewById(R.id.profile_name_text_view);
        emailTextView = view.findViewById(R.id.profile_email_text_view);
        passwordTextView = view.findViewById(R.id.profile_password_text_view);
        phoneTextView = view.findViewById(R.id.profile_phone_text_view);
        signOutButton = view.findViewById(R.id.profile_sign_out_button);

        nameTextView.setText(User.fullName);
        emailTextView.setText(User.email);
        passwordTextView.setText("******");
        phoneTextView.setText(User.phone);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                User.clear();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });
    }



}
