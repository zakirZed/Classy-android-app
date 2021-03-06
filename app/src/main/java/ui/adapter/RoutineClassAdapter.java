package ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zakir.classy.R;

import java.util.List;

import model.Routine;
import util.UsersApi;

public class RoutineClassAdapter extends RecyclerView.Adapter<RoutineClassAdapter.ViewHolder> {
    private Context context;
    private List<Routine> routineList;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");


    public RoutineClassAdapter (Context context, List<Routine> routineList) {
        this.context = context;
        this.routineList = routineList;
    }
    @NonNull
    @Override
    public RoutineClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.upcoming_class_row, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineClassAdapter.ViewHolder viewHolder, int position) {
        Routine routine = routineList.get(position);
            if(!TextUtils.isEmpty(routine.getLink1())){
                viewHolder.link1.setText(routine.getLink1());
                viewHolder.link1.setVisibility(View.VISIBLE);
            }else{
                viewHolder.link1.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(routine.getLink2())){
                viewHolder.link2.setText(routine.getLink2());
                viewHolder.link2.setVisibility(View.VISIBLE);
            }else{
                viewHolder.link2.setVisibility(View.GONE);
            }


            viewHolder.subName.setText(routine.getSubjectName());
            if(!TextUtils.isEmpty(routine.getSubjectCode())){
                viewHolder.subCode.setText("("+routine.getSubjectCode()+")");
                viewHolder.subCode.setVisibility(View.VISIBLE);
            }else{
                viewHolder.subCode.setVisibility(View.GONE);
            }
            viewHolder.inst1.setText(routine.getInstructor1());
            if(!TextUtils.isEmpty(routine.getInstructor2())){
                viewHolder.inst2.setText(routine.getInstructor2());
                viewHolder.inst2.setVisibility(View.VISIBLE);
            }else{
                viewHolder.inst2.setVisibility(View.GONE);
            }
            String s[]=routine.getDocId().split(" ");

            viewHolder.day.setText(routine.getDay()+" "+s[3]+" "+s[4]);

            viewHolder.imageText.setText(routine.getCardImage());




    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView subName,subCode,inst1,inst2,todayText;
        public TextView link1,link2,imageText,status,statusShiftedDate, classType, classTypeShiftedDate;
        public CardView rowCard,imageCard,todayCard;
        public RelativeLayout upcomingRelative1;
        public Button editButton,removeButton;
        public TextView day;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            subName = itemView.findViewById(R.id.subName);
            subCode = itemView.findViewById(R.id.subCode);
            inst1 = itemView.findViewById(R.id.instructorName1);
            inst2 = itemView.findViewById(R.id.instructorName2);
            day = itemView.findViewById(R.id.day);
            link1 = itemView.findViewById(R.id.link1);
            link2 = itemView.findViewById(R.id.link2);
            imageText = itemView.findViewById(R.id.imageText);
            imageCard = itemView.findViewById(R.id.imageCard);
            editButton = itemView.findViewById(R.id.editButton);
            upcomingRelative1 = itemView.findViewById(R.id.upcomingRelative1);
            upcomingRelative1.setVisibility(View.GONE);
            UsersApi usersApi = UsersApi.getInstance();
            Log.d("Admin","admin "+usersApi.isAdmin());
            if(usersApi.isAdmin()){
                Log.d("Admin","admin "+usersApi.isAdmin());
                editButton.setVisibility(View.VISIBLE);
            }
            editButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final Routine routine = routineList.get(position);
            switch (v.getId()) {
                case R.id.editButton:
                    //TODO
                    break;
            }

        }
    }
}
