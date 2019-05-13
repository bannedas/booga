package com.example.Booga;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsPageActivity extends AppCompatActivity implements View.OnClickListener, fragment_profile.OnFragmentInteractionListener {

    public static final String TAG = "SettingsActivity";

    CallbackManager mCallbackManager;
    Button mButtonMergeEmail, mButtonMergeFacebook;
    FirebaseAuth mAuth;
    FirebaseFirestore dataBase;


    Button mbuttonSignOut;
    Button mbuttonUploadPic;
    Button mbuttonBioEdit;
    Button mbuttonChangePassword;
    Button mButtonDeleteAccount;

    EditText mEditTextChangePassword;
    EditText mEditTextMatchChangePassword;

    Switch mSwitchHideEmail;
    Switch mSwitchHidePhoneNumber;
    Button buttonSignOut;
    Button buttonUploadPic;
    Button buttonBioEdit;

    //uploading img to firebase
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        mCallbackManager = CallbackManager.Factory.create();
        dataBase = FirebaseFirestore.getInstance();

        mButtonMergeFacebook = findViewById(R.id.buttonMergeFacebookWithEmailId);
        mButtonMergeEmail = findViewById(R.id.buttonMergeEmailWithFacebookId);
        mbuttonSignOut = findViewById(R.id.button_sign_out);
        mbuttonUploadPic = findViewById(R.id.button_upload_picture);
        mbuttonBioEdit = findViewById(R.id.button_bio_edit);
        mbuttonChangePassword = findViewById(R.id.buttonChangePasswordId);
        mEditTextChangePassword = findViewById(R.id.editTextChangePasswordId);
        mEditTextMatchChangePassword = findViewById(R.id.editTextMatchChangePasswordId);
        mSwitchHideEmail = findViewById(R.id.switchMakeEmailPrivateId);
        mSwitchHidePhoneNumber = findViewById(R.id.switchMakePhonePrivateId);
        buttonSignOut = findViewById(R.id.button_sign_out);
        buttonUploadPic = findViewById(R.id.button_upload_picture);
        buttonBioEdit = findViewById(R.id.button_bio_edit);
        mButtonDeleteAccount = findViewById(R.id.buttonDeleteAccountId);

        mButtonMergeEmail.setOnClickListener(this);
        mbuttonSignOut.setOnClickListener(this);
        mbuttonUploadPic.setOnClickListener(this);
        mbuttonBioEdit.setOnClickListener(this);
        mbuttonChangePassword.setOnClickListener(this);
        mButtonDeleteAccount.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
        buttonUploadPic.setOnClickListener(this);
        buttonBioEdit.setOnClickListener(this);

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
    public void updatePassword() {
        String password = mEditTextChangePassword.getText().toString().trim();
        String confirmPassword = mEditTextMatchChangePassword.getText().toString().trim();
        if (password.isEmpty()) {
            mEditTextChangePassword.setError("Password is required");
            mEditTextChangePassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mEditTextChangePassword.setError("Minimum length of password should be 6");
            mEditTextChangePassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            mEditTextMatchChangePassword.setError(getString(Integer.parseInt("Passwords do not match")));
            mEditTextMatchChangePassword.requestFocus();
        }

        mAuth.getCurrentUser().updatePassword(password);
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
                break;

            case R.id.button_bio_edit:
                Log.d(TAG, "Clicked edit bio button");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit bio");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bioString = input.getText().toString();

                        writeBioToDatabase(bioString);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;

            case R.id.buttonChangePasswordId:
                Log.d(TAG, "change password button clicked");
                updatePassword();
                Toast.makeText(SettingsPageActivity.this, "Your password has been changed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.switchMakeEmailPrivateId:
                Log.d(TAG, "Email hide switch clicked");
                if (mSwitchHideEmail.isActivated()) {
                //HIDE THE EMAIL IN THE PROFILE
            }
                break;

            case R.id.switchMakePhonePrivateId:
                Log.d(TAG, "Phone number hide switch clicked");
                if (mSwitchHidePhoneNumber.isActivated()) {
                //HIDE THE PHONE NUBMER IN THE PROFILE
            }
                break;

            case R.id.buttonDeleteAccountId:
                Log.d(TAG, "Delete Account clicked");
                if (mAuth.getCurrentUser() != null) {
                    Log.d(TAG, "LOGGED IN");

                    FirebaseUser fireUser = mAuth.getCurrentUser();
                    assert fireUser != null;
                    final String UID = fireUser.getUid();
                    dataBase.collection("users").document(UID).delete();

                    AccessToken token = AccessToken.getCurrentAccessToken();
                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Log.d(TAG, "User account deleted.");
                                        Toast.makeText(SettingsPageActivity.this,
                                                "Your account has been deleted and you have been logged out.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SettingsPageActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }

                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    private void writeBioToDatabase(final String bioString) {
        //init db
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get uid
        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null; // check if user != null
        final String UID = fireUser.getUid(); //store user id

        //setup what to write
        Map<String, Object> dbUser = new HashMap<>();
        dbUser.put("bio", bioString);

        // Add a new document with a generated ID
        db.collection("users").document(UID)
                .set(dbUser, SetOptions.merge());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
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

            spaceRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                    startActivity(intent);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error uploading profile pic!");
                    Toast.makeText(SettingsPageActivity.this, "Error uploading", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
        intent.putExtra("isFromSettingsPage", "yes");
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}