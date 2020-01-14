package com.syedsadman16.grouptracker.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.util.ArrayList;
import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    Context context;
    ArrayList<Events> events;

    public EventsAdapter(Context context, ArrayList<Events> events) {
        this.context = context;
        this.events = events;
    }


    // Clean all elements of the recycler
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }


    // Defining references to components in RecyclerView layout
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView eventImage;
        public TextView eventName;
        public TextView eventDate;
        public TextView eventHost;
        public Button joinButton;


        public ViewHolder(View itemView) {
            super(itemView);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventHost = (TextView) itemView.findViewById(R.id.eventHost);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
            joinButton = itemView.findViewById(R.id.joinButton);
        }


        public void populateView(final Events events) {
            eventName.setText(events.getEventName());
            eventHost.setText("Created by " + events.getHostName());
            eventDate.setText(events.getEventDate());
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User.eventid = events.getEventid();
                    updateFirebase();
                    updateMembersFirebase();
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
            // Take base64 string and decode into byte array
            byte[] imageBytes2 = Base64.decode(events.getEventPicture(), Base64.DEFAULT);
            // Convert the byte array into a Bitmap
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.length);
            eventImage.setScaleType(ImageView.ScaleType.FIT_XY);
            eventImage.setImageBitmap(decodedImage);

        }

        public void updateFirebase(){
            // Update users eventid
            Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
            reference.child(User.uid).child("eventid").setValue(User.eventid);
        }

        public void updateMembersFirebase(){
            // Add user to members list
            Firebase eventReference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
            eventReference.child(User.eventid).child("Members").child(User.uid).child("uid").setValue(User.uid); // Members list
        }


    }

    // Inflating the layout file and return it to view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(context).inflate(R.layout.event_recycler_layout, parent, false);
        return new ViewHolder(postView);
    }


    // Get data from position and put into view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Events event = events.get(position);
        holder.populateView(event);
    }

    // Count of total events
    @Override
    public int getItemCount() {
        return events.size();
    }
}

