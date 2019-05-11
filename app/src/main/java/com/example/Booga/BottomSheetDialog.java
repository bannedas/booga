package com.example.Booga;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 72;
    ImageView mImageView;
    private StorageReference spaceRef; // will first set it to fireBase stock.jpg picture
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_event_bottom_sheet_layout, container, false);

        mImageView = v.findViewById(R.id.create_event_image_view);
        spaceRef = FirebaseStorage.getInstance().getReference("create_event_photo");
        databaseRef = FirebaseDatabase.getInstance().getReference("create_event_photo");

        Button upload_picture_from_gallery = v.findViewById(R.id.upload_from_gallery);
        upload_picture_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent ;//= new Intent(getActivity(),CreateEventActivity.class);
                //intent = new Intent(getActivity(),CreateEventActivity.class);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                mListener.onButtonClicked("Button 'Upload fom gallery' clicked");
                dismiss();
            }
        });
//
//        Button remove_picture = v.findViewById(R.id.remove_photo);
//        remove_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onButtonClicked("Button 'Remove photo' clicked");
//                dismiss();
//            }
//        });

        return v;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        }
        catch ( ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == -1 // RESULT_OK (for now doesn't work)
                && data != null && data.getData() != null )
        {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Points to user_photo
            StorageReference imagesRef = storageRef.child("create_event_photo");

            // Get User ID
//            FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
//            assert fireUser != null;
//            final String UID = fireUser.getUid(); //store user id

            // spaceRef now points to "create_event_photo/userID.jpg"
            //StorageReference spaceRef = imagesRef.child(USER_ID + ".jpg");
            spaceRef = imagesRef.child(System.currentTimeMillis() + ".jpg");

            spaceRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
//                    startActivity(intent);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            //Log.e(TAG, "Error uploading profile pic!");
//                            //Toast.makeText(CreateEventActivity.this, "Error uploading", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
