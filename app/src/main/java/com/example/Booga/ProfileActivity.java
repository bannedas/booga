package com.example.Booga;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            updateUI();

        }
    }

    private void updateUI() {
        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null; // check if user != null
        final String UID = fireUser.getUid(); //store user id

        profilePicture = findViewById(R.id.imageViewProfilePicture);

        profilePicture.setImageBitmap(getFacebookProfilePicture("bannedas"));


    }
    //doesnt really work
    public static Bitmap getFacebookProfilePicture(String userID){
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onClick(View view) {

        switch(view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.buttonSignOut:
                Log.d(TAG, "User signed out");
                startActivity(new Intent(this, LoginActivity.class));
                mAuth.signOut();
                LoginManager.getInstance().logOut();


                break;
        }
    }
}
