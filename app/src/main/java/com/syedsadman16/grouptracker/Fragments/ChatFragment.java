package com.syedsadman16.grouptracker.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.syedsadman16.grouptracker.Activities.EventEdit;
import com.syedsadman16.grouptracker.Adapters.MessagesAdapter;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.Members;
import com.syedsadman16.grouptracker.Models.Message;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {
    EditText inputField;
    ImageButton send, imageBtn;
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
        imageBtn = view.findViewById(R.id.imageButton);
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
                    messageArray.add(new Message(userid, messageFieldOutput, senderName, "--:--", false));
                }
                chatRecylerView.scrollToPosition(adapter.getItemCount()-1);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message(User.uid, inputField.getText().toString(), User.firstName, "--:--", false);
                messageArray.add(msg);
                postChatMessages(msg);
                adapter.notifyDataSetChanged();
                inputField.setText("");
                chatRecylerView.scrollToPosition(adapter.getItemCount()-1);
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                adapter.notifyDataSetChanged();
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


    // Selects image and converts to Bitmap
    private void selectImage() {
        final CharSequence[] menuOptions = {"Take picture","Select from Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose attachment");

        builder.setItems(menuOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (menuOptions[item].equals("Take picture")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (menuOptions[item].equals("Select from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                } else if (menuOptions[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                // Add option to add map image
            }
        });
        builder.show();
    }

    // When user is done with intent and comes back to this activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    // Image picker - Choosing camera
                    if (resultCode == RESULT_OK && data != null) {
                        final Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

                        ImageView image = new ImageView(getContext());
                        image.setImageBitmap(selectedImage);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).
                                setMessage("Image Preview").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Message msg = new Message(User.uid, convertToBase64(selectedImage), User.firstName, "--:--", true);
                                        messageArray.add(msg);
                                        //postChatMessages(msg);
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                        builder.create().show();
                    }
                    break;

                case 1:
                    // image picker - Choosing Gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri contentURI = data.getData();
                        try {
                            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            ImageView image = new ImageView(getContext());
                            image.setImageBitmap(bitmap);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).
                                    setMessage("Image Preview").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Message msg = new Message(User.uid, convertToBase64(bitmap), User.firstName, "--:--", true);
                                            messageArray.add(msg);
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                            builder.create().show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
            }
        }
    }


    // Returns image as a string
    public String convertToBase64(Bitmap bitmap) {


        // Implements an output steam for data to be written to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compress bitmap to ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // Convert to byte array
        byte[] imageBytes = baos.toByteArray();
        // Convert byte array to string
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;
        /*
        FirebaseStorage storage = FirebaseStorage.getInstance();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = storage.getReference();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("ChatAc", "Fail");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                String url =  taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                Log.i("ChatAc", "Done");
            }
        });
        */
    }







}
