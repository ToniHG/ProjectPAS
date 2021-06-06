package com.example.myapppas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText email_data,pass_data;
    Button button_register,button_login;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Data
        email_data = findViewById(R.id.editTextEmailAddress);
        pass_data = findViewById(R.id.editTextPassword);

        //Buttons
        button_register = findViewById(R.id.registerButton);
        button_login = findViewById(R.id.logInButton);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Check if the user is connected
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        //Registration
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_data.getText().toString().trim();
                String password = pass_data.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    email_data.setError("Empty email.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    pass_data.setError("Empty password.");
                    return;
                }
                if(password.length() < 6){
                    pass_data.setError("The password must have 6 or moro characters.");
                    return;
                }
                //Registration in Firebase
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "New user created succesfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error creating the user:" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Change to registration menu
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }

}