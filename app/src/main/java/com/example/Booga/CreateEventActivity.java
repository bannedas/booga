package com.example.Booga;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetDialog.BottomSheetListener {

    TextInputLayout text_input_event_title, text_input_event_location, text_input_event_date, text_input_event_time, text_input_event_description;
    Switch private_event_switch;
    Button create_event_button;

    //This is the entry point for the Firebase Authentication SDK
    //private FirebaseAuth mAuth;
    //init firestore database sdk
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventTypeRef = db.collection("eventType");
    private CollectionReference eventTagsRef = db.collection("eventTags");
    private Adapter_Event_Type Adapter_Event_Type;
    private Adapter_Tags Adapter_Tags;

    private URI mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "CreateEventActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE= "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PICTURE = "pictureUrl";
    private static final String KEY_TIMESTAMP_EVENT_CREATED = "createdAt";
    //private static boolean KEY_PRIVATE_PARTY = "description";
    private static final String KEY_USER_ID = "createdByUserID";
    private static String KEY_PRIVATE_EVENT = "isPrivate";
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //This code is not needed for now. but we might need it later
        //FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        USER_ID = mAuth.getCurrentUser().getUid();
        //assign db to our instance
        //db = FirebaseFirestore.getInstance();
        //signUpProgressBar =  findViewById(R.id.buttonSignUpLoginId); // don't know what is this
        text_input_event_title = findViewById(R.id.event_title);
        text_input_event_location = findViewById(R.id.location);
        text_input_event_date = findViewById(R.id.date);
        text_input_event_time = findViewById(R.id.time);
        text_input_event_description = findViewById(R.id.description);
        private_event_switch = findViewById(R.id.private_event_switch);

        create_event_button = findViewById(R.id.create_event_button);
        create_event_button.setOnClickListener(this);

        setupRecyclerViewForEventType();
        setupRecyclerViewForEventTag();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.create_event_button:

                createEvent();
                break;
        }
    }

    private void createEvent() {
        if(!validateInput(text_input_event_title) | !validateInput(text_input_event_location) |
                !validateInput(text_input_event_date) | !validateInput(text_input_event_time) |
                !validateInput(text_input_event_description)) {
            return;
        }

            String input = text_input_event_title.getEditText().getText().toString().trim();
            input += "\n";
            input += text_input_event_location.getEditText().getText().toString().trim();
            input += "\n";
            input += text_input_event_date.getEditText().getText().toString().trim();
            input += "\n";
            input += text_input_event_time.getEditText().getText().toString().trim();
            input += "\n";
            input += text_input_event_description.getEditText().getText().toString().trim();


            String event_title = text_input_event_title.getEditText().getText().toString().trim();
            String event_location = text_input_event_location.getEditText().getText().toString().trim();
            String event_date = text_input_event_date.getEditText().getText().toString().trim();
            String event_time = text_input_event_time.getEditText().getText().toString().trim();
            String event_description = text_input_event_description.getEditText().getText().toString().trim();
            Boolean switchState = private_event_switch.isChecked();

            Map<String, Object> newEvent = new HashMap<>();
            newEvent.put(KEY_TITLE, event_title);
            newEvent.put(KEY_LOCATION, event_location);
            newEvent.put(KEY_DATE, event_date);
            newEvent.put(KEY_TIME, event_time);
            newEvent.put(KEY_DESCRIPTION, event_description);
            newEvent.put(KEY_TIMESTAMP_EVENT_CREATED, new Timestamp(System.currentTimeMillis()));
            newEvent.put(KEY_USER_ID, USER_ID);
            newEvent.put(KEY_PRIVATE_EVENT, switchState);

            db.collection("allEvents").document().set(newEvent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateEventActivity.this, "New event created", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateEventActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });

        //Toast.makeText(this, input, Toast.LENGTH_LONG).show();
    }
    public boolean validateInput(TextInputLayout text_input) {
        String string_event_title_input = text_input.getEditText().getText().toString().trim();
        if(string_event_title_input.isEmpty()) {
            text_input.setError("Field can't be empty");
            return false;
       /* } else if (string_event_title_input.length() > 30 ) {
            text_input.setError("Input too long");
            return false; */
        } else {
            text_input.setError(null);
            return true;
        }
    }

    @Override
    public void onButtonClicked(String text) {

    }

    public void create_event_picture_settings(View view){
        BottomSheetDialog bottomSheet = new BottomSheetDialog();
        bottomSheet.show(getSupportFragmentManager(), "BottomSheet");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Adapter_Event_Type.startListening();
        Adapter_Tags.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Adapter_Event_Type.stopListening();
        Adapter_Tags.stopListening();
    }

    private void setupRecyclerViewForEventType() {
        //1. SELECT * FROM eventType
        //Query EventTypeQuery = eventTypeRef.orderBy("eventTypeName", Query.Direction.DESCENDING);
        Query EventTypeQuery = eventTypeRef;
        /*
         * Attaching the event listener to read the values
         * */
        FirestoreRecyclerOptions<EventType> options =
                new FirestoreRecyclerOptions.Builder<EventType>()
                        .setQuery(EventTypeQuery, EventType.class)
                        .build();

        Adapter_Event_Type = new Adapter_Event_Type(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_event_type_create_event);
        recyclerView.setHasFixedSize(true); //performance reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(Adapter_Event_Type);
    }
    private void setupRecyclerViewForEventTag() {
        //2. SELECT * FROM eventTags
        //Query EventTagsQuery = FirebaseDatabase.getInstance().getReference().child("eventTags");
        //Query TagQuery = eventTagsRef.orderBy("tagName", Query.Direction.DESCENDING);
        Query TagQuery = eventTagsRef;
        /*
         * Attaching the event listener to read the values
         * */
        FirestoreRecyclerOptions<EventTags> options =
                new FirestoreRecyclerOptions.Builder<EventTags>()
                        .setQuery(TagQuery, EventTags.class)
                        .build();

        Adapter_Tags = new Adapter_Tags(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_event_tags_create_event);
        recyclerView.setHasFixedSize(true); //performance reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(Adapter_Tags);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data == null && data.getData() != null) {
           // mImageUri = data.getData();
        }
//        if(requestCode == PICK_IMAGE_REQUEST && resultCode = RESULT_OK && data != null
//                && data.getData() != null) {
//            mImageUri = data.getData();

        }
}