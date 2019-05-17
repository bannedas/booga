package com.example.Booga;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainScreenActivity extends AppCompatActivity implements fragment_all_events.OnFragmentInteractionListener,
        fragment_near_me.OnFragmentInteractionListener, fragment_my_event.OnFragmentInteractionListener,
        fragment_profile.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = "MainScreenActivity";
    private FirebaseAuth mAuth;

    private TextView mToolbar_Title;
    private Toolbar mToolbar, mProfileToolbar;
    private ImageView mToolbarSettings;
    private ImageView mToolbarDrawerMenu;
    private ImageView mToolbarProfileDrawerMenu;
    public int backButtonCount = 0;
    private FirebaseFirestore db;

    private ImageView iv;

    //drawer stuff
    private DrawerLayout mDrawerLayout;
    private ImageView imageViewDrawerPic;
    private TextView textViewDrawerName;
    private View drawer_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        boolean fromSettings = false;
        // retreive data from intent.putExtra
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            String temp = intent.getStringExtra("isFromSettingsPage");
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

        mToolbarProfileDrawerMenu = findViewById(R.id.imageView_profile_toolbar_icon_left_id);
        mToolbarDrawerMenu = findViewById(R.id.imageView_toolbar_icon_left);
        mToolbarSettings = findViewById(R.id.imageView_profile_toolbar_icon_right_id);

        mToolbarProfileDrawerMenu.setOnClickListener(this);
        mToolbarDrawerMenu.setOnClickListener(this);
        mToolbarSettings.setOnClickListener(this);

        // navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawer_header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        imageViewDrawerPic = drawer_header.findViewById(R.id.image_view_drawer_picture);
        textViewDrawerName = drawer_header.findViewById(R.id.text_view_drawer_name);
        updateDrawerProfileInfo(); // loads name and picture from firebase

        // Initialize profle toolbar
        mProfileToolbar = findViewById(R.id.toolbar_profile_main_screen_id);

        // Initialize bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize fragments in toolbar frame and main frame
        loadFragment(new fragment_near_me(), R.id.fragment_container_main);

        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        //inflate filters menu
        iv = findViewById(R.id.imageView_toolbar_icon_right);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog_list_row, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

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

    private void updateDrawerProfileInfo() {
        // ====================================== name and last name ===============================
        // Get User ID
        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null;
        String UID = fireUser.getUid(); //store user id

        // init firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        String firstName = document.getString("first_name");
                        String lastName = document.getString("last_name");
                        String firstLastName = getString(R.string.text_view_name_placeholder, firstName, lastName);
                        textViewDrawerName.setText(firstLastName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // ====================================== profile pic ======================================
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Points to user_photo
        StorageReference imagesRef = storageRef.child("user_photo");

        // spaceRef now points to "users/userID.jpg"
        StorageReference spaceRef = imagesRef.child(UID + ".jpg");

        spaceRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(drawer_header)
                            .load(task.getResult())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewDrawerPic);
                }
            }
        });
    }

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
    public void onBackPressed() {
        if(backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount = 0;
        } else {
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

            case R.id.imageView_toolbar_icon_left:
                mDrawerLayout.openDrawer(Gravity.START);
                break;

            case R.id.imageView_profile_toolbar_icon_left_id:
                mDrawerLayout.openDrawer(Gravity.START);
        }

    }
}
