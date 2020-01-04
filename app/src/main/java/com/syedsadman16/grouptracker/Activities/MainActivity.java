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

        // define your fragments here
        final Fragment fragment1 = new EventsFragment();
        final Fragment fragment1b = new ViewEventsFragment();
        final Fragment fragment2 = new ChatFragment();
        final Fragment fragment3 = new MapsFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.event_button:
                        Log.i("Main", User.eventid);
                        // If the user is not in any event, take to default events page
                        if(User.eventid.equals("null")) {
                            fragment = fragment1;
                        }
                        // else take them to detailed events page
                        else {
                            fragment = fragment1b;
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

}