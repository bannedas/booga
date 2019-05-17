package com.example.Booga;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class Adapter_Event_Type extends FirestoreRecyclerAdapter<EventType, Adapter_Event_Type.EventTypeHolder> {

    private Context mContext;

    public Adapter_Event_Type(@NonNull FirestoreRecyclerOptions<EventType> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventTypeHolder holder, int position, @NonNull EventType model) {
        holder.event_type_text.setText(model.getEventTypeName());
    }

    @NonNull
    @Override
    public EventTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v  = inflater.inflate(R.layout.item_row, parent,false);
        return new EventTypeHolder(v);
    }

    class EventTypeHolder extends RecyclerView.ViewHolder {

        public TextView event_type_text;

        public EventTypeHolder(@NonNull View itemView) {
            super(itemView);

            event_type_text = itemView.findViewById(R.id.event_text_view_box);
        }
    }
}
