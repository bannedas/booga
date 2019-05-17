package com.example.Booga;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout text_input_event_title, text_input_event_location, text_input_event_date, text_input_event_time, text_input_event_description;
    Switch private_event_switch;
    Button create_event_button;
    TextView textViewUploadPicEn, textViewUploadPicDa;
    ImageView pinkTint;

    //SelectableAdapter adapter;

    private ImageView stockImage;
    private final int PICK_IMAGE_REQUEST = 72;
    private Uri filePath;
    private StorageReference mStorageRef;
    Map<String, Object> newEvent;

    //This is the entry point for the Firebase Authentication SDK
    private FirebaseAuth mAuth;
    //init firestore database sdk
    private FirebaseFirestore db;
    private Adapter_Event_Type Adapter_Event_Type;
    private Adapter_Tags Adapter_Tags;

    private static final String TAG = "CreateEventActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE= "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TIMESTAMP_EVENT_CREATED = "createdAt";
    private static final String KEY_USER_ID = "createdByUserID";
    private static final String KEY_PRIVATE_EVENT = "isPrivate";
    private static final String KEY_FEATURED = "featured";
    private static final String KEY_SPONSORED = "sponsored";
    private String USER_ID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //This code is not needed for now. but we might need it later
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //create eventID
        eventID = UUID.randomUUID().toString();

        // get UID
        FirebaseUser fireUser = mAuth.getCurrentUser();
        assert fireUser != null;
        USER_ID = fireUser.getUid(); //store user id

        text_input_event_title = findViewById(R.id.event_title);
        text_input_event_location = findViewById(R.id.location);
        text_input_event_date = findViewById(R.id.date);
        text_input_event_time = findViewById(R.id.time);
        text_input_event_description = findViewById(R.id.description);
        private_event_switch = findViewById(R.id.private_event_switch);
        create_event_button = findViewById(R.id.create_event_button);

        textViewUploadPicEn = findViewById(R.id.text_view_upload_picture_english);
        textViewUploadPicDa = findViewById(R.id.text_view_upload_picture_danish);
        pinkTint = findViewById(R.id.pink_tint);

        create_event_button.setOnClickListener(this);

        setupRecyclerViewForEventType();
        setupRecyclerViewForEventTag();

        stockImage = findViewById(R.id.create_event_image_view);
        stockImage.setImageResource(R.drawable.stock);

        //pink_tint = findViewById(R.id.pink_tint);
        //stockImage.setImageResource(R.drawable.pink_gradient);

        mStorageRef = FirebaseStorage.getInstance().getReference("create_event_photo");
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
            String event_title = text_input_event_title.getEditText().getText().toString().trim();
            String event_location = text_input_event_location.getEditText().getText().toString().trim();
            String event_date = text_input_event_date.getEditText().getText().toString().trim();
            String event_time = text_input_event_time.getEditText().getText().toString().trim();
            String event_description = text_input_event_description.getEditText().getText().toString().trim();
            Boolean switchState = private_event_switch.isChecked();

            newEvent = new HashMap<>();
            newEvent.put(KEY_TITLE, event_title);
            newEvent.put(KEY_LOCATION, event_location);
            newEvent.put(KEY_DATE, event_date);
            newEvent.put(KEY_TIME, event_time);
            newEvent.put(KEY_DESCRIPTION, event_description);
            newEvent.put(KEY_TIMESTAMP_EVENT_CREATED, new Timestamp(System.currentTimeMillis()));
            newEvent.put(KEY_USER_ID, USER_ID);
            newEvent.put(KEY_PRIVATE_EVENT, switchState);
            newEvent.put(KEY_FEATURED, false); // hardcoded for now
            newEvent.put(KEY_SPONSORED, false);

            db.collection("allEvents").document(eventID).set(newEvent)
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
        } else {
            text_input.setError(null);
            return true;
        }
    }

    public void create_event_picture_settings(View view){
        textViewUploadPicEn.setVisibility(View.GONE);
        textViewUploadPicDa.setVisibility(View.GONE);
        pinkTint.setVisibility(View.GONE);
        Log.d(TAG, "Clicked upload picture button");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 72);
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

        Query EventTypeQuery = db.collection("eventType");
        /*
         * Attaching the event listener to read the values
         * */
        FirestoreRecyclerOptions<EventType> options =
                new FirestoreRecyclerOptions.Builder<EventType>()
                        .setQuery(EventTypeQuery, EventType.class)
                        .build();

        Adapter_Event_Type = new Adapter_Event_Type(options, getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.recyclerView_event_type_create_event);
        recyclerView.setHasFixedSize(true); //performance reasons
        //recyclerView.s
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(Adapter_Event_Type);


        //recyclerView.setLayoutManager(layoutManager);
        //List<EventType> selectableItems = options;
        //adapter = new SelectableAdapter((SelectableViewHolder.OnItemSelectedListener) this,options,true);
        //recyclerView.setAdapter(adapter);
    }
    private void setupRecyclerViewForEventTag() {
        //2. SELECT * FROM eventTags
        //Query EventTagsQuery = FirebaseDatabase.getInstance().getReference().child("eventTags");
        //Query TagQuery = eventTagsRef.orderBy("tagName", Query.Direction.DESCENDING);
        Query TagQuery = db.collection("eventTags");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Points to user_photo
            final StorageReference imagesRef = storageRef.child("events");

            // spaceRef now points to "users/userID.jpg"
            final StorageReference spaceRef = imagesRef.child(eventID + ".jpg");

            // load pciture into imageview
            Glide.with(CreateEventActivity.this)
                    .load(filePath)
                    .transform(new CenterCrop())
                    .into(stockImage);

            spaceRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG, spaceRef + " uploaded to " + imagesRef);
                    Toast.makeText(CreateEventActivity.this, "Picture uploaded", Toast.LENGTH_SHORT).show();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error uploading profile pic!");
                            Toast.makeText(CreateEventActivity.this, "Error uploading", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadPicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Points to user_photo
        StorageReference imagesRef = storageRef.child("events");

        StorageReference spaceRef = imagesRef.child(eventID + ".jpg");

        spaceRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(CreateEventActivity.this)
                            .load(task.getResult())
                            .apply(new RequestOptions().override(600, 200))
                            .into(stockImage);
                }
            }
        });
    }
}