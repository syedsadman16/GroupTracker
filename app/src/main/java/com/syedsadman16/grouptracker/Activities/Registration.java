package com.syedsadman16.grouptracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.syedsadman16.grouptracker.R;

import org.json.JSONObject;


public class Registration extends AppCompatActivity {

    String firstName, lastName, password, email;
    String phoneNumber;
    EditText firstNameField, lastNameField, passwordField, emailField, numberField;
    Button exitToLogin, submitReg;
    FirebaseAuth auth;

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
        numberField = findViewById(R.id.phoneField);

        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();

        //Cancel registration
        exitToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields(); goToSignIn();
            }
        });

        //Registers user and exits to Sign In
        submitReg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               registerUser();
           }
       });
    }

    public void registerUser(){
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        phoneNumber = numberField.getText().toString();

        if(firstName.equals("")){
            firstNameField.setError("Required");
        }
        else if(lastName.equals("")){
            lastNameField.setError("Required");
        }
        else if(password.equals("")){
            passwordField.setError("Required");
        }
        else if(password.length() < 6){
            passwordField.setError("Minimum 6 characters");
        }
        else if(phoneNumber.equals("")){
            numberField.setError("Required");
        }
        else if(!isValidEmail(email)) {
            emailField.setError("Not a valid email");
        }
        else {
            // Instantiate the RequestQueue.
            RequestQueue rQueue = Volley.newRequestQueue(this);

            // Checks JSON file from Realtime Database
            String url = "https://grouptracker-ef84c.firebaseio.com/users.json";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                // If user doesn't exist, create it
                                if (!obj.has(email)) {
                                    authenticateUser();
                                } else {
                                    emailField.setError("User already exists");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i("JSON", "Error");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Volley", "Unable to process StringRequest");
                }
            });

            rQueue.add(stringRequest);
        }
    }

    // Checks if email matches requirements
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Creates user in Firebase with email and password
    public void authenticateUser(){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Auth", "Successs!");

                            // Auth already created user, now get custom UID to make info abt user
                            String uid = auth.getCurrentUser().getUid();

                            // Add user details to Realtime Database
                            Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
                            reference.child("users").setValue(uid);
                            reference.child(uid).child("Email").setValue(email);
                            reference.child(uid).child("password").setValue(password);
                            reference.child(uid).child("First Name").setValue(firstName);
                            reference.child(uid).child("Last Name").setValue(lastName);
                            reference.child(uid).child("Phone Number").setValue(phoneNumber);
                            reference.child(uid).child("eventid").setValue("null");

                            // Complete Registration
                            Toast.makeText(getApplicationContext(), "Registered User!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            clearFields();
                            goToSignIn();

                        } else {
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Log.i("Auth", "Failed Registration: "+e.getMessage());
                            Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Intent to go to Sign In
    public void goToSignIn(){
        Intent intent = new Intent(Registration.this, SignIn.class);
        startActivity(intent);
    }

    // Clears the text fields
    public void clearFields(){
        firstNameField.setText("");lastNameField.setText("");passwordField.setText("");emailField.setText("");numberField.setText("");
    }


}
