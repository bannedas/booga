package com.example.Booga;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainScreenActivity extends AppCompatActivity implements fragment_all_events.OnFragmentInteractionListener,
        fragment_near_me.OnFragmentInteractionListener, fragment_my_event.OnFragmentInteractionListener,
        fragment_profile.OnFragmentInteractionListener, fragment_attend_events.OnFragmentInteractionListener,
        fragment_hosted_events.OnFragmentInteractionListener{

    private static final String TAG = "MainScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new fragment_near_me());
    }



    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_main, fragment);
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
                    loadFragment(fragment);
                    Log.e(TAG, "loading NEAR ME fragment");
                    return true;
                case R.id.bottom_navigation_all_events_item:
                    fragment = new fragment_all_events();
                    loadFragment(fragment);
                    Log.e(TAG, "loading ALL EVENT fragment");
                    return true;
                case R.id.bottom_navigation_my_events_item:
                    fragment = new fragment_my_event();
                    loadFragment(fragment);
                    Log.e(TAG, "loading MY EVENT fragment");
                    return true;
                case R.id.bottom_navigation_my_profile_item:
                    fragment = new fragment_profile();
                    loadFragment(fragment);
                    Log.e(TAG, "loading PROFILE fragment");
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
