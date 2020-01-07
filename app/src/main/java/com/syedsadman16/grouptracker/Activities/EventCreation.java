package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.util.Calendar;

import static com.syedsadman16.grouptracker.Models.User.email;

public class EventCreation extends AppCompatActivity {
    Button goBackBtn, displayDateButton, displayTimeButton, addEventBtn;
    ImageView eventImage, locationIcon;
    TextView eventDescription, eventName, eventLocation, eventPassword;
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
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), 1);
            }
        });


        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save to Database
                pushToFirebase(User.fullName, displayDateButton.getText().toString(), eventDescription.getText().toString(),
                        "null", eventLocation.getText().toString(), eventName.getText().toString(),
                        eventPassword.getText().toString(), displayTimeButton.getText().toString(), User.uid);
                // Update eventid in Firebase
                changeUserFirebase();
                // Go back to Main Activity
                startActivity(new Intent(EventCreation.this, MainActivity.class));
                finish();
            }
        });


    }

    public void pushToFirebase(String createdBy, String Date, String Description, String Image,
                                String Location, String Name, String Password, String Time, String uid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        // Generate a UID
        String key = reference.push().getKey();
        // Set parent UID
        reference.child(key).setValue(key);
        // Push child references
        reference.child(key).child("CreatedBy").setValue(createdBy);
        reference.child(key).child("Date").setValue(Date);
        reference.child(key).child("Description").setValue(Description);
        reference.child(key).child("Image").setValue(Image);
        reference.child(key).child("Location").setValue(Location);
        reference.child(key).child("Name").setValue(Name);
        reference.child(key).child("Password").setValue(Password);
        reference.child(key).child("Time").setValue(Time);
        reference.child(key).child("uid").setValue(uid); // Owner
        reference.child(key).child("eventid").setValue(key); // Reference the event in EventViewer
        reference.child(key).child("Members").child(User.uid).setValue(User.uid); // Members list

        // Complete
        Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();
        // Change user eventid to auto join event
        User.eventid = key;
    }

    public void changeUserFirebase(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("eventid").setValue(User.eventid);
    }


    // Retrieving MapsActivtiy intent info
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String location = data.getStringExtra("location");
                Log.i("EVentCreation", location);
                eventLocation.setText(location);
            }
        }
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
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
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
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }


    /*
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        MapDialogFragment editNameDialogFragment = MapDialogFragment.newInstance("Autocomplete Map");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
    */
}
