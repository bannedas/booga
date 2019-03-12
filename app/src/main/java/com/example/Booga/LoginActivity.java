package com.example.Booga;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EMAIL = "email";
        FirebaseAuth mAuth;
        EditText loginEditTextEmail, loginEditTextPassword;
        ProgressBar LoginProgressBar;
        LoginButton loginButton;
    private CallbackManager fbCallback = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();

        LoginProgressBar = findViewById(R.id.loginProgressBarId);
        loginEditTextEmail = findViewById(R.id.editTextEmailId);
        loginEditTextPassword = findViewById(R.id.editTextPasswordId);

        findViewById(R.id.buttonSignUpId).setOnClickListener(this);
        findViewById(R.id.buttonLoginId).setOnClickListener(this);

            printHashKey(getApplicationContext());


        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");



        // Callback registration
        loginButton.registerCallback(fbCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }


        });


        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } //else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(FacebookLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        //}

                        // ...
                    }
                });
    }





    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
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
                /*This method ensures that the 2 activities Login and Signup gets removed from the stack,
                    which means when you are logged in and press the back button, you will not get redirected
                    to the signup screen again, but will exit the app instead
                 */
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallback.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}