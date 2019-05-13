package com.example.Booga;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_profile extends Fragment {

    private static final String TAG = "fragment_profile";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private OnFragmentInteractionListener mListener;

    String UID;

    //event list
    List<event> mList;
    List<event> mListAttended;

    ImageView profilePictureImageView;

    TextView name;
    TextView bio;
    TextView pictureError;
    TextView mUserEmail;
    TextView mUserPhoneNumber;

    RecyclerView recyclerViewHostedEvents;
    RecyclerView recyclerViewAttendedEvents;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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

        // Get User ID
        FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
        assert fireUser != null;
        UID = fireUser.getUid(); //store user id

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePictureImageView = v.findViewById(R.id.profilePictureId);
        name = v.findViewById(R.id.text_view_name);
        bio = v.findViewById(R.id.text_view_user_info);
        mUserEmail = v.findViewById(R.id.textViewShowUserEmailId);
        mUserPhoneNumber = v.findViewById(R.id.textViewShowUserPhoneId);
        pictureError = v.findViewById(R.id.text_view_picture_error);
        pictureError.setVisibility(View.GONE);

        recyclerViewHostedEvents = v.findViewById(R.id.recycle_view_hosted_events);
        recyclerViewAttendedEvents = v.findViewById(R.id.recycle_view_attend_events);

        String userEmail = mAuth.getCurrentUser().getEmail();
        String userPhone = mAuth.getCurrentUser().getPhoneNumber();
        mUserEmail.setText(userEmail);
        mUserPhoneNumber.setText(userPhone);

        updateProfilePicture();
        updateUserNameAndBio();

        updateHostedEvents();
        updateAttendedEvents();


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

    private void updateHostedEvents() {
        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        db.collection("allEvents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mList = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String createBy = document.getString("createdByUserID");
                        if(createBy.equals(UID)) {
                            String eventTitle = document.getString("title");
                            String eventLocation = document.getString("location");
                            // TODO distance from google maps
                            String eventId = document.getId();
                            mList.add(new event(eventTitle,eventLocation,"?? m",eventId));
                        }
                    }
                    Adapter_Event_Cards adapter_hosted_events = new Adapter_Event_Cards(getContext(), mList);
                    recyclerViewHostedEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, true));
                    recyclerViewHostedEvents.setAdapter(adapter_hosted_events);
                    ViewCompat.setNestedScrollingEnabled(recyclerViewHostedEvents, false);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void updateAttendedEvents() {
        DocumentReference docRef = db.collection("attendance").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mListAttended = new ArrayList<>();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            Log.e(TAG, "try: " + entry.getKey());

                            // for every eventID read their database and update adapters

                            DocumentReference docRef = db.collection("allEvents").document(entry.getKey());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        assert document != null;
                                        if (document.exists()) {
                                            String eventTitle = document.getString("title");
                                            Log.e(TAG, "Added: " + eventTitle);
                                            String eventLocation = document.getString("location");
                                            // TODO distance from google maps
                                            String eventId = document.getId();
                                            mListAttended.add(new event(eventTitle, eventLocation, "?? m", eventId));
                                        }
                                    }
                                }
                            });
                        }
                    }
                    Adapter_Event_Cards adapter_attended_events = new Adapter_Event_Cards(getContext(), mListAttended);
                    recyclerViewAttendedEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
                    recyclerViewAttendedEvents.setAdapter(adapter_attended_events);
                    ViewCompat.setNestedScrollingEnabled(recyclerViewAttendedEvents, false);
                }

            }
        });
    }

    private void updateProfilePicture() {
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
}

