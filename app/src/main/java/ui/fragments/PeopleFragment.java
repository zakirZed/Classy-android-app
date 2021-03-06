package ui.fragments;

import android.os.Bundle;
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

import model.ClassUsers;
import model.Routine;
import ui.adapter.PeopleAdapter;
import ui.adapter.ShiftedCancelledAdapter;
import util.UsersApi;

public class PeopleFragment extends Fragment {
    private List<ClassUsers> peopleList;
    private RecyclerView recyclerView;
    private PeopleAdapter peopleAdapter;
    private  ClassUsers people = new ClassUsers() ;


    private ProgressBar peopleFragmentProgressBar;
    private TextView peopleFragmentText;



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");



    public PeopleFragment() {
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        peopleFragmentProgressBar = view.findViewById(R.id.peopleFragmentProgressBar);
        peopleFragmentText  = view.findViewById(R.id.peopleFragmentTextView);
        peopleList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.peopleFragmentRecyclerView);
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

        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()==null){
            peopleFragmentText.setText("You have'nt joined any class yet");
            peopleFragmentText.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onStart() {

        UsersApi usersApi = UsersApi.getInstance();
        if(usersApi.getClassId()!=null)
            showClassUsers();
        super.onStart();
    }

    private void showClassUsers() {
        peopleFragmentProgressBar.setVisibility(View.VISIBLE);
        UsersApi usersApi = UsersApi.getInstance();
        collectionReference.document(usersApi.getClassId()).collection("ClassUsers").orderBy("admin", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        peopleFragmentProgressBar.setVisibility(View.VISIBLE);
                        peopleList.clear();
                        if(error!=null) {
                            //TODO
                            Log.d("PF", error.getLocalizedMessage());
                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot p : queryDocumentSnapshots){
                                people = p.toObject(ClassUsers.class);
                                peopleList.add(people);
                            }
                            peopleAdapter = new PeopleAdapter(getContext(), peopleList);
                            recyclerView.setAdapter(peopleAdapter);
                            peopleAdapter.notifyDataSetChanged();
                            peopleFragmentText.setVisibility(View.INVISIBLE);
                            peopleFragmentProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            peopleFragmentProgressBar.setVisibility(View.INVISIBLE);
                            Log.d("PF","not found");
                        }



                    }
                });

    }
}