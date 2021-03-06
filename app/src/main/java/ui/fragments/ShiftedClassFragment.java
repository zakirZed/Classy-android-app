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
import com.google.firebase.firestore.Query;
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
public class ShiftedClassFragment extends Fragment {
    private List<Routine> routineList;
    private RecyclerView recyclerView;
    private ShiftedCancelledAdapter shiftedCancelledAdapter;
    private  Routine routine = new Routine() ;


    private ProgressBar shiftedFragmentProgressBar;
    private TextView shiftedFragmentText;



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReference2 = db.collection("Classes");



    public ShiftedClassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_shifted_class, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        shiftedFragmentProgressBar = view.findViewById(R.id.cancelledFragmentProgressBar);
        shiftedFragmentText  = view.findViewById(R.id.cancelledFragmentTextView);
        routineList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.shiftedClassRecyclerView);
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
//            shiftedFragmentText.setText("You have'nt joined any class yet");
//            shiftedFragmentText.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void onStart() {

        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()!=null)
            showShiftedClass();
        super.onStart();
    }

    private void showShiftedClass() {
        shiftedFragmentProgressBar.setVisibility(View.VISIBLE);
        UsersApi usersApi = UsersApi.getInstance();
        routineList.clear();
        collectionReference2.document(usersApi.getClassId()).collection("Routine").whereEqualTo("status","Shifted").orderBy("queryTime")
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
                            shiftedFragmentText.setVisibility(View.INVISIBLE);
                            shiftedFragmentProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else{

                            shiftedFragmentProgressBar.setVisibility(View.INVISIBLE);
                            shiftedFragmentText.setText("No Shifted Class");
                            shiftedFragmentText.setVisibility(View.VISIBLE);
                        }



                    }
                });

    }
}

