package com.zakir.classy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.ClassApi;
import util.UsersApi;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText userNameEditText,emailEditText,passEditText,confPassEditText;
    private ProgressBar progressBar;
    private Button signUpButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
   // private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        userNameEditText = findViewById(R.id.sinup_user);
        emailEditText = findViewById(R.id.signup_email);
        passEditText = findViewById(R.id.signup_password);
        confPassEditText = findViewById(R.id.signup_confPassword);
        progressBar = findViewById(R.id.signup_loading);
        signUpButton = findViewById(R.id.signup_button);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null){
                    //user logged in already
                }else{
                    //no user yet
                }
            }
        };

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passEditText.getText().toString().trim();
                String confirmPass = confPassEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(username)
                        &&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)
                        &&!TextUtils.isEmpty(confirmPass)){
                    if(password.length()<6){
                        Toast.makeText(CreateAccountActivity.this,"Your password is too short, please enter at least 6 character",Toast.LENGTH_LONG)
                                .show();
                    }
                    else if(!password.equals(confirmPass)){
                        Toast.makeText(CreateAccountActivity.this,"Confirm password do not match with password",Toast.LENGTH_LONG)
                                .show();
                    }else createAccount(username,email,password,confirmPass);

                }
                else{
                    Toast.makeText(CreateAccountActivity.this,"Your some field is empty, please enter all fields",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }



    private void createAccount(final String username, final String email, String password, String confirmPass){
        if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(email)
                &&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(confirmPass)){

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();
                                //creating user map
                                Map<String, Object> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username",username);
                                userObj.put("email",email);
                                userObj.put("classId",null);
                                userObj.put("className",null);
                                //Now user data to database
                                db.collection("Users").document(username+"_"+currentUserId).set(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                                        //Global api
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        UsersApi usersApi = UsersApi.getInstance();
                                                                        usersApi.setUserId(currentUserId);
                                                                        usersApi.setUserName(username);
                                                                        usersApi.setEmail(email);
                                                                        usersApi.setClassId(null);
                                                                        usersApi.setClassName(null);
                                                                        usersApi.setAdmin(false);
                                                                        ClassApi.getInstance().setClassId(null);
                                                                    //    usersApi.setTokenId(Id);

                                                                        Intent intent = new Intent(CreateAccountActivity.this,MainPageActivity.class);
//                                                                    intent.putExtra("Name",name);
//                                                                    intent.putExtra("userId",currentUserId);
//                                                                    intent.putExtra("email",email);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }

                                                            })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                //TODO :

                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("CreateAccount", "createUserWithEmail:failure -> "+ Objects.requireNonNull(task.getException()).toString());
//                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if(e.getLocalizedMessage().equals("The email address is badly formatted.")){
                                Toast.makeText(CreateAccountActivity.this, e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }else if(e.getLocalizedMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
                                Toast.makeText(CreateAccountActivity.this, "Please check your internet connection",
                                        Toast.LENGTH_LONG).show();
                            }else if(e.getLocalizedMessage().equals("The email address is already in use by another account.")){
                                Toast.makeText(CreateAccountActivity.this, e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed, please try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                            Log.d("CreateAccount", "createUserWithEmail:failure - "+e.getLocalizedMessage());
                            //TODO
                        }
                    });
        }else{
            //somethin went wrong
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}