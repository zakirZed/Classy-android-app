package ui.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zakir.classy.MakeChangeActivity;
import com.zakir.classy.R;

import java.util.List;

import model.ClassUsers;
import model.Routine;
import util.UsersApi;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private Context context;
    private List<ClassUsers> peopleList;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");

    public PeopleAdapter(Context context, List<ClassUsers> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.class_users_row, viewGroup, false);
        return new ViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter.ViewHolder viewHolder, int position) {
        ClassUsers people = peopleList.get(position);
       viewHolder.uImage.setText(people.getUsersImage());
       viewHolder.uName.setText(people.getUsersName());
       if(people.isAdmin())  viewHolder.isAdmin.setVisibility(View.VISIBLE);
       else viewHolder.isAdmin.setVisibility(View.GONE);

    }



    @Override
    public int getItemCount() {
        int size=peopleList.size();
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView uImage,uName,isAdmin;
        public CardView peopleCard,imageCard;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            uName = itemView.findViewById(R.id.classUsersName);
            uImage = itemView.findViewById(R.id.classUsersImage);
            isAdmin = itemView.findViewById(R.id.classAdmin);
            imageCard = itemView.findViewById(R.id.classUsersImageCard);
            peopleCard = itemView.findViewById(R.id.classUsersCard);
            if(UsersApi.getInstance().isAdmin())
              peopleCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            int position = getAdapterPosition();
            final ClassUsers people = peopleList.get(position);
            switch (v.getId()){
                case R.id.classUsersCard :
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    if(!people.isAdmin()) {
                        dialogBuilder.setPositiveButton("Make " + people.getUsersName() + " Admin", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setTitle("Adding");
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();
                                collectionReference.document(UsersApi.getInstance().getClassId()).collection("ClassUsers")
                                        .document(people.getUsersName()+"_"+people.getUsersId()).update("admin", true);
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();




                            }
                        });
                    }
                    else{
                        dialogBuilder.setPositiveButton("Remove " + people.getUsersName() + " from Admin", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setTitle("Removing");
                                progressDialog.setMessage("Please wait...");
                                collectionReference.document(UsersApi.getInstance().getClassId()).collection("ClassUsers")
                                        .document(people.getUsersName()+"_"+people.getUsersId()).update("admin", false);
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();


                            }
                        });

                    }
                    dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.show();

                    break;


            }

        }


    }
}
