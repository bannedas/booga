package com.example.Booga;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventPage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EventPage";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String eventID;
    String createdBy;
    String firstName;
    String lastName;
    Context mContext = EventPage.this;

    //event list
    List<event> mList;

    ImageView eventPhoto;
    ImageView iconBack;
    ImageView createdByPhoto;

    TextView eventStreet;
    TextView eventHostName;
    TextView eventHostNameBottom;
    TextView eventPostAddress;
    TextView eventDistance;
    TextView eventTicket;
    TextView eventTime;
    TextView eventDesc;
    TextView eventTitle;

    Button attendButton;
    ToggleButton iconHeart;

    RecyclerView recyclerViewGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        // retreive eventID from intent.putExtra
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            eventID = intent.getStringExtra("ID");
        }

        // annoying stuff
        eventTitle = findViewById(R.id.text_view_event_name);
        eventPhoto = findViewById(R.id.image_view_event_picture);
        iconHeart = findViewById(R.id.toggle_button_event_favourite);
        iconBack = findViewById(R.id.image_view_icon_back);
        eventStreet = findViewById(R.id.text_view_event_street);
        eventHostName = findViewById(R.id.text_view_event_host_name);
        eventHostNameBottom = findViewById(R.id.text_view_event_host_name_bottom);
        eventPostAddress = findViewById(R.id.text_view_event_post);
        eventDistance = findViewById(R.id.text_view_event_distance);
        eventTicket = findViewById(R.id.text_view_event_ticket);
        eventTime = findViewById(R.id.text_view_event_time);
        eventDesc = findViewById(R.id.text_view_event_description);
        attendButton = findViewById(R.id.button_event_page_attend);
        createdByPhoto = findViewById(R.id.image_view_event_host_picture);

        iconBack.setOnClickListener(this);
        iconHeart.setOnClickListener(this);
        attendButton.setOnClickListener(this);

        recyclerViewGallery = findViewById(R.id.recycle_view_gallery);

        updatePicture(eventID, "events", eventPhoto, false);
        updateInfo(eventID);
        updateGalleryPictures(eventID, 3);

    }

    private void updateGalleryPictures(final String ID, int gallerySize) {
        mList = new ArrayList<>();
        for(int i = 0; i < gallerySize; i++) {
            mList.add(new event(ID));
        }
        Adapter_Photo_Cards adapter = new Adapter_Photo_Cards(mContext, mList);
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL,false));
        recyclerViewGallery.setAdapter(adapter);
    }

    private void updateInfo(final String ID) {
        //init firebase storage db
        db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("allEvents").document(ID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists()) {
                        eventTitle.setText(document.getString("title"));
                        eventStreet.setText(document.getString("location"));
                        eventPostAddress.setText("Post address");
                        eventDistance.setText("?? m");
                        eventTicket.setText("Ticket price here");
                        eventTime.setText(document.getString("time"));
                        eventDesc.setText(document.getString("description"));

                        // get createdBy id from firebase and update creator photo
                        createdBy = document.getString("createdByUserID");
                        updatePicture(createdBy, "user_photo", createdByPhoto, true);

                        // get first, last name with uid and update fields
                        getCreatorInfo(createdBy);
                    } else {
                        Log.e(TAG, "Document: " + ID + " NOT FOUND");
                    }
                }
            }
        });
    }

    private void getCreatorInfo(String UID) {
        DocumentReference docRef = db.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        firstName = document.getString("first_name");
                        lastName = document.getString("last_name");
                    }
                    eventHostName.setText(firstName + " " + lastName);
                    eventHostNameBottom.setText(eventHostName.getText().toString());
                }
            }
        });
    }

    private void updatePicture(final String ID, String folder, final ImageView photo, final boolean isRound) {
        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef1 = storage1.getReference();
        // Points to events folder in database
        final StorageReference imagesRef1 = storageRef1.child(folder);

        StorageReference spaceRef1 = imagesRef1.child(ID + ".jpg");

        spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if(isRound) {
                        Glide.with(mContext)
                                .load(task.getResult())
                                .apply(RequestOptions.circleCropTransform())
                                .into(photo);
                    } else {
                        Glide.with(mContext)
                                .load(task.getResult())
                                .into(photo);
                    }
                } else {
                    Log.e(TAG, "Image  " + ID + ".jpg NOT FOUND");

                    //load IMAGE NOT FOUND

                    StorageReference spaceRef1 = imagesRef1.child("image-not-found.png");

                    spaceRef1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Glide.with(mContext)
                                        .load(task.getResult())
                                        .into(photo);

                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_event_page_attend:
                Log.e(TAG, "Attending");
                break;
            case R.id.image_view_icon_back:
                //mimic back button
                finish();
                break;
            case R.id.toggle_button_event_favourite:
                if(iconHeart.isChecked()) {
                    Log.e(TAG, "on");
                } else {
                    Log.e(TAG, "off");
                }
                break;
        }
    }
}
