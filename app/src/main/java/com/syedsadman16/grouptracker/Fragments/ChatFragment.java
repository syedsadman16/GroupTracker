package com.syedsadman16.grouptracker.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.syedsadman16.grouptracker.Adapters.MessagesAdapter;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.Members;
import com.syedsadman16.grouptracker.Models.Message;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.util.ArrayList;


public class ChatFragment extends Fragment {
    EditText inputField;
    ImageButton send;
    RecyclerView chatRecylerView;
    ArrayList<Message> messageArray = new ArrayList<>();
    String eventChatId = "null";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    // After View has been inflated, reference all the methods that need to be called
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        inputField = view.findViewById(R.id.messageInputField);
        send = view.findViewById(R.id.sendButton);
        getEventChatID();
        chatRecylerView = view.findViewById(R.id.messages_recycler_view);
        chatRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final MessagesAdapter adapter = new MessagesAdapter(getContext(), messageArray);
        chatRecylerView.setAdapter(adapter);


        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/chat/"+eventChatId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    String userid = child.child("userid").getValue().toString();
                    String messageFieldOutput = child.child("message").getValue().toString();
                    String senderName = child.child("senderName").getValue().toString();
                    messageArray.add(new Message(userid, messageFieldOutput, senderName, "--:--"));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message(User.uid, inputField.getText().toString(), User.firstName, "--:--");
                messageArray.add(msg);
                postChatMessages(msg);
                adapter.notifyDataSetChanged();
                inputField.setText("");
            }
        });
    }

    public void getChatMessages(final MessagesAdapter adapter){

    }

    public void postChatMessages(final Message msg){

        // Reference chat for this specific event
        Firebase chat_reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/chat/"+eventChatId);
        // Generate custom id for each message
        String key = chat_reference.push().getKey();
        chat_reference.child(key).setValue(msg);

    }

    public void getEventChatID() {

        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatID = dataSnapshot.child(User.eventid).child("chatId").getValue().toString();
                eventChatId = chatID;
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }



}
