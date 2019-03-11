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

    private void userLogin() {

        String email = loginEditTextEmail.getText().toString().trim();
        String password = loginEditTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            loginEditTextEmail.setError("Email is required");
            loginEditTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEditTextEmail.setError("Please enter a valid email");
            loginEditTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            loginEditTextPassword.setError("Password is required");
            loginEditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            loginEditTextPassword.setError("Minimum length of password should be 6");
            loginEditTextPassword.requestFocus();
            return;
        }

        LoginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                LoginProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.buttonSignUpId:

                startActivity(new Intent(this, SignupActivity.class));

                break;

            case R.id.buttonLoginId:

                userLogin();
                break;

        }

    }
}
