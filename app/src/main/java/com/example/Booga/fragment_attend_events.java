package com.example.Booga;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_attend_events.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_attend_events#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_attend_events extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "fragment_attend_events";
    private FirebaseAuth mAuth;

    ImageView mImageEvent1;
    ImageView mImageEvent2;
    ImageView mImageEvent3;

    private OnFragmentInteractionListener mListener;

    public fragment_attend_events() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_attend_events.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_attend_events newInstance(String param1, String param2) {
        fragment_attend_events fragment = new fragment_attend_events();
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

        View v = inflater.inflate(R.layout.fragment_attend_events, container, false);
        mImageEvent1 = v.findViewById(R.id.imageEvent1);
        mImageEvent2 = v.findViewById(R.id.imageEvent2);
        mImageEvent3 = v.findViewById(R.id.imageEvent3);
        updateProfilePicture();
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

    private void updateProfilePicture() {
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
                    Glide.with(fragment_attend_events.this)
                            .load(task.getResult())
                            .transform(new RoundedCorners(20))
                            .into(mImageEvent1);

                }
            }
        });

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_attend_events.this)
                            .load(task.getResult())
                            .transform(new RoundedCorners(20))
                            .into(mImageEvent2);

                }
            }
        });

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(fragment_attend_events.this)
                            .load(task.getResult())
                            .transform(new RoundedCorners(20))
                            .into(mImageEvent3);

                }
            }
        });
    }
}
