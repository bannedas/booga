package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

        FirebaseAuth mAuth;
        CallbackManager mCallbackManager;
        FirebaseAuth.AuthStateListener mAuthListener;

        EditText mLoginEditTextEmail, mLoginEditTextPassword;
        Button mFacebookLoginButton;
        TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        mLoginEditTextEmail = findViewById(R.id.editTextEmailId);
        mLoginEditTextPassword = findViewById(R.id.editTextPasswordId);
        mSignUpTextView = findViewById(R.id.textViewSignUpId);
        mFacebookLoginButton = findViewById(R.id.facebookButtonId);

        findViewById(R.id.textViewSignUpId).setOnClickListener(this);
        findViewById(R.id.buttonLoginId).setOnClickListener(this);

        printHashKey(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


// Callback registration
        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mFacebookLoginButton.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSucces:" + loginResult);

                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook:Error");
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null) {
//            updateUI();
//        }
    }

    private void updateUI() {
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);

    }



    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Intent intent = new Intent(LoginActivity.this, LinkAccountsActivty.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("VAL", 3);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
    public void userLogin() {
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
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
            //When button Login is pressed, call the method mergeFacebookToEmail
            case R.id.buttonLoginId:
                userLogin();
                break;
            case R.id.forgotPasswordId:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(this, ResetPasswordActivity.class),
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
                break;
        }
    }
}