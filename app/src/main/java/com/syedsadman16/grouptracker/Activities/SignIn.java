package com.syedsadman16.grouptracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SignIn extends AppCompatActivity {

    String email, password, firstName, lastName, fullName ,phoneNumber, eventid;
    EditText getEmailField, getPasswordField;
    Button goToRegistration, signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Auto-sign in user
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Log.i("Signin", User.email + " has signed in");
            getUserJSONValues();
        }

        // Setting up buttons
        getEmailField = findViewById(R.id.emailField);
        getPasswordField = findViewById(R.id.passwordField);
        goToRegistration = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.loginButton);

        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Intent intent = new Intent(SignIn.this, Registration.class);startActivity(intent);}
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizeUser();
            }
        });

    }

    public void authorizeUser(){
        email = getEmailField.getText().toString();
        password = getPasswordField.getText().toString();

        // Auth signs in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserJSONValues();
                        }
                        // If sign in fails, display a message to the user.
                        else {
                            Log.i("SignIn", String.valueOf(task.getException()));
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getUserJSONValues(){

        // Retrieve user profile
        // Volley works in the background thread
        String url = "https://grouptracker-ef84c.firebaseio.com/users.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                try {

                    // Retrieve as JSON and get desired fields
                    JSONObject obj = new JSONObject(s);
                    String id = mAuth.getUid(); // Firebase generated UID
                    firstName = obj.getJSONObject(id).getString("First Name");
                    lastName = obj.getJSONObject(id).getString("Last Name");
                    phoneNumber = obj.getJSONObject(id).getString("Phone Number");
                    email = obj.getJSONObject(id).getString("Email");
                    eventid = obj.getJSONObject(id).getString("eventid");
                    fullName = firstName + " " + lastName;

                    // Add details for static User
                    User.fullName = fullName;
                    User.firstName = firstName;
                    User.email = email;
                    User.phone = phoneNumber;
                    User.eventid = eventid;
                    User.uid = id;
                    Log.i("SIGNIN", id);

                    Toast.makeText(getApplicationContext(), "Signing in...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignIn.this, MainActivity.class));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("VolleyError", ""+volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(SignIn.this);
        rQueue.add(stringRequest);
    }


}
