package ui.fragments;


import android.os.Bundle;
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
import java.util.List;

import model.Routine;
import ui.adapter.ShiftedCancelledAdapter;
import ui.adapter.UpcomingClassAdapter;
import util.UsersApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class CancelledClassFragment extends Fragment {
    private List<Routine> routineList;
    private RecyclerView recyclerView;
    private ShiftedCancelledAdapter shiftedCancelledAdapter;
    private  Routine routine = new Routine() ;


    private ProgressBar cancelledFragmentProgressBar;
    private TextView cancelledFragmentText;



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReference2 = db.collection("Classes");



    public CancelledClassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_cancelled_class, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        cancelledFragmentProgressBar = view.findViewById(R.id.cancelledFragmentProgressBar);
        cancelledFragmentText = view.findViewById(R.id.cancelledFragmentTextView);
        routineList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.cancelledClassRecyclerView);
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

//        UsersApi usersApi = UsersApi.getInstance();
//        if(usersApi.getClassId()==null){
//            cancelledFragmentText.setText("You have'nt joined any class yet");
//            cancelledFragmentText.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void onStart() {

        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()!=null)
            showCancelledClass();
        super.onStart();
    }

    private void showCancelledClass() {
        cancelledFragmentProgressBar.setVisibility(View.VISIBLE);
        UsersApi usersApi = UsersApi.getInstance();
        routineList.clear();
        collectionReference2.document(usersApi.getClassId()).collection("Routine").whereEqualTo("status","Cancelled").orderBy("queryTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        routineList.clear();
                        if(error!=null) {
                            //TODO
                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot r : queryDocumentSnapshots){
                                routine = r.toObject(Routine.class);
                                //  Log.d("DDDD","isShifted "+routine.isShifted());
                                routineList.add(routine);
                            }
                            shiftedCancelledAdapter = new ShiftedCancelledAdapter(getContext(),
                                    routineList);
                            recyclerView.setAdapter(shiftedCancelledAdapter);
                            shiftedCancelledAdapter.notifyDataSetChanged();
                            cancelledFragmentText.setVisibility(View.INVISIBLE);
                            cancelledFragmentProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else{

                            cancelledFragmentProgressBar.setVisibility(View.INVISIBLE);
                            cancelledFragmentText.setText("No Cancelled Class");
                            cancelledFragmentText.setVisibility(View.VISIBLE);
                        }



                    }
                });

    }
}

