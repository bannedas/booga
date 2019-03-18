package com.example.Booga;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";
    EditText signUpEditTextEmail, signUpEditTextPassword, signUpEditFirstName, singUpEditLastName;
    Button buttonSignUpWithFacebook, buttonLogin;
    //ProgressBar signUpProgressBar;

    //This is the entry point for the Firebase Authentication SDK
    private FirebaseAuth mAuth;
    //init firestore database sdk
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //This code is not needed for now. but we might need it later
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        //assign db to our instance
        db = FirebaseFirestore.getInstance();
        //signUpProgressBar =  findViewById(R.id.buttonSignUpLoginId);
        signUpEditTextEmail = findViewById(R.id.text_view_sign_up_email_input);
        signUpEditTextPassword = findViewById(R.id.text_view_sign_up_password_input);
        signUpEditFirstName = findViewById(R.id.text_view_sign_up_name_input);
        singUpEditLastName = findViewById(R.id.text_view_sign_up_input_last_name);

        buttonSignUpWithFacebook = findViewById(R.id.buttonSignUpWithFacebookId);
        buttonSignUpWithFacebook.setOnClickListener(this);
        buttonLogin = findViewById(R.id.buttonSignUpLoginId);
        buttonLogin.setOnClickListener(this);
    }


        /*
        This method Registers the user.
         */
    private void registerUser() {
        /*
        This gets the text written by the user in the EditTextFields email and password.
         */
        String email = signUpEditTextEmail.getText().toString().trim();
        String password = signUpEditTextPassword.getText().toString().trim();
        final String firstName = signUpEditFirstName.getText().toString().trim();
        final String lastName = singUpEditLastName.getText().toString().trim();

        // If the Email EditTextField is empty show an error and point where the error is by using .requestFocus
        if (email.isEmpty()) {
            signUpEditTextEmail.setError("Email is required");
            signUpEditTextEmail.requestFocus();
            return;
        } else if (firstName.isEmpty()) {
            signUpEditFirstName.setError("First name is required");
            signUpEditFirstName.requestFocus();
            return;
        } else if (lastName.isEmpty()) {
            singUpEditLastName.setError("Last name is required");
            singUpEditLastName.requestFocus();
            return;
        }
        /*
         If the Email in the EditTextField does not match with an already registered Email in Firebase,
         show an error and point where the error is by using .requestFocus
         */
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEditTextEmail.setError("Please enter a valid email");
            signUpEditTextEmail.requestFocus();
            return;
        }
        // If the Password EditTextField is empty show an error and point where the error is by using .requestFocus
        if(password.isEmpty()) {
            signUpEditTextPassword.setError("Password is required");
            signUpEditTextPassword.requestFocus();
            return;
        }
        // If the Password EditTextField is not long enough, show an error and point where the error is by using .requestFocus
        if(password.length() < 6) {
            signUpEditTextPassword.setError("Minimum length of password should be 6");
            signUpEditTextPassword.requestFocus();
            return;
        }
        //signUpProgressBar.setVisibility(View.VISIBLE);

        /*
        This code uses the entry point for Firebase and uses the createUserWithEmailAndPassword method
        to create a user so it is saved in Firebase. For now it redirects to the MainScreenActivity,
        this we can just edit later.
         */
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //signUpProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser fireUser = mAuth.getCurrentUser(); //get user info
                    assert fireUser != null; // check if user != null
                    final String UID = fireUser.getUid(); //store user id

                    Map<String, Object> dbUser = new HashMap<>();
                    dbUser.put("first_name", firstName);
                    dbUser.put("last_name", lastName);

                    // Add a new document with a generated ID
                    db.collection("users").document(UID).set(dbUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "User added with ID: " + UID);
                        }
                    });

                    //If everything succeeds, redirect to MainScreenActivity
                    Intent intent = new Intent(SignUpActivity.this, MainScreenActivity.class);

                     /*This method ensures that the 2 activities Login and Signup gets removed from the stack,
                        which means when you are logged in and press the back button, you will not get redirected
                        to the signup screen again, but will exit the app instead
                      */
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();

                    //If User is already registered, handle CollisionException and show a Toast message
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    /*
    This method onClick makes sure that when you click on the buttons,
    they do what they are supposed to do.
     */
    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            //When Sign up button is pressed, call the method registerUser
            case R.id.buttonSignUpLoginId:
                registerUser();
                break;
        }
    }


}