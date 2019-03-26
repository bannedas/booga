package com.example.Booga;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class Adapter_Event_Cards extends RecyclerView.Adapter<Adapter_Event_Cards.myViewHolder> {

    Context mContext;
    List<event> mData;

    public Adapter_Event_Cards(Context mContext, List<event> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v  = inflater.inflate(R.layout.card_event_item,parent,false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.event_title.setText(mData.get(position).getmTitle());
        holder.event_location.setText(mData.get(position).getmLocation());
        holder.event_distance.setText(mData.get(position).getmDistance());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        // Initialize the elements of the event card we wish to change
        ImageView event_photo;
        TextView event_title, event_location, event_distance;


        public myViewHolder(View itemView) {
            super(itemView);
            event_photo = itemView.findViewById(R.id.card_Event_Photo_Id);
            event_title = itemView.findViewById(R.id.card_Event_Title_Text_Id);
            event_distance = itemView.findViewById(R.id.card_Event_Distance_Text_Id);
            event_location = itemView.findViewById(R.id.card_Event_Location_Text_Id);
        }
    }
}
