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
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static com.syedsadman16.grouptracker.Models.User.email;

public class EventCreation extends AppCompatActivity {
    Button goBackBtn, displayDateButton, displayTimeButton, addEventBtn;
    ImageView eventImage, locationIcon;
    TextView eventDescription, eventName, eventLocation, eventPassword, chooseImageTextView;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        Firebase.setAndroidContext(this);

        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDescription = findViewById(R.id.eventDescField);
        eventName = findViewById(R.id.eventNameField);
        eventPassword = findViewById(R.id.eventPassword);
        eventLocation = findViewById(R.id.eventLocationField);
        displayDateButton = findViewById(R.id.displayDateButton);
        displayTimeButton = findViewById(R.id.displayTimeButton);
        goBackBtn = findViewById(R.id.backBtn);
        addEventBtn = findViewById(R.id.addEventBtn);
        locationIcon = findViewById(R.id.locationIcon);
        chooseImageTextView = findViewById(R.id.chooseImageTextView);

        eventLocation.setInputType(InputType.TYPE_NULL);

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

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), 2);
            }
        });

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Selects image and converts to Bitmap
                selectImage();
            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eventName.getText().toString().equals("")){
                    eventName.setError("Required");
                }
                else if(displayDateButton.getText().toString().equals("Choose Date")){
                    displayDateButton.setError("Required");
                }
                else if(displayTimeButton.equals("00:00")){
                    displayTimeButton.setError("Required");
                }
                else if(eventLocation.getText().toString().equals("")){
                    displayTimeButton.setError("Required");
                } else {
                    // Save to Database
                    pushToFirebase(User.fullName, displayDateButton.getText().toString(), eventDescription.getText().toString(),
                            User.bitmap, eventLocation.getText().toString(), eventName.getText().toString(),
                            eventPassword.getText().toString(), displayTimeButton.getText().toString(), User.uid);
                    Log.i("EventCreation", User.bitmap);
                    // Update eventid in Firebase
                    changeUserFirebase();
                    Log.i("EventCreation", "Created EventID" + User.eventid);
                    // Go back to Main Activity
                    startActivity(new Intent(EventCreation.this, MainActivity.class));
                    finish();
                }
            }
        });


    }

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
                        eventLocation.setText(location);
                    }
                    break;
            }
        }
    }

    // Creates a new event with given details
    public void pushToFirebase(String createdBy, String Date, String Description, String Image,
                                String Location, String Name, String Password, String Time, String uid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        // Generate a UID
        String key = reference.push().getKey();
        // Set parent UID
        reference.child(key).setValue(key);
        // Push child references
        reference.child(key).child("CreatedBy").setValue(createdBy); // Name of owner
        reference.child(key).child("Date").setValue(Date);
        reference.child(key).child("Description").setValue(Description);
        reference.child(key).child("Location").setValue(Location);
        reference.child(key).child("Name").setValue(Name); // name of Event
        reference.child(key).child("Password").setValue(Password);
        reference.child(key).child("Time").setValue(Time);
        reference.child(key).child("uid").setValue(uid); // ID of the owner
        reference.child(key).child("eventid").setValue(key); // event id, used to reference in EventViewer
        reference.child(key).child("Members").child(User.uid).setValue(User.uid); // Members list
        reference.child(key).child("Image").setValue(Image);

        // Complete
        Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();
        // Change user eventid (used to auto join event)
        User.eventid = key;
        Log.i("EventCreation", "Pushed EventID" + User.eventid);
    }

    // Changes eventid status for a user
    public void changeUserFirebase(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("eventid").setValue(User.eventid);
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
