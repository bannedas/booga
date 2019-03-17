package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private final int SPLASH_DISPLAY_LENGTH = 1000;

        FirebaseAuth mAuth;
        EditText loginEditTextEmail, loginEditTextPassword;
        //ProgressBar LoginProgressBar;
        Button facebookLoginButton;
        TextView signUpTextView;
        CallbackManager fbCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbCallback = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mAuth = FirebaseAuth.getInstance();
        //LoginProgressBar = findViewById(R.id.loginProgressBarId);
        loginEditTextEmail = findViewById(R.id.editTextEmailId);
        loginEditTextPassword = findViewById(R.id.editTextPasswordId);
        signUpTextView = findViewById(R.id.textViewSignUpId);
        facebookLoginButton = findViewById(R.id.facebookButtonId);

        findViewById(R.id.textViewSignUpId).setOnClickListener(this);
        findViewById(R.id.buttonLoginId).setOnClickListener(this);

        printHashKey(getApplicationContext());


        // we neeed something like this to check if user is logged in already when launching app
//        if(mAuth.getCurrentUser() != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                startActivity(new Intent(this, ProfileActivity.class),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//            }
//            return;
//        }

        // Callback registration
        LoginManager.getInstance().registerCallback(fbCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
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
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. getCurrentUser", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User was successfully logged in");
                            // Sign in success, update UI with the signed-in user's information
                        } else {
                             //If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. signInWithCredential", Toast.LENGTH_SHORT).show();
                        }
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
        //LoginProgressBar.setVisibility(View.VISIBLE);

        /*
        This code uses the entry point for Firebase and uses the signInWithEmailAndPassword method
        to log in a user which is already created in Firebase. For now it redirects to the ProfileActivity,
        this we can just edit later.
         */
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. LINKING 2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //WE NEED TO USE THIS CODE TO CHECK IF THE ACCOUNTS ARE ALREADY LINKED, SOMEHOW.
//        final FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        FirebaseUser currentUser = task.getResult().getUser();
//                        // Merge prevUser and currentUser accounts and data
//                        currentUser = prevUser;
//                        // ...
//                    }
//                });
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //LoginProgressBar.setVisibility(View.GONE);

                //If Email and password is correct, Login the user and redirect to ProfileActivity
                if (task.isSuccessful()) {
                    Log.d(TAG, "User was successfully logged in");
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
            case R.id.textViewSignUpId:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(this, SignUpActivity.class),
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
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