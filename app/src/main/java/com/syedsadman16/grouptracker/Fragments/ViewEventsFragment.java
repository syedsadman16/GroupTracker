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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ViewEventsFragment extends Fragment {

    Button sign_out_btn, leaveGroupButton;
    String eventid, date, time, description, image, location, name ,password, createdBy, uid;
    String adminEventId;
    TextView nameTextView, timeTextView, locationTextView, detailsTextView;

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
        nameTextView = view.findViewById(R.id.eventNameTextView);
        timeTextView = view.findViewById(R.id.eventTimeTextView);
        locationTextView = view.findViewById(R.id.eventLocationTextView);
        detailsTextView = view.findViewById(R.id.eventDetailsTextView);
        sign_out_btn = view.findViewById(R.id.signOutBtn);
        leaveGroupButton = view.findViewById(R.id.leaveGroupButton);

        Firebase.setAndroidContext(getActivity());

        // Setting up sign out Button (temporary)
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EventsFragment", "User has signed out");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });

        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMemberFirebase();
                User.eventid = "null";
                changeUserFirebase();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        // Retrieve eventid set on signup and joining events
        eventid = User.eventid;

        // Check the users eventid and pull key from datatbase
        String url = "https://grouptracker-ef84c.firebaseio.com/events.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    name = jsonObject.getJSONObject(eventid).getString("Name");
                    date = jsonObject.getJSONObject(eventid).getString("Date");
                    time = jsonObject.getJSONObject(eventid).getString("Time");
                    description = jsonObject.getJSONObject(eventid).getString("Description");
                    image = jsonObject.getJSONObject(eventid).getString("Image");
                    location = jsonObject.getJSONObject(eventid).getString("Location");
                    createdBy = jsonObject.getJSONObject(eventid).getString("CreatedBy");
                    uid = jsonObject.getJSONObject(eventid).getString("uid");
                    adminEventId = jsonObject.getJSONObject(eventid).getString("eventid"); // Admin privileges

                    Log.i("ViewEvents", name + date + location);
                    nameTextView.setText(name);
                    timeTextView.setText("When: " + date + " " + "@" + " " + time);
                    locationTextView.setText("Location: " +location);
                    detailsTextView.setText("Details: " + description);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("VolleyError", ""+volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(stringRequest);


    }

    public void changeUserFirebase(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("eventid").setValue(User.eventid);
    }

    public void removeMemberFirebase() {
        Firebase eventReference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid);
        Log.i("VieFrag", "https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid);
        eventReference.child("Members").child(User.uid).removeValue();
    }

}
