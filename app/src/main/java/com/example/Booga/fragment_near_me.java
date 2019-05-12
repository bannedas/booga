package com.example.Booga;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_near_me.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_near_me#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_near_me extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String TAG = "fragment_near_me";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView[] sliderArray;

    private FirebaseFirestore db;

    //event list
    List<event> mList;

    private OnFragmentInteractionListener mListener;

    private ViewPager mViewPager_Slider;

    public fragment_near_me() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_near_me.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_near_me newInstance(String param1, String param2) {
        fragment_near_me fragment = new fragment_near_me();
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

        //TODO make everything dynamic and not HARD CODED

        sliderArray = new ImageView[2];

        View v = inflater.inflate(R.layout.fragment_near_me, container, false);

        sliderArray[0] = v.findViewById(R.id.slide_Near_Me_1);
        sliderArray[1] = v.findViewById(R.id.slide_Near_Me_2);

        sliderArray[0].setImageResource(R.drawable.slider_pink);

        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        // pull all events from firebase, then get their title, loc, dist, eventID
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
                    Adapter_Slider adapter_slider;
                    adapter_slider = new Adapter_Slider(getContext(), mList);
                    mViewPager_Slider.setAdapter(adapter_slider);
                    mViewPager_Slider.addOnPageChangeListener(viewListener);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        mViewPager_Slider = v.findViewById(R.id.viewPager_Near_Me_Id);
        return v;
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            for (ImageView slider: sliderArray){
                slider.setImageResource(R.drawable.slider_whitesmoke);
            }
            sliderArray[i].setImageResource(R.drawable.slider_pink);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

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
