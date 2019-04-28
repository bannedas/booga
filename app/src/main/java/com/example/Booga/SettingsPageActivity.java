package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;

import static com.example.Booga.LinkAccountsActivty.mEmailLoginButton1;

public class SettingsPageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SettingsActivity";

    CallbackManager mCallbackManager;
    Button mButtonMergeEmail, mButtonMergeFacebook;
    FirebaseAuth mAuth;

    Button buttonSignOut;
    Button buttonUploadPic;

    //uploading img to firebase
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);


        mCallbackManager = CallbackManager.Factory.create();

        mButtonMergeFacebook = findViewById(R.id.buttonMergeFacebookWithEmailId);
        mButtonMergeEmail = findViewById(R.id.buttonMergeEmailWithFacebookId);
        buttonSignOut = findViewById(R.id.button_sign_out);
        buttonUploadPic = findViewById(R.id.button_upload_picture);

        mButtonMergeEmail.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
        buttonUploadPic.setOnClickListener(this);

        imageView = findViewById(R.id.imgView);

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

            //When Sign up button is pressed, call the method registerUser
            case R.id.button_sign_out:
                Log.d(TAG, "User signed out");
                intent = new Intent(this, LoginActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    startActivity(intent);
                }
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                break;

            case R.id.button_upload_picture:
                Log.d(TAG, "Clicked upload picture button");
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
                // Points to user_photo
                StorageReference imagesRef = storageRef.child("user_photo");

                // Get User ID
                FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
                assert fireUser != null;
                final String UID = fireUser.getUid(); //store user id

                // spaceRef now points to "users/userID.jpg"
                StorageReference spaceRef = imagesRef.child(UID + ".jpg");

                spaceRef.putFile(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
        startActivity(intent);
    }
}
