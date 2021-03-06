package com.example.Booga;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Adapter_Photo_Cards extends RecyclerView.Adapter<Adapter_Photo_Cards.myViewHolder> {

    Context mContext;
    List<event> mData;
    private static final String TAG = "adapter_photo_cards";

    public Adapter_Photo_Cards(Context mContext, List<event> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v  = inflater.inflate(R.layout.card_picture_item, parent,false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        holder.event_photo.setBackgroundColor(Color.rgb(0, 0, 0));

        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef1 = storage1.getReference();
        // Points to events folder in database
        final StorageReference imagesRef1 = storageRef1.child("events");

        StorageReference spaceRef1 = imagesRef1.child(mData.get(position).getmPhoto() + ".jpg");

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(mContext)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(20))
                            .into(holder.event_photo);

                } else {
                    Log.e(TAG, "Image not found for event: " + mData.get(position).getmTitle());

                    //load IMAGE NOT FOUND

                    StorageReference spaceRef1 = imagesRef1.child("image-not-found.png");

                    spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Glide.with(mContext)
                                        .load(task.getResult())
                                        .transform( new RoundedCorners(20))
                                        .into(holder.event_photo);

                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        // Initialize the elements of the event card we wish to change
        ImageView event_photo;

        public myViewHolder(View itemView) {
            super(itemView);
            event_photo = itemView.findViewById(R.id.card_Event_Photo_Id);
        }
    }
}
