package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.syedsadman16.grouptracker.Models.Events;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EventEdit extends AppCompatActivity {

    EditText editEventName, editEventDetails;
    TextView editLocation;
    ImageView editImageView;
    Button editDate, editTime, saveButton;
    String eventName, eventLocation, eventTime, eventDate, eventDescription, eventImage, eventPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        editDate = findViewById(R.id.editDate);
        editEventDetails = findViewById(R.id.editEventDetails);
        editEventName = findViewById(R.id.editEventName);
        editTime = findViewById(R.id.editTime);
        editImageView = findViewById(R.id.editImageView);
        editLocation = findViewById(R.id.editLocation);
        saveButton = findViewById(R.id.saveButton);

        Intent edit = getIntent();
        eventName = edit.getExtras().getString("Title");
        eventDate = edit.getExtras().getString("Date");
        eventLocation = edit.getExtras().getString("Location");
        eventTime = edit.getExtras().getString("Time");
        eventDescription = edit.getExtras().getString("Description");
        eventImage = edit.getExtras().getString("Image");

        User.bitmap = eventImage;
        byte[] imageBytes2 = Base64.decode(eventImage, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.length);
        editImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        editImageView.setImageBitmap(decodedImage);

        editDate.setText(eventDate);
        editEventDetails.setText(eventDescription);
        editEventName.setText(eventName);
        editTime.setText(eventTime);
        editLocation.setText(eventLocation);

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), 2);
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDate = editDate.getText().toString();
                eventName = editEventName.getText().toString();
                eventLocation = editLocation.getText().toString();
                eventTime = editTime.getText().toString();
                eventDescription = editEventDetails.getText().toString();
                eventImage = User.bitmap;
                saveToFirebase(eventDate, eventDescription, eventImage, eventLocation, eventName, eventTime);
                startActivity(new Intent(EventEdit.this, MainActivity.class));
                finish();
            }
        });

    }

    // Creates a new event with given details
    public void saveToFirebase(String Date, String Description, String Image,
                               String Location, String Name, String Time){

        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/");
        // Create Events object and push it to database
       // Events editEvent = new Events(Date, Description, Image, Location, Name, Time);
        reference.child(User.eventid).child("eventDate").setValue(Date);
        reference.child(User.eventid).child("eventDescription").setValue(Description);
        reference.child(User.eventid).child("eventImageURL").setValue(Image);
        reference.child(User.eventid).child("eventLocation").setValue(Location);
        reference.child(User.eventid).child("eventName").setValue(Name);
        reference.child(User.eventid).child("eventTime").setValue(Time);

        Toast.makeText(getApplicationContext(), "All Changes Saved", Toast.LENGTH_SHORT).show();
    }

    // Selects image and converts to Bitmap
    private void selectImage() {
        final CharSequence[] menuOptions = {"Take picture","Select from Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEdit.this);
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
                        User.bitmap = convertToBase64(selectedImage);
                        editImageView.setImageBitmap(selectedImage);
                    }
                    break;

                case 1:
                    // image picker - Choosing Gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri contentURI = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                            User.bitmap = convertToBase64(bitmap);
                            editImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2:
                    // Location picker
                    if(resultCode == RESULT_OK) {
                        String location = data.getStringExtra("location");
                        editLocation.setText(location);
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

                editDate.setText(dateText);
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
                editTime.setText(dateText);
            }
        }, hour, minute, is24HourFormat);

        timePickerDialog.show();
    }

}
