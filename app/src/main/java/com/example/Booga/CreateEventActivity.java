package com.example.Booga;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetDialog.BottomSheetListener {

    TextInputLayout text_input_event_title, text_input_event_location, text_input_event_date, text_input_event_time, text_input_event_description;
    Switch private_event_switch;
    Button create_event_button;

    SelectableAdapter adapter;

    private ImageView stockImage, pink_tint;
    private final int PICK_IMAGE_REQUEST = 72;
    private Uri filePath;
    private StorageReference mStorageRef;
    private Uri downloadUri;
    Map<String, Object> newEvent;
    private Task<Uri> urlTask;

    //This is the entry point for the Firebase Authentication SDK
    //private FirebaseAuth mAuth;
    //init firestore database sdk
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventTypeRef = db.collection("eventType");
    private CollectionReference eventTagsRef = db.collection("eventTags");
    private Adapter_Event_Type Adapter_Event_Type;
    private Adapter_Tags Adapter_Tags;

    private static final String TAG = "CreateEventActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE= "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PICTURE = "pictureUrl";
    private static final String KEY_TIMESTAMP_EVENT_CREATED = "createdAt";
    private static final String KEY_USER_ID = "createdByUserID";
    private static final String KEY_PRIVATE_EVENT = "isPrivate";
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //This code is not needed for now. but we might need it later
        //FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        USER_ID = mAuth.getCurrentUser().getUid();

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
                //if(urlTask != null && urlTask.isComplete()){
                    createEvent();
                //} else {
                 //   Toast.makeText(this, "WAIT, UPLOAD IN PROGRESS", Toast.LENGTH_SHORT).show();
                //}
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

            if(filePath != null) {
                uploadImage();
                Toast.makeText(CreateEventActivity.this, "Picture was selected!", Toast.LENGTH_SHORT).show();
                newEvent.put(KEY_PICTURE, downloadUri);
            } else {
                newEvent.put(KEY_PICTURE, "https://firebasestorage.googleapis.com/v0/b/booga-69d74.appspot.com/o/create_event_photo%2Fstock.jpg?alt=media&token=d1ae8ce6-7980-4616-a99b-d781ca0094c7");
            }

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
        } else {
            text_input.setError(null);
            return true;
        }
    }

    @Override
    public void onButtonClicked(String text) {

    }

    public void create_event_picture_settings(View view){
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Picasso.get().load(filePath).into(stockImage);
            //stockImage.setImageURI(filePath); //if we don't want to use Picasso, delete line above and us this method.
            //link to read more about this: https://medium.com/@multidots/glide-vs-picasso-930eed42b81d
        }
    }
    private void uploadImage() {
        if(filePath != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + "jpg");

            urlTask = fileReference.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        downloadUri = task.getResult();
                    } else {
                        Toast.makeText(CreateEventActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}