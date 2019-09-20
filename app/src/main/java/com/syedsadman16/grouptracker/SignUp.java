package com.syedsadman16.grouptracker;

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
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    String email, password, firstName, fullName ,phoneNumber;
    EditText getEmailField, getPasswordField;
    Button goToRegistration, signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignUp.this, MainActivity.class));
        }

        getEmailField = findViewById(R.id.emailField);
        getPasswordField = findViewById(R.id.passwordField);
        goToRegistration = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.loginButton);

        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Intent intent = new Intent(SignUp.this, Registration.class);startActivity(intent);}
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


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Set firebase instance
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Retrieve user profile
                            String url = "https://grouptracker-ef84c.firebaseio.com/users.json";
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        //Setup as JSONObject
                                        JSONObject obj = new JSONObject(s);
                                        String id = mAuth.getUid();
                                        firstName = obj.getJSONObject(id).getString("First Name");
                                        String lastName = obj.getJSONObject(id).getString("Last Name");
                                        fullName = firstName + "" + lastName;
                                        phoneNumber = obj.getJSONObject(id).getString("Phone Number");
                                        email = obj.getJSONObject(id).getString("Email");
                                        User.fullName = fullName;
                                        User.firstName = firstName;
                                        User.email = email;
                                        User.phone = phoneNumber;

                                        startActivity(new Intent(SignUp.this, MainActivity.class));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Log.i("VolleyError", ""+volleyError);
                                    }
                             });
                            RequestQueue rQueue = Volley.newRequestQueue(SignUp.this);
                            rQueue.add(stringRequest);
                        }

                        // If sign in fails, display a message to the user.
                        else {
                            Log.i("SignIn", String.valueOf(task.getException()));
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
