package com.syedsadman16.grouptracker.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ViewEventsFragment extends Fragment {

    Button sign_out_btn, leaveGroupButton;
    String eventid, date, time, description, image, location, name ,password, createdBy, uid;
    String adminEventId;
    TextView nameTextView, timeTextView, locationTextView, detailsTextView;
    ImageView eventImageView;

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
        eventImageView = view.findViewById(R.id.eventImageView);

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
                // If event owner leaves, then delete the event
                /*if(uid.equals(User.uid)){ //event_owner = current_owner
                    changeAllUserFirebase();
                    //deleteEvent();
                    Log.i("ViewEventsFragment", "Works");
                } else { // Regular users to be deleted
                    */// Remove current user from members list and change their eventid status
                    removeMemberFirebase();
                    User.eventid = "null";
                    changeUserFirebase(User.uid, User.eventid);
                //}
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        // Start navigation to destination
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+location)));
            }
        });


        // Retrieve eventid of the signed-in user to select specific group user is in
        eventid = User.eventid;
        Log.i("EventCreation", "ViewEventsFrag" + User.eventid);
        // Check the  eventid and pull specific key from datatbase
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
                    location = jsonObject.getJSONObject(eventid).getString("Location");
                    createdBy = jsonObject.getJSONObject(eventid).getString("CreatedBy"); // name of event creator
                    uid = jsonObject.getJSONObject(eventid).getString("uid"); // UID of the event creator
                    image = jsonObject.getJSONObject(eventid).getString("Image");

                    Log.i("ViewEvents", name + date + location);
                    nameTextView.setText(name);
                    timeTextView.setText( date + " " + "@" + " " + time);
                    locationTextView.setText("" +location);
                    detailsTextView.setText(description);

                    // Take base64 string and decode into byte array
                    byte[] imageBytes2 = Base64.decode(image, Base64.DEFAULT);
                    // Convert the byte array into a Bitmap
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.length);
                    eventImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    eventImageView.setImageBitmap(decodedImage);

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

    // Changes eventid of single user
    public void changeUserFirebase(String userid,String eventid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(userid).child("eventid").setValue(eventid);
    }

    // Removes single user from Members list
    public void removeMemberFirebase() {
        Firebase eventReference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid);
        eventReference.child("Members").child(User.uid).removeValue();
    }

    // Changes eventid of all users
    public void changeAllUserFirebase(){
        // Change eventid of all member to "null"
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+eventid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    // Get each member
                    //String memberid = child.child("Members");
                    //changeUserFirebase(memberid, "null");
                    Log.i("ViewEventsFragment", "eventid"+eventid);
                   // Log.i("ViewEventsFragment", "UID"+memberid);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    // Delete event
    public void deleteEvent(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.child(eventid).removeValue();
    }


}
