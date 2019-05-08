package com.example.Booga;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity implements fragment_all_events.OnFragmentInteractionListener,
        fragment_near_me.OnFragmentInteractionListener, fragment_my_event.OnFragmentInteractionListener,
        fragment_profile.OnFragmentInteractionListener, fragment_attend_events.OnFragmentInteractionListener{

    private static final String TAG = "MainScreenActivity";
    private TextView mToolbar_Title;
    private Toolbar mToolbar, mProfileToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Initialize top toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main_screen_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        mToolbar_Title = (TextView) mToolbar.findViewById(R.id.textView_toolbar_title_id);
        mToolbar_Title.setText(R.string.toolbar_title_near_me);

        // Initialize profle toolbar
        mProfileToolbar = (Toolbar) findViewById(R.id.toolbar_profile_main_screen_id);

        // Initialize bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize fragments in toolbar frame and main frame
        loadFragment(new fragment_near_me(),R.id.fragment_container_main);
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
                    loadFragment(fragment,R.id.fragment_container_main);
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
                    loadFragment(fragment,R.id.fragment_container_main);
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
                    loadFragment(fragment,R.id.fragment_container_main);
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
                    loadFragment(fragment,R.id.fragment_container_main);
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
}
