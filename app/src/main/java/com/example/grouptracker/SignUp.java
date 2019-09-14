package com.example.grouptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {

    String email, password;
    EditText getEmailField, getPasswordField;
    Button goToRegistration, signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getEmailField = findViewById(R.id.emailField);
        getPasswordField = findViewById(R.id.passwordField);
        goToRegistration = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.loginButton);

        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Registration.class);
                startActivity(intent);
            }
        });


    }
}
