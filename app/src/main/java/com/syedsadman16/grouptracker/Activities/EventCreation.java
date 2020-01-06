package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.syedsadman16.grouptracker.R;

import java.util.Calendar;

public class EventCreation extends AppCompatActivity {
    Button goBackBtn, displayDateButton, displayTimeButton, addEventBtn;
    ImageView eventImage, locationIcon;
    TextView eventDescription, eventName, eventLocation;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDescription = findViewById(R.id.eventDescField);
        eventName = findViewById(R.id.eventNameField);
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

        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Maps Navigation

            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the contents of the fields

                // Save to Database

                //Switch Activity
            }
        });


        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //String key = database.getReference("quiz").push().getKey();

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
