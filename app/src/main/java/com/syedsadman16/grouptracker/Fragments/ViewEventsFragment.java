package com.syedsadman16.grouptracker.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.syedsadman16.grouptracker.Activities.EventEdit;
import com.syedsadman16.grouptracker.Activities.EventViewer;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Activities.MapsActivity;
import com.syedsadman16.grouptracker.Activities.SignIn;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

//=============================================================================
// Retrieves information from Firebase and populates fields for an Event
// Users can leave and Admin can delete events
// IMPORTANT: Every time a listener is registered to an reference, it must
//            be unregistered!!!
// Clicking "Edit Event" launches EventEdit.java
//=============================================================================

public class ViewEventsFragment extends Fragment {

    Button sign_out_btn, leaveGroupButton, editEvent;
    String eventid, date, time, description, image, location, name ,password, createdBy, uid;
    String adminEventId;
    TextView nameTextView, timeTextView, locationTextView, detailsTextView;
    ImageView eventImageView;

    public ViewEventsFragment() { } // Required empty constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        editEvent = view.findViewById(R.id.editButton);

        // If user is here, then user has an eventid != null
        // Create a reference and register it to receive information about the current event
        Firebase.setAndroidContext(getActivity());
        final Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        eventid = User.eventid;
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child(eventid).child("eventName").getValue().toString();
                date = dataSnapshot.child(eventid).child("eventDate").getValue().toString();
                time = dataSnapshot.child(eventid).child("eventTime").getValue().toString();
                description = dataSnapshot.child(eventid).child("eventDescription").getValue().toString();
                location = dataSnapshot.child(eventid).child("eventLocation").getValue().toString();
                createdBy = dataSnapshot.child(eventid).child("createdBy").getValue().toString();
                uid = dataSnapshot.child(eventid).child("uid").getValue().toString();
                image = dataSnapshot.child(eventid).child("eventImageURL").getValue().toString();

                nameTextView.setText(name);
                timeTextView.setText( date + " " + "@" + " " + time);
                locationTextView.setText("" +location);
                detailsTextView.setText(description);

                // Take base64 string and decode into byte array
                // Convert the byte array into a Bitmap
                // Set the Bitmap to ImageView
                byte[] imageBytes2 = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.length);
                eventImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                eventImageView.setImageBitmap(decodedImage);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        reference.addValueEventListener(eventListener);


        // Change this to User Profile page
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EventsFragment", "User has signed out");
                FirebaseAuth.getInstance().signOut();
                User.clear();
                startActivity(new Intent(getActivity(), SignIn.class));
            }
        });


        // When admin leaves, delete event. When user leaves, change user eventid
        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Admin permissions. Compare uid retrieved from event with User.uid
                // First update Firebase by changing each members  eventid to "null"
                // Switch to MainActivity => Default EventsFragment
                // Unregister the listener and delete the event
                if(uid.equals(User.uid)){
                    changeAllUserFirebase();
                    final String tempEventId = User.eventid;
                    User.eventid = "null";
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    reference.removeEventListener(eventListener);
                    deleteEvent(tempEventId);
                }

                // Member Permissions
                // Remove member from Members list
                // Update their eventid to "null"
                else {
                    removeMemberFirebase();
                    User.eventid = "null";
                    changeUserFirebase(User.uid, User.eventid);
                    reference.removeEventListener(eventListener);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });

        // Allows the user to click the location and start GPS
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+location)));
            }
        });

        detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getContext());

                float scale = getResources().getDisplayMetrics().density;
                int dpSize = (int) (20*scale + 0.5f);

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setPadding(dpSize, 0, dpSize, 0);
                layout.addView(edittext);

                alert.setMessage("Enter new description");
                alert.setView(layout);

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/");
                        reference.child(User.eventid).child("eventDescription").setValue(edittext.getText().toString());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();

            }
        });

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(getActivity(), EventEdit.class);
               // edit.putExtra("Image", image);
                edit.putExtra("Title", name);
                edit.putExtra("Date", date);
                edit.putExtra("Time", time);
                edit.putExtra("Description", description);
                edit.putExtra("Location", location);
                startActivity(edit);
            }
        });

    }


    // Used for Regular members, NOT Admin
    // When user leaves event, remove them from Members list
    public void removeMemberFirebase() {
        Firebase eventReference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid);
        eventReference.child("Members").child(User.uid).removeValue();
    }

    // Changes eventid of ALL users to null when the Admin leaves event
    // Reference Members list of the current event
    // Retrieve each member and change eventid to null
    public void changeAllUserFirebase(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+eventid+"/Members");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    String memberid = child.child("uid").getValue().toString();
                    changeUserFirebase(memberid, "null");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    // Changes eventid of single user
    public void changeUserFirebase(String userid, String eventid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(userid).child("eventid").setValue(eventid);
    }

    // Remove the entire event from Firebase
    public void deleteEvent(String eventid){
        Log.i("Main", "Deleting Event from View");
        Log.i("Main", "DELETED " + eventid);
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.child(eventid).removeValue();
    }



}
