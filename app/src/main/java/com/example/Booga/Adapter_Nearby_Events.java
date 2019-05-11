package com.example.Booga;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Adapter_Nearby_Events extends FirestoreRecyclerAdapter<event, Adapter_Nearby_Events.eventHolder> {

    public Adapter_Nearby_Events(@NonNull FirestoreRecyclerOptions<event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull eventHolder holder, int position, @NonNull event model) {
        holder.eventTitle.setText(model.getmTitle());
        holder.eventDistance.setText(model.getmDistance());
        holder.eventLocation.setText(model.getmLocation());
        holder.eventPhoto.setImageURI(Uri.parse(model.getmPhoto()));
    }

    @NonNull
    @Override
    public eventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event_item, parent, false);

        return new eventHolder(view);
    }

    class eventHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle;
        public TextView eventDistance;
        public TextView eventLocation;
        public ImageView eventPhoto;

        public eventHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.card_Event_Title_Text_Id);
            eventDistance = itemView.findViewById(R.id.card_Event_Distance_Text_Id);
            eventLocation = itemView.findViewById(R.id.card_Event_Location_Text_Id);
            eventPhoto = itemView.findViewById(R.id.card_Event_Photo_Id);
        }
    }
}
