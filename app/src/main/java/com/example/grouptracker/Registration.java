package com.example.grouptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class Registration extends AppCompatActivity {

    String firstName, lastName, password, email;
    EditText firstNameField, lastNameField, passwordField, emailField;
    Button exitToLogin, submitReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        firstNameField = findViewById(R.id.firstField);
        lastNameField = findViewById(R.id.lastField);
        passwordField = findViewById(R.id.passwordField);
        emailField = findViewById(R.id.emailField);
        exitToLogin = findViewById(R.id.cancelButton);
        submitReg = findViewById(R.id.submitReg);

        Firebase.setAndroidContext(this);

        exitToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, SignUp.class);
                startActivity(intent);
            }
        });















    }




}
