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
import com.syedsadman16.grouptracker.Models.Members;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.util.ArrayList;
import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    Context context;
    ArrayList<Members> members;

    public UserListAdapter(Context context, ArrayList<Members> membersArrayList) {
        this.context = context;
        this.members = membersArrayList;
    }


    // Clean all elements of the recycler
    public void clear() {
        members.clear();
        notifyDataSetChanged();
    }


    // Defining references to components in RecyclerView layout
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView user_image;
        public TextView user_name;
        public TextView user_distance;


        public ViewHolder(View itemView) {
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_distance = (TextView) itemView.findViewById(R.id.user_distance);

        }


        public void populateView(final Members member) {
            user_name.setText(member.getName());
            user_distance.setText("Write Function To Calculate Distance");


            // Take base64 string and decode into byte array
            //byte[] imageBytes2 = Base64.decode(events.getEventPicture(), Base64.DEFAULT);
            // Convert the byte array into a Bitmap
            //Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.length);
            //eventImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //eventImage.setImageBitmap(decodedImage);

        }

        // Do something with the users distance
        public void updateFirebase(){

            Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
            reference.child(User.uid).child("eventid").setValue(User.eventid);
        }
        public void updateMembersFirebase(){
            Firebase eventReference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
            eventReference.child(User.eventid).child("Members").child(User.uid).child("uid").setValue(User.uid); // Members list
            eventReference.child(User.eventid).child("Members").child(User.uid).child("name").setValue(User.firstName);
        }


    }

    // Inflating the layout file and return it to view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(context).inflate(R.layout.user_list_recycler_layout, parent, false);
        return new ViewHolder(postView);
    }


    // Get data from position and put into view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Members member = members.get(position);
        holder.populateView(member);
    }

    // Count of total events
    @Override
    public int getItemCount() {
        return members.size();
    }
}

