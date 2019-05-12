package com.example.Booga;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_all_events.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_all_events#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_all_events extends Fragment {

    private static final String TAG = "fragment_all_events";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //event list
    List<event> mList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView_Events_Nearby;
    private RecyclerView recyclerView_Events_Trending;
    private RecyclerView recyclerView_Event_Types;

    public fragment_all_events() {
        // Required empty public constructor
    }

    public static fragment_all_events newInstance(String param1, String param2) {
        fragment_all_events fragment = new fragment_all_events();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_events, container, false);
        recyclerView_Events_Nearby = v.findViewById(R.id.recyclerView_All_Events_Nearby_Id);
        recyclerView_Events_Trending =v.findViewById(R.id.recyclerView_All_Events_Trending_Id);
        recyclerView_Event_Types = v.findViewById(R.id.recyclerView_Event_Type_Id);

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
                    Adapter_Event_Cards adapter = new Adapter_Event_Cards(getContext(),mList);
                    recyclerView_Events_Nearby.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL,false));
                    recyclerView_Events_Trending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
                    recyclerView_Event_Types.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

                    recyclerView_Events_Nearby.setAdapter(adapter);
                    recyclerView_Events_Trending.setAdapter(adapter);
                    recyclerView_Event_Types.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


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
}
