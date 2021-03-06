package com.zakir.classy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

import util.ClassApi;
import util.UsersApi;

public class LogInActivity extends AppCompatActivity {

    private TextView createAccText;
    private EditText loginEmailText,loginPassText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        createAccText =findViewById(R.id.createAcc);
        loginButton = findViewById(R.id.login_button);
        loginEmailText = findViewById(R.id.login_email);
        loginPassText = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.loading);

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

        createAccText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,CreateAccountActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmailText.getText().toString().trim();
                String password = loginPassText.getText().toString().trim();
                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    logIn(email,password);
                }
                else{
                    //TODO :
//                    Toast.makeText(LogInActivity.this,"Your some input is not valid",Toast.LENGTH_LONG)
//                            .show();
                    Snackbar.make(v, "Fill in all the fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logIn(String email, String password) {
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                              FirebaseUser user = firebaseAuth.getCurrentUser();
                              assert user != null;
                            String currentUserId = user.getUid();

                            collectionReference.whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if(error!=null){
                                                Log.d("login",error.toString());
                                            }
                                            assert value != null;
                                            if(!value.isEmpty()){
                                                UsersApi usersApi = UsersApi.getInstance();
                                                for(QueryDocumentSnapshot snapshot : value) {
                                                    usersApi.setUserName(snapshot.getString("username"));
                                                    usersApi.setUserId(snapshot.getString("userId"));
                                                    usersApi.setEmail(snapshot.getString("email"));
                                                    usersApi.setClassId(snapshot.getString("classId"));
                                                   // usersApi.setAdmin(snapshot.getBoolean("admin"));
                                                    usersApi.setClassName(snapshot.getString("className"));

                                                }
                                                ClassApi.getInstance().setClassId(usersApi.getClassId());
                                                if(UsersApi.getInstance().getClassId()!=null){
                                                    db.collection("Classes").document(UsersApi.getInstance().getClassId())
                                                            .collection("ClassUsers").document(UsersApi.getInstance().getUserName()+"_"+ UsersApi.getInstance().getUserId())
                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(Objects.requireNonNull(task.getResult()).exists()){
                                                                UsersApi.getInstance().setAdmin(task.getResult().getBoolean("admin"));
                                                                startActivity(new Intent(LogInActivity.this,MainPageActivity.class));
                                                                progressBar.setVisibility(View.GONE);
                                                                finish();
                                                            }


                                                        }
                                                    });
                                                }else{
                                                    startActivity(new Intent(LogInActivity.this,MainPageActivity.class));
                                                    progressBar.setVisibility(View.GONE);
                                                    finish();
                                                }


                                            }
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if(e.getLocalizedMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
                                Toast.makeText(LogInActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
                            } else if(e.getLocalizedMessage().equals("The email address is badly formatted.")
                                   ||e.getLocalizedMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                               Toast.makeText(LogInActivity.this,"Your email is wrong or you do not have an account",Toast.LENGTH_LONG).show();
                           }else if(e.getLocalizedMessage().equals("The password is invalid or the user does not have a password.")){
                                Toast.makeText(LogInActivity.this,"Your password is wrong",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(LogInActivity.this,"You are temporarily blocked due to too many attempt of wrong password. Please try again later ",Toast.LENGTH_LONG).show();
                            }
                            Log.d("login",e.getLocalizedMessage());
                        }
                    });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LogInActivity.this,"Please Enter email and password",Toast.LENGTH_LONG)
                    .show();
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