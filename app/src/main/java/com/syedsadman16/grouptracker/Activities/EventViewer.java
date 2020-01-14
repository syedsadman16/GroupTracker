package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.syedsadman16.grouptracker.Adapters.EventsAdapter;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class EventViewer extends AppCompatActivity {

    ArrayList<Events> eventsArrayList = new ArrayList<>();
    String eventid, date, time, description, image, location, name ,password, createdBy, uid;

    /*
     * Just for viewing events. To Join events, check EventsAdapter
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);

        RecyclerView eventRecyclerView = findViewById(R.id.event_recycler_view);
        final EventsAdapter adapter = new EventsAdapter(this, eventsArrayList);
        // Attach the adapter to the recyclerview to populate items
        eventRecyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.addValueEventListener(new ValueEventListener() {
            // Gets all children of Events.json ==> eventid (unique id for each individual event)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren() ){

                    name = child.child("eventName").getValue().toString();
                    date = child.child("eventDate").getValue().toString();
                    time = child.child("eventTime").getValue().toString();
                    image = child.child("eventImageURL").getValue().toString();
                    description = child.child("eventDescription").getValue().toString();
                    location = child.child("eventLocation").getValue().toString();
                    password = child.child("eventPassword").getValue().toString();
                    createdBy = child.child("createdBy").getValue().toString();
                    uid = child.child("uid").getValue().toString();
                    eventid = child.child("eventid").getValue().toString();

                    // Adding to events object for future use
                    Events event = new Events(name, createdBy, date, eventid, image);
                    eventsArrayList.add(event);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

    }




}