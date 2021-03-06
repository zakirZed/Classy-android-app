package com.zakir.classy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import model.ClassUsers;
import model.Routine;
import util.ClassApi;
import util.UsersApi;

public class MainPageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReference2 = db.collection("Classes");

    private AppBarConfiguration mAppBarConfiguration;
    private TextView nav_className,nav_classId;
    private ProgressBar progressBar;
    private TextView mainPageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.mainPageProgressBar);
        mainPageText = findViewById(R.id.mainPageTextView);
        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()==null){
            mainPageText.setText("You have'nt joined any class yet");
            mainPageText.setVisibility(View.VISIBLE);
        }
        if(usersApi.getClassId()!=null){
            mainPageText.setVisibility(View.GONE);
        }
        View headerNavView = navigationView.getHeaderView(0);
        TextView nav_userNameTextView = headerNavView.findViewById(R.id.userNameNavHeader);
        TextView nav_userEmail = headerNavView.findViewById(R.id.userEmailNavHeader);
        nav_classId = headerNavView.findViewById(R.id.classIdHeaderText);
        nav_className = headerNavView.findViewById(R.id.classNameHeaderText);
        String Uname = usersApi.getUserName();
        String Uemail = usersApi.getEmail();
        String className = usersApi.getClassName();
        String classId = usersApi.getClassId();
        Log.d("Main","className "+UsersApi.getInstance().getClassName());
        nav_userNameTextView.setText(Uname);
        nav_userEmail.setText(Uemail);
        if(className!=null){
            nav_className.setText(className);
            nav_classId.setText(classId);
            nav_className.setVisibility(View.VISIBLE);
            nav_classId.setVisibility(View.VISIBLE);

        } else {
            nav_className.setVisibility(View.GONE);
            nav_classId.setVisibility(View.GONE);
//            nav_className.setTextSize(20);
//            nav_className.setText("Class not joined");
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_routine, R.id.nav_people)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
        currentUser = firebaseAuth.getCurrentUser();
//        ClassApi classApi = ClassApi.getInstance();
//        Toast.makeText(MainPageActivity.this,"Hello "+usersApi.getUserName()+"! Your classId is "+usersApi.getClassId(),Toast.LENGTH_LONG).show();



    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createMenu:
                 createClass();
                break;
            case R.id.signOutMenu:
                signOut();
                break;
            case R.id.joinMenu:
                joinClass();
                break;

        }
        return super.onOptionsItemSelected(item);

    }




    private void joinClass() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
        builder.setTitle("Join Class");

        final EditText editText = new EditText(MainPageActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint("ClassID");
        editText.setHintTextColor(Color.GRAY);
        builder.setView(editText);
        builder.setMessage("Enter classId");
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if(classApi.getClassId()==null) classApi.setClassId(null);
                if(!TextUtils.isEmpty(editText.getText().toString().trim())){
                    progressBar.setVisibility(View.VISIBLE);
                  //  ClassApi classApi = ClassApi.getInstance();
                    UsersApi usersApi = UsersApi.getInstance();
                 //   classApi.setClassId(editText.getText().toString());
                    usersApi.setClassId(editText.getText().toString());
                    updateUserData();


                   // showRoutine();
                //    Toast.makeText(MainPageActivity.this,"Hello "+usersApi.getUserName()+"! Your classId is "+classApi.getClassId(),Toast.LENGTH_LONG).show();

                    //  startActivity(new Intent(MainPageActivity.this,CreateClassActivity.class));
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainPageActivity.this,"Please! Enter ClassId",Toast.LENGTH_LONG).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void updateUserData() {

        final UsersApi usersApi = UsersApi.getInstance();
        collectionReference2.document(usersApi.getClassId()).collection("Routine").whereEqualTo("classId",usersApi.getClassId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot r :queryDocumentSnapshots){
                    Routine routine = r.toObject(Routine.class);
                    if(!TextUtils.isEmpty(routine.getClassName())){
                        UsersApi.getInstance().setClassName(routine.getClassName());
//                        nav_className.setText(UsersApi.getInstance().getClassName());
                        collectionReference.document(usersApi.getUserName()+"_"+usersApi.getUserId())
                                .update("classId",usersApi.getClassId(),"className",usersApi.getClassName())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ClassUsers people = new ClassUsers();
                                people.setUsersName(usersApi.getUserName());
                                people.setUsersId(usersApi.getUserId());
                                people.setAdmin(false);
                                String[] userImageString = usersApi.getUserName().split(" ");
                                String userImage="";
                                for (int j=0;j<userImageString.length;j++) {
                                    userImage = userImage + userImageString[j].charAt(0);
                                }
                                people.setUsersImage(userImage);
                                collectionReference2.document(usersApi.getClassId()).collection("ClassUsers")
                                        .document(usersApi.getUserName()+"_"+usersApi.getUserId()).set(people)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                FirebaseMessaging.getInstance().subscribeToTopic(usersApi.getClassId())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {

                                                                }
                                                                progressBar.setVisibility(View.GONE);
                                                                Log.d("Main", "success");
                                                                Toast.makeText(MainPageActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                                recreate();
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                        break;
                    }
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        //TODO
                    }
                });



    }

    private void signOut() {
        if(currentUser!=null && firebaseAuth!=null){
            firebaseAuth.signOut();
            startActivity(new Intent(MainPageActivity.this,LogInActivity.class));
            finish();
        }
    }

    private void createClass() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
        builder.setTitle("Class Name");

        final EditText editText = new EditText(MainPageActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint("Class Name");
        editText.setHintTextColor(Color.GRAY);
        builder.setView(editText);
        builder.setMessage("Enter class name with section");
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ClassApi classApi = ClassApi.getInstance();
//                if(classApi.getClassId()!=null) {
//                    classApi.setClassId(null);
//                }
                if(!TextUtils.isEmpty(editText.getText().toString().trim())){
                 //   classApi.setClassName(editText.getText().toString().trim());
                    String className = editText.getText().toString().trim();
                    Intent intent = new Intent(MainPageActivity.this, CreateClassActivity.class);
                    intent.putExtra("ClassName",className);
                    intent.putExtra("ClassId", (Bundle) null);
                    startActivity(intent);
                    //finish();
                }else{
                    Toast.makeText(MainPageActivity.this,"Please! Enter Class Name",Toast.LENGTH_LONG).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

//        builder.setPositiveButton("Create", (dialogInterface, i) -> {
//            String shoppingListName = editText.getText().toString().trim();
//            addShoppingList(shoppingListName);
//        });
//
//        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()!=null){
            mainPageText.setVisibility(View.GONE);
            nav_className.setText(usersApi.getClassName());
            nav_classId.setText(usersApi.getClassId());
            nav_className.setVisibility(View.VISIBLE);
            nav_classId.setVisibility(View.VISIBLE);
            FirebaseMessaging.getInstance().subscribeToTopic(usersApi.getClassId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (!task.isSuccessful()) {

                            }
                            Log.d("Main", "success");
                           // Toast.makeText(MainPageActivity.this, "success", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    });
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}




