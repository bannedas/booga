package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    Fragment fragmentAttend;

    ImageView mImageEvent1;
    ImageView mImageEvent2;
    ImageView mImageEvent3;
    ProgressBar loadingPanel;

    TextView name;
    TextView bio;

    Button buttonSignOut;

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


        mImageEvent1 = v.findViewById(R.id.imageEvent4);
        mImageEvent2 = v.findViewById(R.id.imageEvent5);
        mImageEvent3 = v.findViewById(R.id.imageEvent6);
        buttonSignOut = v.findViewById(R.id.button_sign_out);
        loadingPanel = v.findViewById(R.id.loadingPanel);
        name = v.findViewById(R.id.text_view_name);
        bio = v.findViewById(R.id.text_view_user_info);


        mImageEvent1.setVisibility(View.GONE);
        mImageEvent2.setVisibility(View.GONE);
        mImageEvent3.setVisibility(View.GONE);
        buttonSignOut.setVisibility(View.GONE);
        profilePictureImageView.setVisibility(View.GONE);
        settingsIcon.setVisibility(View.GONE);
        menuIcon.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        bio.setVisibility(View.GONE);


//        fragmentAttend = v.findViewById(R.id.fragment);

        Fragment childFragment = new fragment_attend_events();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_attend_events, childFragment).commit();

        updateProfilePicture();
        updateAttendEvent();

        buttonSignOut.setOnClickListener(this);
        return v;
       // return inflater.inflate(R.layout.fragment_profile, container, false);
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
        }
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
        StorageReference spaceRef = imagesRef.child("QCrTXUs8UgQvyNCf582AGyBzu9A2.jpg");

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

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
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

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(20))
                            .into(mImageEvent3);

                    //DELETE progressbar to and show content

                    mImageEvent1.setVisibility(View.VISIBLE);
                    mImageEvent2.setVisibility(View.VISIBLE);
                    mImageEvent3.setVisibility(View.VISIBLE);
                    buttonSignOut.setVisibility(View.VISIBLE);
                    profilePictureImageView.setVisibility(View.VISIBLE);
                    settingsIcon.setVisibility(View.VISIBLE);
                    menuIcon.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    bio.setVisibility(View.VISIBLE);

                    loadingPanel.setVisibility(View.GONE);

                }
            }
        });


    }
}

