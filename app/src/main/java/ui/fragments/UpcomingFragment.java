package ui.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zakir.classy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Routine;
import ui.adapter.UpcomingClassAdapter;
import util.UsersApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {
    private List<Routine> routineList;
    private RecyclerView recyclerView;
    private UpcomingClassAdapter upcomingClassAdapter;


    private ProgressBar mainPageProgressBar;
    private TextView mainPageText;



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReference2 = db.collection("Classes");


    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_upcoming, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainPageProgressBar = view.findViewById(R.id.mainPageProgressBar);
        mainPageText = view.findViewById(R.id.mainPageTextView);
        routineList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.upComingRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


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



    }


    private void showRoutine() {
        mainPageProgressBar.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE,1);

        final String nextDay = DateFormat.format("EEEE", calendar2).toString();
        final String Day = DateFormat.format("EEEE", calendar).toString();
        final String Date1 = DateFormat.format("MMM d, yyyy", calendar).toString();
        final String nextDate = DateFormat.format("MMM d, yyyy", calendar2).toString();
        String Date2 = DateFormat.format("MMM d, yyyy HH:", calendar).toString();
        Log.d("Routine","Date is "+Date1);
        Date date2 = new Date(Date2);
        Log.d("Routine","next date :"+nextDate);

        Timestamp timestamp = new Timestamp (date2);
        UsersApi usersApi = UsersApi.getInstance();
        routineList.clear();
        collectionReference2.document(usersApi.getClassId()).collection("Routine").whereEqualTo("inList",false).whereGreaterThanOrEqualTo("queryTime",timestamp).orderBy("queryTime").limit(30L)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        routineList.clear();
                        if(error!=null) {
                            //TODO
                        }
                        Routine routine1 = new Routine();
                        boolean tomorrow = true;
                        boolean otherDay = true;
                        routine1.setTodayText("Today");

                        Log.d("Routine","today "+routine1.getTodayText());
                        routineList.add(routine1);
                        Log.d("Routine","today "+routineList.get(0).getTodayText());
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot r : queryDocumentSnapshots){
                                if(r.toObject(Routine.class).getDate().equals(nextDate)&&tomorrow){
                                    Log.d("Routine","tomorrow "+routineList.get(0).getTodayText());
                                    Routine routine3=new Routine();
                                    routine3.setTodayText("Tomorrow");
                                    if(routineList.size()==1) {
                                        Routine routine =new Routine();
                                        routine.setTodayText("No Class Today");
                                        routineList.add(routine);
                                    };
                                    routineList.add(routine3);
                                    tomorrow=false;
                                }
                                if(!r.toObject(Routine.class).getDate().equals(nextDate)&&!r.toObject(Routine.class).getDate().equals(Date1)&&otherDay){
                                   Routine routine2=new Routine();
                                   routine2.setTodayText("Other Day");
                                   if(routineList.size()==1||routineList.get(routineList.size()-1).getDate().equals(Date1)){
                                       Routine routine =new Routine();
                                       if(routineList.size()==1){
                                           routine.setTodayText("No Class Today");
                                           routineList.add(routine);
                                       }

                                       Routine routine3=new Routine();
                                       routine3.setTodayText("Tomorrow");
                                       routineList.add(routine3);
                                       tomorrow=false;

                                   }

                                   if((routineList.get(routineList.size()-1).getTodayText()!=null)&&(routineList.get(routineList.size()-1).getTodayText().equals("Tomorrow"))){
                                       Routine routine = new Routine();
                                       routine.setTodayText("No Class");
                                       routineList.add(routine);
                                   }
                                    routineList.add(routine2);
                                    otherDay=false;
                                }

                                Routine  routine = r.toObject(Routine.class);
                                routineList.add(routine);
                            }
                            Log.d("Routine","today "+routineList.get(0).getTodayText());

                            upcomingClassAdapter = new UpcomingClassAdapter(getContext(),
                                    routineList);
                          //  Log.d("Routine","today "+routineList.get(3).getTodayText());
                            recyclerView.setAdapter(upcomingClassAdapter);
                         //   Log.d("Routine","today "+routineList.get(6).getTodayText());
                            upcomingClassAdapter.notifyDataSetChanged();
                            mainPageProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            mainPageProgressBar.setVisibility(View.INVISIBLE);
                            mainPageText.setText("No Class at all");
                            mainPageText.setVisibility(View.VISIBLE);
                        }



                    }
                });
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            for(QueryDocumentSnapshot r : queryDocumentSnapshots){
//                                routine = r.toObject(Routine.class);
//                                Log.d("DDDD","isShifted "+routine.isShifted());
//                                routineList.add(routine);
//                            }
//                            upcomingClassAdapter = new UpcomingClassAdapter(getContext(),
//                                    routineList);
//                            recyclerView.setAdapter(upcomingClassAdapter);
//                            upcomingClassAdapter.notifyDataSetChanged();
//                            mainPageProgressBar.setVisibility(View.INVISIBLE);
//                        }
//                        else{
//                            mainPageProgressBar.setVisibility(View.INVISIBLE);
//                            mainPageText.setText("Hurray! No Class Today");
//                            mainPageText.setVisibility(View.VISIBLE);
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //TODO
//                        mainPageProgressBar.setVisibility(View.INVISIBLE);
//                    }
//                });
    }

    @Override
    public void onStart() {
       UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()!=null)
            showRoutine();

        super.onStart();
    }
}
