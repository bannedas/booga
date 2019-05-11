package com.example.Booga;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ViewPager mViewPager_Slider;
    private Adapter_Slider adapter_slider;

    private ImageView slider1, slider2, slider3, slider4;
    private ImageView[] sliderArray = new ImageView[4];
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

        View v = inflater.inflate(R.layout.fragment_near_me, container, false);

        slider1 = v.findViewById(R.id.slide_Near_Me_1);
        slider2 = v.findViewById(R.id.slide_Near_Me_2);
        slider3 = v.findViewById(R.id.slide_Near_Me_3);
        slider4 = v.findViewById(R.id.slide_Near_Me_4);

        sliderArray[0] = slider1;
        sliderArray[1] = slider2;
        sliderArray[2] = slider3;
        sliderArray[3] = slider4;

        slider1.setImageResource(R.drawable.slider_pink);

        List<event> testList = new ArrayList<>();
        testList.add(new event("Fest i Slusen","AAU","23 m","event1"));
        testList.add(new event("Lunch","Canteen","50 m","event2"));
        testList.add(new event("Dinner","Canteen","50 m","event3"));
        testList.add(new event("Homework","Canteen","50 m","event4"));

        adapter_slider = new Adapter_Slider(getContext(),testList);

        mViewPager_Slider = v.findViewById(R.id.viewPager_Near_Me_Id);

        mViewPager_Slider.setAdapter(adapter_slider);

        mViewPager_Slider.addOnPageChangeListener(viewListener);
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
