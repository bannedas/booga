package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsPageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SettingsActivity";

    Button mButtonMergeAccounts;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        mButtonMergeAccounts = findViewById(R.id.buttonMergeEmailWithFacebookId);
        mButtonMergeAccounts.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        AccessToken token;
        token = AccessToken.getCurrentAccessToken();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (token != null) {
            mButtonMergeAccounts.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.buttonMergeEmailWithFacebookId:
                Intent intent = new Intent(getApplicationContext(), LinkAccountsActivty.class);
                    startActivity(intent);
                break;

        }
    }
}
