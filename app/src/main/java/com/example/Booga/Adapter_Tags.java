package com.example.Booga;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Adapter_Tags extends FirestoreRecyclerAdapter<EventTags, Adapter_Tags.TagHolder> {

    public Adapter_Tags(@NonNull FirestoreRecyclerOptions<EventTags> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TagHolder holder, int position, @NonNull EventTags model) {
        holder.tag_text.setText(model.getTagName());
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTag) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new TagHolder(view);
    }

    class TagHolder extends RecyclerView.ViewHolder {

        public TextView tag_text;

        public TagHolder(@NonNull View itemView) {
            super(itemView);

            tag_text = itemView.findViewById(R.id.event_text_view_box);
        }
    }
}

