package com.example.Booga;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        final ImageView  imageView;
        TextView  eventTitle, eventLocation, eventDistance;

        imageView = view.findViewById(R.id.slide_Event_Background_Image_Id);
        eventTitle = view.findViewById(R.id.slide_Event_Title_Text_Id);
        eventLocation = view.findViewById(R.id.slide_Event_Location_Text_Id);
        eventDistance = view.findViewById(R.id.slide_Event_Distance_Text_Id);

        eventTitle.setText(mData.get(position).getmTitle());
        eventLocation.setText(mData.get(position).getmLocation());
        eventDistance.setText(mData.get(position).getmDistance());

        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef1 = storage1.getReference();
        // Points to events folder in database
        StorageReference imagesRef1 = storageRef1.child("events");

        // TODO get specific event photo

        StorageReference spaceRef1 = imagesRef1.child(mData.get(position).getmPhoto() + ".jpg");

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(mContext)
                            .load(task.getResult())
                            .transform(new CenterCrop())
                            .into(imageView);

                }
            }
        });

        container.addView(view, 0);
        // TODO ADD CONTENT TO SLIDER ITEM
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
