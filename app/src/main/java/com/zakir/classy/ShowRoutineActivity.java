package com.zakir.classy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Routine;
import ui.adapter.RoutineClassAdapter;
import ui.adapter.UpcomingClassAdapter;
import util.UsersApi;

public class ShowRoutineActivity extends AppCompatActivity {
    private List<Routine> routineList;
    private RecyclerView recyclerView;
    private RoutineClassAdapter routineClassAdapter;
    private String day;


    private ProgressBar progressBar;
    private TextView text;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_routine);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            day = bundle.getString("Day");
        }
       // this.setTitle(day);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(day);
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.showRoutineProgressBar);
        text = findViewById(R.id.showRoutineTextView);
        routineList = new ArrayList<>();
        recyclerView = findViewById(R.id.showRoutineRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowRoutineActivity.this));
        UsersApi usersApi = UsersApi.getInstance();
        Log.d("Show","Hello "+usersApi.isAdmin());
        if (usersApi.getClassId() == null) {
            Log.d("Show","Hello "+usersApi.getUserName());
            text.setText("You have'nt joined any class yet");
            text.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        UsersApi usersApi = UsersApi.getInstance();
        if (usersApi.getClassId() != null)
            showRoutine();

        super.onStart();
    }

    private void showRoutine() {
        progressBar.setVisibility(View.VISIBLE);
        UsersApi usersApi = UsersApi.getInstance();
        routineList.clear();
        collectionReference.document(usersApi.getClassId()).collection("Routine").whereEqualTo("day", day).whereEqualTo("iamRoutine", true).orderBy("queryTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        routineList.clear();
                        if (error != null) {
                            //TODO
                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot r : queryDocumentSnapshots) {
                                Routine routine = r.toObject(Routine.class);
                                Log.d("Routine", "isShifted " + routine.getDay());
                                routineList.add(routine);
                            }
                            routineClassAdapter = new RoutineClassAdapter(ShowRoutineActivity.this,
                                    routineList);
                            recyclerView.setAdapter(routineClassAdapter);
                            routineClassAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            text.setText("Weekend");
                            text.setVisibility(View.VISIBLE);
                        }


                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
