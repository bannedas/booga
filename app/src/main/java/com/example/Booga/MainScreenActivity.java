package com.example.Booga;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainScreenActivity extends AppCompatActivity implements fragment_all_events.OnFragmentInteractionListener,
        fragment_near_me.OnFragmentInteractionListener, fragment_my_event.OnFragmentInteractionListener,
        fragment_profile.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = "MainScreenActivity";
    private TextView mToolbar_Title;
    private Toolbar mToolbar, mProfileToolbar;
    private ImageView mToolbarSettings;
    public int backButtonCount = 0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        boolean fromSettings = false;
        // retreive data from intent.putExtra
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            String temp = intent.getStringExtra("isFromSettingsPage");
            Log.e(TAG, "t: " + temp);
            if(temp != null) {
                if (temp.equals("yes")) {
                    fromSettings = true;
                }
            }
        }

        // Initialize top toolbar
        mToolbar = findViewById(R.id.toolbar_main_screen_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        mToolbar_Title = mToolbar.findViewById(R.id.textView_toolbar_title_id);
        mToolbar_Title.setText(R.string.toolbar_title_near_me);
        mToolbarSettings = findViewById(R.id.imageView_profile_toolbar_icon_right_id);

        mToolbarSettings.setOnClickListener(this);

        // Initialize profle toolbar
        mProfileToolbar = findViewById(R.id.toolbar_profile_main_screen_id);


        // Initialize bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize fragments in toolbar frame and main frame
        loadFragment(new fragment_near_me(), R.id.fragment_container_main);

        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        // Enable Firestore offline caching
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        // if coming from settings page then load porfile fragment
        if(fromSettings) {
            Fragment fragment;
            fragment = new fragment_profile();
            loadFragment(fragment, R.id.fragment_container_main);
            //Toolbar updating
            getSupportActionBar().hide();
            setSupportActionBar(mProfileToolbar);
            mToolbar.setVisibility(View.GONE);
            mProfileToolbar.setVisibility(View.VISIBLE);
            Log.d(TAG, "loading PROFILE fragment");
        }
    }

    private void loadFragment(Fragment fragment, int fragmentContainerID) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentContainerID, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {

                case R.id.bottom_navigation_near_me_item:
                    fragment = new fragment_near_me();
                    loadFragment(fragment, R.id.fragment_container_main);
                    //Toolbar updating
                    getSupportActionBar().hide();
                    setSupportActionBar(mToolbar);
                    mToolbar_Title.setText(R.string.toolbar_title_near_me);
                    mProfileToolbar.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loading NEAR ME fragment");
                    return true;

                case R.id.bottom_navigation_all_events_item:
                    fragment = new fragment_all_events();
                    loadFragment(fragment, R.id.fragment_container_main);
                    //Toolbar updating
                    getSupportActionBar().hide();
                    setSupportActionBar(mToolbar);
                    mToolbar_Title.setText(R.string.toolbar_title_near_me);
                    mProfileToolbar.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loading ALL EVENT fragment");
                    return true;

                case R.id.bottom_navigation_my_events_item:

                    fragment = new fragment_my_event();
                    loadFragment(fragment, R.id.fragment_container_main);
                    //Toolbar updating
                    getSupportActionBar().hide();
                    setSupportActionBar(mToolbar);
                    mToolbar_Title.setText(R.string.toolbar_title_my_event);
                    mProfileToolbar.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loading MY EVENT fragment");
                    return true;

                case R.id.bottom_navigation_my_profile_item:

                    fragment = new fragment_profile();
                    loadFragment(fragment, R.id.fragment_container_main);
                    //Toolbar updating
                    getSupportActionBar().hide();
                    setSupportActionBar(mProfileToolbar);
                    mToolbar.setVisibility(View.GONE);
                    mProfileToolbar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loading PROFILE fragment");
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void floating_action_button(View view){
        Log.d(TAG, "Prompting to Create Event window");
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        startActivity(intent);
    }

    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {

        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount = 0;
        }
        else
        {
            Toast.makeText(this, "Press the back button again to close the application.", Toast.LENGTH_LONG).show();
            backButtonCount++;
            super.onBackPressed();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_profile_toolbar_icon_right_id:
                Intent intent = new Intent(MainScreenActivity.this, SettingsPageActivity.class);
                startActivity(intent);
                Log.d(TAG, "Settings icon clicked");
                break;
        }

    }

}
