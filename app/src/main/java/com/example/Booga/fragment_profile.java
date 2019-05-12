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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private FirebaseFirestore db;

    //event list
    List<event> mList;

    ImageView profilePictureImageView;
    ImageView settingsIcon;

    TextView name;
    TextView bio;
    TextView pictureError;
    TextView mUserEmail;
    TextView mUserPhoneNumber;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePictureImageView = v.findViewById(R.id.profilePictureId);
        settingsIcon = v.findViewById(R.id.iconSettingsId);
        name = v.findViewById(R.id.text_view_name);
        bio = v.findViewById(R.id.text_view_user_info);
        mUserEmail = v.findViewById(R.id.textViewShowUserEmailId);
        mUserPhoneNumber = v.findViewById(R.id.textViewShowUserPhoneId);
        pictureError = v.findViewById(R.id.text_view_picture_error);
        pictureError.setVisibility(View.GONE);

        recyclerView = v.findViewById(R.id.recyclerView_My_Events_Id);

        String userEmail = mAuth.getCurrentUser().getEmail();
        String userPhone = mAuth.getCurrentUser().getPhoneNumber();
        mUserEmail.setText(userEmail);
        mUserPhoneNumber.setText(userPhone);

        Fragment childFragment = new fragment_attend_events();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_attend_events, childFragment).commit();

        updateProfilePicture();
        updateUserNameAndBio();

        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        db.collection("allEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mList = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String eventTitle = document.getString("title");
                        String eventLocation = document.getString("location");
                        // TODO distance from google maps
                        String eventId = document.getId();
                        mList.add(new event(eventTitle,eventLocation,"?? m",eventId));
                    }
                    Adapter_Event_Cards adapter = new Adapter_Event_Cards(getContext(), mList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, true));
                    recyclerView.setAdapter(adapter);
                    ViewCompat.setNestedScrollingEnabled(recyclerView, false);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        settingsIcon.setOnClickListener(this);
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
        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null;
        final String UID = fireUser.getUid(); //store user id
        // spaceRef now points to "users/userID.jpg"
        StorageReference spaceRef = imagesRef.child(UID + ".jpg");

        spaceRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_profile.this)
                            .load(task.getResult())
                            .into(profilePictureImageView);
                } else {
                    pictureError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateUserNameAndBio() {
        // Get User ID
        final FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null;
        final String UID = fireUser.getUid(); //store user id

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
                        String userBio = document.getString("bio");

                        String firstLastName = getString(R.string.text_view_name_placeholder, firstName, lastName);

                        name.setText(firstLastName);
                        bio.setText(userBio);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iconSettingsId:
                Intent intent = new Intent(getActivity(), SettingsPageActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                Log.d(TAG, "WARNING" + Objects.requireNonNull(mAuth.getCurrentUser()).getProviders());
        }
    }
}

