package com.syedsadman16.grouptracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.syedsadman16.grouptracker.R;

public class EventCreation extends AppCompatActivity {
    Button goBackBtn;
    ImageView eventImage;
    TextView eventDescription, eventName, eventLocation, eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDescription = findViewById(R.id.eventDescField);
        eventName = findViewById(R.id.eventNameField);
        eventLocation = findViewById(R.id.eventLocationField);
        eventTime = findViewById(R.id.eventTimeField);


        goBackBtn = findViewById(R.id.backBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventCreation.this, MainActivity.class));
            }
        });

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //String key = database.getReference("quiz").push().getKey();

    }
}
