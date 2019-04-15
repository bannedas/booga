package com.example.Booga;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static com.example.Booga.LinkAccountsActivty.mEmailLoginButton1;

public class SettingsPageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SettingsActivity";

    CallbackManager mCallbackManager;
    Button mButtonMergeEmail, mButtonMergeFacebook;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);


        mCallbackManager = CallbackManager.Factory.create();

        mButtonMergeFacebook = findViewById(R.id.buttonMergeFacebookWithEmailId);
        mButtonMergeEmail = findViewById(R.id.buttonMergeEmailWithFacebookId);
        mButtonMergeEmail.setOnClickListener(this);
        mButtonMergeFacebook.setVisibility(View.VISIBLE);
        mButtonMergeEmail.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().getProviders().contains("facebook.com")) {
            mButtonMergeFacebook.setVisibility(View.GONE);
        }
        if (mAuth.getCurrentUser().getProviders().contains("password")) {
            mButtonMergeEmail.setVisibility(View.GONE);
        }
        mAuth = FirebaseAuth.getInstance();




        // Callback registration
        mButtonMergeFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mButtonMergeFacebook.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(SettingsPageActivity.this, Arrays.asList("public_profile", "user_friends"));
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
                            Intent intent = new Intent(SettingsPageActivity.this, MainScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Intent intent = new Intent(SettingsPageActivity.this, LinkAccountsActivty.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("VAL", 2);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.buttonMergeEmailWithFacebookId:

                Intent intent = new Intent(getApplicationContext(), LinkAccountsActivty.class);
                Bundle bundle = new Bundle();
                bundle.putInt("VAL", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
        startActivity(intent);
    }
}
