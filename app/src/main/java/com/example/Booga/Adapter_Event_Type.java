package com.example.Booga;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Adapter_Event_Type extends FirestoreRecyclerAdapter<EventType, Adapter_Event_Type.EventTypeHolder> {

    public Adapter_Event_Type(@NonNull FirestoreRecyclerOptions<EventType> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventTypeHolder holder, int position, @NonNull EventType model) {
        holder.event_type_text.setText(model.getEventTypeName());
    }

    @NonNull
    @Override
    public EventTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new EventTypeHolder(view);
    }

    class EventTypeHolder extends RecyclerView.ViewHolder {

        public TextView event_type_text;

        public EventTypeHolder(@NonNull View itemView) {
            super(itemView);

            event_type_text = itemView.findViewById(R.id.event_text_view_box);
        }
    }
}
