package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.google.firebase.FirebaseApp;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class EventCreation extends AppCompatActivity {
    Button goBackBtn, displayDateButton, displayTimeButton, addEventBtn;
    ImageView eventImage, locationIcon;
    TextView eventDescriptionTextView, eventNameTextView, eventLocationTextView, eventPasswordTextView, chooseImageTextView;
    String createdBy, uid, eventid, eventName, eventLocation, eventTime, eventDate, eventDescription, eventImageURL, eventPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        Firebase.setAndroidContext(this);

        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDescriptionTextView = findViewById(R.id.eventDescField);
        eventNameTextView = findViewById(R.id.eventNameField);
        eventPasswordTextView = findViewById(R.id.eventPassword);
        eventLocationTextView = findViewById(R.id.eventLocationField);
        displayDateButton = findViewById(R.id.displayDateButton);
        displayTimeButton = findViewById(R.id.displayTimeButton);
        goBackBtn = findViewById(R.id.backBtn);
        addEventBtn = findViewById(R.id.addEventBtn);
        locationIcon = findViewById(R.id.locationIcon);
        chooseImageTextView = findViewById(R.id.chooseImageTextView);

        eventLocationTextView.setInputType(InputType.TYPE_NULL);

        displayDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        displayTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventCreation.this, MainActivity.class));
            }
        });

        eventLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), 2);
            }
        });

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eventNameTextView.getText().toString().equals("")){
                    eventNameTextView.setError("Required");
                }
                else if(displayDateButton.getText().toString().equals("Choose Date")){
                    displayDateButton.setError("Required");
                }
                else if(displayTimeButton.equals("00:00")){
                    displayTimeButton.setError("Required");
                }
                else if(eventLocationTextView.getText().toString().equals("")){
                    eventLocationTextView.setError("Required");
                }
                else {
                    createdBy = User.fullName;
                    eventDate = displayDateButton.getText().toString();
                    eventDescription = eventDescriptionTextView.getText().toString();
                    eventImageURL = User.bitmap;
                    eventLocation =  eventLocationTextView.getText().toString();
                    eventName =  eventNameTextView.getText().toString();
                    eventPassword = eventPasswordTextView.getText().toString();
                    eventTime = displayTimeButton.getText().toString();
                    uid = User.uid;

                    pushToFirebase(createdBy, eventDate, eventDescription, eventImageURL, eventLocation,
                            eventName, eventPassword, eventTime, uid);

                    changeUserFirebase();
                    startActivity(new Intent(EventCreation.this, MainActivity.class));
                    finish();
                }
            }
        });

    }


    // Creates a new event with given details
    public void pushToFirebase(String createdBy, String Date, String Description, String Image,
                               String Location, String Name, String Password, String Time, String uid){

        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");

        // Generate a UID
        String key = reference.push().getKey();
        // Change Users event status
        User.eventid = key;

        // Create Events object and push it to database
        Events events = new Events(key, createdBy, Date, Description, Image, Location, Name, Password, Time, uid);
        reference.child(key).setValue(events);
        // Create a members list
        reference.child(key).child("Members").child(User.uid).child("uid").setValue(User.uid);

        Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();
    }

    // Change event status of user
    public void changeUserFirebase(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("eventid").setValue(User.eventid);
    }

    // Selects image and converts to Bitmap
    private void selectImage() {
        final CharSequence[] menuOptions = {"Take picture","Select from Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EventCreation.this);
        builder.setTitle("Choose an Event Picture");

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    // Image picker - Choosing camera
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        // Set bitmap to user => used when pushing to firebase
                        User.bitmap = convertToBase64(selectedImage);
                        // Hide the textview
                        chooseImageTextView.setVisibility(View.INVISIBLE);
                        eventImage.setImageBitmap(selectedImage);
                    }
                    break;

                case 1:
                    // image picker - Choosing Gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri contentURI = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                            User.bitmap = convertToBase64(bitmap);
                            Log.i("EventCreation", User.bitmap);
                            eventImage.setImageBitmap(bitmap);
                            chooseImageTextView.setVisibility(View.INVISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2:
                    // Location picker
                    if(resultCode == RESULT_OK) {
                        String location = data.getStringExtra("location");
                        Log.i("EVentCreation", location);
                        eventLocationTextView.setText(location);
                    }
                    break;
            }
        }
    }


    // Returns image as a string
    public String convertToBase64(Bitmap bitmap){

        // Implements an output steam for data to be written to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compress bitmap to ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // Convert to byte array
        byte[] imageBytes = baos.toByteArray();
        // Convert byte array to string
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("EEEE, MMM d, yyyy", calendar1).toString();

                displayDateButton.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void timePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                String dateText = DateFormat.format("h:mm a", calendar1).toString();
                displayTimeButton.setText(dateText);
            }
        }, hour, minute, is24HourFormat);

        timePickerDialog.show();
    }


}
