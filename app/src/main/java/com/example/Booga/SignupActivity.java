package com.example.Booga;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {


    EditText signUpEditTextEmail, signUpEditTextPassword;
    Button buttonSignUp, buttonLogin;
    ProgressBar signUpProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        signUpProgressBar =  findViewById(R.id.signUpProgressBarId);
        signUpEditTextEmail = findViewById(R.id.editTextSignUpEmailId);
        signUpEditTextPassword = findViewById(R.id.editTextSignUpPasswordId);




        buttonSignUp = findViewById(R.id.buttonSignUpSignUpId);
        buttonSignUp.setOnClickListener(this);
        buttonLogin = findViewById(R.id.buttonSignUpLoginId);
        buttonLogin.setOnClickListener(this);




    }

    private void registerUser() {
        String email = signUpEditTextEmail.getText().toString().trim();
        String password = signUpEditTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            signUpEditTextEmail.setError("Email is required");
            signUpEditTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEditTextEmail.setError("Please enter a valid email");
            signUpEditTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            signUpEditTextPassword.setError("Password is required");
            signUpEditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            signUpEditTextPassword.setError("Minimum length of password should be 6");
            signUpEditTextPassword.requestFocus();
            return;
        }

        signUpProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.buttonSignUpSignUpId:
                registerUser();
                break;

            case R.id.buttonSignUpLoginId:
                startActivity(new Intent(this, LoginActivity.class));

                break;
        }

    }
}
