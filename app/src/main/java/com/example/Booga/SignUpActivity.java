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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    EditText signUpEditTextEmail, signUpEditTextPassword;
    Button buttonSignUp, buttonLogin;
    ProgressBar signUpProgressBar;

    //This is the entry point for the Firebase Authentication SDK
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


        /*
        This method Registers the user.
         */
    private void registerUser() {

        /*
        This gets the text written by the user in the EditTextFields email and password.
         */
        String email = signUpEditTextEmail.getText().toString().trim();
        String password = signUpEditTextPassword.getText().toString().trim();

        // If the Email EditTextField is empty show an error and point where the error is by using .requestFocus
        if (email.isEmpty()) {
            signUpEditTextEmail.setError("Email is required");
            signUpEditTextEmail.requestFocus();
            return;
        }
        /*
         If the Email in the EditTextField does not match with an already registered Email in Firebase,
         show an error and point where the error is by using .requestFocus
         */
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEditTextEmail.setError("Please enter a valid email");
            signUpEditTextEmail.requestFocus();
            return;
        }
        // If the Password EditTextField is empty show an error and point where the error is by using .requestFocus
        if(password.isEmpty()) {
            signUpEditTextPassword.setError("Password is required");
            signUpEditTextPassword.requestFocus();
            return;
        }
        // If the Password EditTextField is not long enough, show an error and point where the error is by using .requestFocus
        if(password.length() < 6) {
            signUpEditTextPassword.setError("Minimum length of password should be 6");
            signUpEditTextPassword.requestFocus();
            return;
        }

        signUpProgressBar.setVisibility(View.VISIBLE);


        /*
        This code uses the entry point for Firebase and uses the createUserWithEmailAndPassword method
        to create a user so it is saved in Firebase. For now it redirects to the ProfileActivity,
        this we can just edit later.
         */
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    //If everything succeeds, redirect to ProfileActivity
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    //This method ensures that the 2 activities Login and Signup gets removed from the stack
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();

                    //If User is already registered, handle CollisionException and show a Toast message
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


    /*
    This method onClick makes sure that when you click on the buttons,
    they do what they are supposed to do.
     */
    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.buttonSignUpSignUpId:
                registerUser();
                break;

            //When button "Already have a login?" is pressed, redirect to LoginActivity
            case R.id.buttonSignUpLoginId:
                startActivity(new Intent(this, LoginActivity.class));

                break;
        }

    }
}
