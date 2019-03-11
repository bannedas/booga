package com.example.Booga;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

        FirebaseAuth mAuth;
        EditText loginEditTextEmail, loginEditTextPassword;
        ProgressBar LoginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        LoginProgressBar =  findViewById(R.id.loginProgressBarId);
        loginEditTextEmail = findViewById(R.id.editTextEmailId);
        loginEditTextPassword = findViewById(R.id.editTextPasswordId);

        findViewById(R.id.buttonSignUpId).setOnClickListener(this);
        findViewById(R.id.buttonLoginId).setOnClickListener(this);
    }

    /*
    This method is used to login the user
     */
    private void userLogin() {

        /*
        This gets the text written by the user in the EditTextFields email and password.
         */
        String email = loginEditTextEmail.getText().toString().trim();
        String password = loginEditTextPassword.getText().toString().trim();

        // If the Email EditTextField is empty show an error and point where the error is by using .requestFocus
        if (email.isEmpty()) {
            loginEditTextEmail.setError("Email is required");
            loginEditTextEmail.requestFocus();
            return;
        }

        /*
         If the Email in the EditTextField does not match with an already registered Email in Firebase,
         show an error and point where the error is by using .requestFocus
         */
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEditTextEmail.setError("Please enter a valid email");
            loginEditTextEmail.requestFocus();
            return;
        }

        // If the Password EditTextField is empty show an error and point where the error is by using .requestFocus
        if(password.isEmpty()) {
            loginEditTextPassword.setError("Password is required");
            loginEditTextPassword.requestFocus();
            return;
        }

        // If the Password EditTextField is not long enough, show an error and point where the error is by using .requestFocus
        if(password.length() < 6) {
            loginEditTextPassword.setError("Minimum length of password should be 6");
            loginEditTextPassword.requestFocus();
            return;
        }

        LoginProgressBar.setVisibility(View.VISIBLE);

        /*
        This code uses the entry point for Firebase and uses the signInWithEmailAndPassword method
        to log in a user which is already created in Firebase. For now it redirects to the ProfileActivity,
        this we can just edit later.
         */
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                LoginProgressBar.setVisibility(View.GONE);

                //If Email and password is correct, Login the user and redirect to ProfileActivity
                if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                //This method ensures that the 2 activities Login and Signup gets removed from the stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

            //When button "Sign up" is pressed, redirect to SignUpActivity
            case R.id.buttonSignUpId:

                startActivity(new Intent(this, SignUpActivity.class));
                break;

            //When button Login is pressed, call the method userLogin
            case R.id.buttonLoginId:

                userLogin();
                break;

        }
    }
}
