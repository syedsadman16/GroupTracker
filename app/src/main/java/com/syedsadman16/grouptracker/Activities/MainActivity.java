package com.syedsadman16.grouptracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.syedsadman16.grouptracker.Fragments.ChatFragment;
import com.syedsadman16.grouptracker.Fragments.EventsFragment;
import com.syedsadman16.grouptracker.Fragments.MapsFragment;
import com.syedsadman16.grouptracker.Fragments.ViewEventsFragment;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment fragment1 = new EventsFragment(); // Default
        final Fragment fragment1b = new ViewEventsFragment(); // Joined event
        final Fragment fragment2 = new ChatFragment();
        final Fragment fragment3 = new MapsFragment();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {

                    case R.id.event_button:
                        Log.i("ViewEventsFragment", "MainActivity: "+User.eventid);
                        // Check to see if user is already in an event
                        if(!User.eventid.equals("null")) {
                            Log.i("Main", "EVENTID "+ User.eventid);
                            fragment = fragment1b;
                        }
                        else {
                            fragment = fragment1;
                        }
                        break;

                    case R.id.chat_button:
                        fragment = fragment2;
                        break;

                    case R.id.map_button:
                        fragment = fragment3;
                        break;

                    default: return true;
                }
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
                return true;
            }
        });

        // Set default selection to be launched
        bottomNavigationView.setSelectedItemId(R.id.event_button);


    }


    // Remove the entire event from Firebase
    public void deleteEvent(String eventid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.child(eventid).removeValue();
    }

    @Override
    public void onBackPressed() {
        Log.i("Main", "Back pressed");
    }



}
