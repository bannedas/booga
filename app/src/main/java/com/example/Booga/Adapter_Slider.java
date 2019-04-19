package com.example.Booga;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class Adapter_Slider extends PagerAdapter {

    private Context mContext;
    private List<event> mData;
    private LayoutInflater layoutInflater;

    public Adapter_Slider(Context mContext, List<event> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.slide_event_item, container, false);

        ImageView  imageView;
        TextView  eventTitle, eventLocation, eventDistance;

        imageView = view.findViewById(R.id.slide_Event_Background_Image_Id);
        eventTitle = view.findViewById(R.id.slide_Event_Title_Text_Id);
        eventLocation = view.findViewById(R.id.slide_Event_Location_Text_Id);
        eventDistance = view.findViewById(R.id.slide_Event_Distance_Text_Id);

        container.addView(view, 0);
        // TODO ADD CONTENT TO SLIDER ITEM
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);
    }
}
