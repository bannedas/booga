package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_profile extends Fragment implements View.OnClickListener {

    private static final String TAG = "fragment_profile";
    private FirebaseAuth mAuth;

    ImageView profilePictureImageView;
    ImageView settingsIcon;
    ImageView menuIcon;

    ImageView mImageEvent1;
    ImageView mImageEvent2;
    ImageView mImageEvent3;

    TextView name;
    TextView bio;

    Button buttonSignOut;
    Button buttonSettings;

    RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public fragment_profile() {
        // Required empty public constructor
    }

    public static fragment_profile newInstance(String param1, String param2) {
        fragment_profile fragment = new fragment_profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePictureImageView = v.findViewById(R.id.profilePictureId);

        settingsIcon = v.findViewById(R.id.iconSettingsId);
        menuIcon = v.findViewById(R.id.iconMenuId);


//        mImageEvent1 = v.findViewById(R.id.imageEvent4);
//        mImageEvent2 = v.findViewById(R.id.imageEvent5);
//        mImageEvent3 = v.findViewById(R.id.imageEvent6);
        buttonSignOut = v.findViewById(R.id.button_sign_out);
        buttonSettings = v.findViewById(R.id.buttonSettingsId);
        name = v.findViewById(R.id.text_view_name);
        bio = v.findViewById(R.id.text_view_user_info);

        Fragment childFragment = new fragment_attend_events();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_attend_events, childFragment).commit();

        updateProfilePicture();
        //updateAttendEvent();


        recyclerView = v.findViewById(R.id.recyclerView_My_Events_Id);



        //Test of recycleview
        List<event> mList = new ArrayList<>();
        mList.add(new event("Fest i Slusen","AAU","23 m","photo"));
        mList.add(new event("Lunch","Canteen","50 m","photo"));
        mList.add(new event("Dinner","Canteen","50 m","photo"));
        mList.add(new event("Homework","Canteen","50 m","photo"));
        mList.add(new event("Sleep","Canteen","50 m","photo"));

        Adapter_Event_Cards adapter = new Adapter_Event_Cards(getContext(),mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);



        buttonSignOut.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    private void updateProfilePicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Points to user_photo
        StorageReference imagesRef = storageRef.child("user_photo");

        // Get User ID
//        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
//        final String UID = fireUser.getUid(); //store user id

        // spaceRef now points to "users/userID.jpg"
        StorageReference spaceRef = imagesRef.child("user2.jpg");

        spaceRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .into(profilePictureImageView);

                }
            }
        });
    }

    private void updateAttendEvent() {
        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef1 = storage1.getReference();
        // Points to user_photo
        StorageReference imagesRef1 = storageRef1.child("events");

        // Get User ID
//        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
//        final String UID = fireUser.getUid(); //store user id

        // spaceRef now points to "users/userID.jpg"


        StorageReference spaceRef1 = imagesRef1.child("event1.jpg");

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(20))
                            .into(mImageEvent1);

                }
            }
        });

        StorageReference spaceRef2 = imagesRef1.child("event1.jpg");

        spaceRef2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(20))
                            .into(mImageEvent2);

                }
            }
        });

        StorageReference spaceRef3 = imagesRef1.child("event1.jpg");

        spaceRef3.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(20))
                            .into(mImageEvent3);
                }
            }
        });


    }
    public void onClick(View view) {
        switch(view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.button_sign_out:
                Log.d(TAG, "User signed out");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    startActivity(intent);
                }
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                break;

            case R.id.buttonSettingsId:
                 intent = new Intent(getActivity(), SettingsPageActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    Log.d(TAG, "WARNING" +mAuth.getCurrentUser().getProviders());
                } else {
                    startActivity(intent);
                    Log.d(TAG, "WARNING" +mAuth.getCurrentUser().getProviders());
                }
        }
    }
}

