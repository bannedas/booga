package com.example.Booga;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LinkAccountsActivty extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LinkAccountsActivity";

    FirebaseAuth mAuth;
    CallbackManager mCallbackManager;

    EditText mLoginEditTextEmail, mLoginEditTextPassword;
    public static Button mFacebookLoginButton1;
    public static Button mEmailLoginButton1;

    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_accounts_activty);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        mLoginEditTextEmail = findViewById(R.id.mergeEditTextEmailId);
        mLoginEditTextPassword = findViewById(R.id.mergeEditTextPasswordId);
        mFacebookLoginButton1 = findViewById(R.id.mergeFacebookButtonId);
        mEmailLoginButton1 = findViewById(R.id.mergeLoginButtonId);
        mFacebookLoginButton1.setVisibility(View.GONE);
        mEmailLoginButton1.setVisibility(View.GONE);

        Bundle bun = getIntent().getExtras();
        int val = bun.getInt("VAL");

        if (val == 1) {
            mFacebookLoginButton1.setVisibility(View.VISIBLE);
        }
        if (val == 2) {
            mEmailLoginButton1.setVisibility(View.VISIBLE);
        }
        if (val == 3) {
            mEmailLoginButton1.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.mergeLoginButtonId).setOnClickListener(LinkAccountsActivty.this);
        findViewById(R.id.mergeFacebookButtonId).setOnClickListener(LinkAccountsActivty.this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void mergeFacebookToEmail() {
        /*
        This gets the text written by the user in the EditTextFields email and password.
         */
        String email = mLoginEditTextEmail.getText().toString().trim();
        String password = mLoginEditTextPassword.getText().toString().trim();

        // If the Email EditTextField is empty show an error and point where the error is by using .requestFocus
        if (email.isEmpty()) {
            mLoginEditTextEmail.setError("Email is required");
            mLoginEditTextEmail.requestFocus();
            return;
        }

        /*
         If the Email in the EditTextField does not match with an already registered Email in Firebase,
         show an error and point where the error is by using .requestFocus
         */
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mLoginEditTextEmail.setError("Please enter a valid email");
            mLoginEditTextEmail.requestFocus();
            return;
        }

        // If the Password EditTextField is empty show an error and point where the error is by using .requestFocus
        if (password.isEmpty()) {
            mLoginEditTextPassword.setError("Password is required");
            mLoginEditTextPassword.requestFocus();
            return;
        }

        // If the Password EditTextField is not long enough, show an error and point where the error is by using .requestFocus
        if (password.length() < 6) {
            mLoginEditTextPassword.setError("Minimum length of password should be 6");
            mLoginEditTextPassword.requestFocus();
            return;
        }
        //LoginProgressBar.setVisibility(View.VISIBLE);

        /*
        This code uses the entry point for Firebase and uses the signInWithEmailAndPassword method
        to log in a user which is already created in Firebase. For now it redirects to the MainScreenActivity,
        this we can just edit later.
         */

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //linkFacebookToEmail();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            linkFacebookToEmail(accessToken);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LinkAccountsActivty.this, "typo",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mergeEmailToFacebook () {
        String email = mLoginEditTextEmail.getText().toString().trim();
        String password = mLoginEditTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            mLoginEditTextEmail.setError("Email is required");
            mLoginEditTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mLoginEditTextEmail.setError("Please enter a valid email");
            mLoginEditTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mLoginEditTextPassword.setError("Password is required");
            mLoginEditTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mLoginEditTextPassword.setError("Minimum length of password should be 6");
            mLoginEditTextPassword.requestFocus();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(getApplicationContext(), SettingsPageActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LinkAccountsActivty.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void linkFacebookToEmail(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(LinkAccountsActivty.this, MainScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LinkAccountsActivty.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //When button "Sign up" is pressed, redirect to SignUpActivity
            case R.id.mergeFacebookButtonId:
                mergeEmailToFacebook();


                break;
            //When button Login is pressed, call the method mergeFacebookToEmail
            case R.id.mergeLoginButtonId:
                mergeFacebookToEmail();
                break;
        }
    }
}
