package com.zakir.classy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import util.UsersApi;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        final Button getStarted = findViewById((R.id.getStartedButton));
        firebaseAuth = FirebaseAuth.getInstance();


                authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        currentUser =firebaseAuth.getCurrentUser();
                        if(currentUser!=null){
                            getStarted.setVisibility(View.GONE);
                            currentUser = firebaseAuth.getCurrentUser();
                            final String currentUserId = currentUser.getUid();

                            collectionReference
                                    .whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if(error!=null){
                                                return;
                                            }
                                            String name;
                                            assert value != null;
                                            if(!value.isEmpty()){
                                                for(QueryDocumentSnapshot snapshot : value){
                                                    UsersApi usersApi = UsersApi.getInstance();
                                                    usersApi.setUserId(snapshot.getString("userId"));
                                                    usersApi.setUserName(snapshot.getString("username"));
                                                    usersApi.setEmail(snapshot.getString("email"));
                                                    usersApi.setClassId(snapshot.getString("classId"));
                                                  //  usersApi.setAdmin(snapshot.getBoolean("admin"));
                                                    usersApi.setClassName(snapshot.getString("className"));

                                                }
                                                if(UsersApi.getInstance().getClassId()!=null&&isOnline()) {
                                                    db.collection("Classes").document(UsersApi.getInstance().getClassId())
                                                            .collection("ClassUsers").document(UsersApi.getInstance().getUserName()+"_"+UsersApi.getInstance().getUserId())
                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(Objects.requireNonNull(task.getResult()).exists()){
                                                                UsersApi.getInstance().setAdmin(Objects.requireNonNull(task.getResult()).getBoolean("admin"));
                                                                startActivity(new Intent(MainActivity.this,
                                                                        MainPageActivity.class));
                                                                finish();
                                                            }


                                                        }
                                                    });
                                                } else if(!isOnline()){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                    builder.setTitle("Warning");
                                                    builder.setMessage("Network not available");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivity(new Intent(MainActivity.this,
                                                                    MainPageActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();

                                                }else{
                                                    startActivity(new Intent(MainActivity.this,
                                                            MainPageActivity.class));
                                                    finish();
                                                }

                                            }
                                        }
                                    });


                        }else{
                            getStarted.setVisibility(View.VISIBLE);
                            getStarted.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MainActivity.this,LogInActivity.class));
                                    finish();
                                }
                            });
                        }
                    }
                };

            }




//        }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
//    public void setUpFirebaseAuth(){
//
//
//
//    }

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