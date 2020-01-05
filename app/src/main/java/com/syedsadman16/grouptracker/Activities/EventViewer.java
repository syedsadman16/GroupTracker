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
    String eventid, date, description, image, location, name ,password, createdBy, uid;

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

        // Retrieve user profile
        String url = "https://grouptracker-ef84c.firebaseio.com/events.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    // Need an iterator to return keys
                    Iterator i = jsonObject.keys();
                    while (i.hasNext()) {

                        eventid = i.next().toString();
                        name = jsonObject.getJSONObject(eventid).getString("Name");
                        date = jsonObject.getJSONObject(eventid).getString("Date");
                        description = jsonObject.getJSONObject(eventid).getString("Description");
                        image = jsonObject.getJSONObject(eventid).getString("Image");
                        location = jsonObject.getJSONObject(eventid).getString("Location");
                        password = jsonObject.getJSONObject(eventid).getString("Password");
                        createdBy = jsonObject.getJSONObject(eventid).getString("CreatedBy");
                        uid = jsonObject.getJSONObject(eventid).getString("uid");

                        Log.i("EventViewer", name+""+date+""+location+""+password+""+uid);

                        Events event = new Events(name, createdBy, date);
                        event.setEventDescription(description);
                        event.setEventImageURL(image);
                        event.setEventLocation(location);
                        event.setPassword(password);
                        event.setUid(uid);
                        eventsArrayList.add(event);

                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("VolleyError", ""+volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(stringRequest);

    }




}
