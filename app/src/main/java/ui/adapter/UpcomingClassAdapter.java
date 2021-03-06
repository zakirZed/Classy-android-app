package ui.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zakir.classy.MakeChangeActivity;
import com.zakir.classy.R;

import java.util.Calendar;
import java.util.List;

import model.Routine;
import util.UsersApi;

public class UpcomingClassAdapter extends RecyclerView.Adapter<UpcomingClassAdapter.ViewHolder> {
    private Context context;
    private List<Routine> routineList;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");

    public UpcomingClassAdapter(Context context, List<Routine> routineList) {
        this.context = context;
        this.routineList = routineList;
    }

    @NonNull
    @Override
    public UpcomingClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.upcoming_class_row, viewGroup, false);
        return new ViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(@NonNull UpcomingClassAdapter.ViewHolder viewHolder, int position) {
        Log.d("TAG","Position "+position);
        Routine routine = routineList.get(position);
        viewHolder.todayCard.setVisibility(View.GONE);
        viewHolder.noClassText.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(routine.getTodayText())){
            viewHolder.rowCard.setVisibility(View.GONE);
           if(routine.getTodayText().equals("No Class Today")||routine.getTodayText().equals("No Class")){
                viewHolder.noClassText.setText(routine.getTodayText());
                viewHolder.noClassText.setVisibility(View.VISIBLE);
            }else{
                viewHolder.todayText.setText(routine.getTodayText());
                viewHolder.todayCard.setVisibility(View.VISIBLE);
            }

        }else{
            viewHolder.todayCard.setVisibility(View.GONE);
            viewHolder.noClassText.setVisibility(View.GONE);
            viewHolder.rowCard.setVisibility(View.VISIBLE);
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

            if(routine.getStatus().equals("Shifted")) {
                viewHolder.rowCard.setCardBackgroundColor(context.getResources().getColor((R.color.startGColo)) );
                viewHolder.editButton.setVisibility(View.GONE);
                viewHolder.link1.setVisibility(View.GONE);
                viewHolder.link2.setVisibility(View.GONE);
                if(UsersApi.getInstance().isAdmin())
                    viewHolder.removeButton.setVisibility(View.VISIBLE);
            } else if(routine.getStatus().equals("Cancelled")) {
                viewHolder.rowCard.setCardBackgroundColor(context.getResources().getColor((R.color.centerGColor)));
                viewHolder.link1.setVisibility(View.GONE);
                viewHolder.link2.setVisibility(View.GONE);
                viewHolder.editButton.setVisibility(View.GONE);
                if(UsersApi.getInstance().isAdmin())
                    viewHolder.removeButton.setVisibility(View.VISIBLE);
            } else if(routine.getStatus().equals("Live")) {
                if(UsersApi.getInstance().isAdmin())
                    viewHolder.editButton.setVisibility(View.VISIBLE);
                viewHolder.rowCard.setCardBackgroundColor(context.getResources().getColor((R.color.green)));
                if(!TextUtils.isEmpty(routine.getLink1())) viewHolder.link1.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(routine.getLink2())) viewHolder.link2.setVisibility(View.VISIBLE);
                viewHolder.removeButton.setVisibility(View.GONE);

            }
            else {
                if(UsersApi.getInstance().isAdmin())
                    viewHolder.editButton.setVisibility(View.VISIBLE);
                viewHolder.rowCard.setCardBackgroundColor(context.getResources().getColor((R.color.cardColor)) );
                if(!TextUtils.isEmpty(routine.getLink1())) viewHolder.link1.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(routine.getLink2())) viewHolder.link2.setVisibility(View.VISIBLE);
                viewHolder.removeButton.setVisibility(View.GONE);
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

            viewHolder.day.setText(routine.getDay()+" "+routine.getDocId());

            viewHolder.imageText.setText(routine.getCardImage());
            viewHolder.status.setText(routine.getStatus());
            viewHolder.classType.setText(routine.getClassType());
            if(!TextUtils.isEmpty(routine.getClassTypeShiftedDate())){
                viewHolder.classTypeShiftedDate.setText(routine.getClassTypeShiftedDate());
                viewHolder.classTypeShiftedDate.setVisibility(View.VISIBLE);
            }else{
                viewHolder.classTypeShiftedDate.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(routine.getShiftedDate())){
                viewHolder.statusShiftedDate.setText(routine.getShiftedDate());
                viewHolder.statusShiftedDate.setVisibility(View.VISIBLE);
            }else{
                viewHolder.statusShiftedDate.setVisibility(View.GONE);
            }
        }




       }



    @Override
    public int getItemCount() {
        int size=routineList.size();
      //  Log.d("TAG","size "+size);
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView subName,subCode,inst1,inst2,todayText,noClassText;
        public TextView link1,link2,imageText,status,statusShiftedDate, classType, classTypeShiftedDate;
        public CardView rowCard,imageCard,todayCard;
        public Button editButton,removeButton;
      //  public ImageView instructorImage;
        public TextView day;
        String userId,userName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            subName = itemView.findViewById(R.id.subName);
            subCode = itemView.findViewById(R.id.subCode);
            inst1 = itemView.findViewById(R.id.instructorName1);
            inst2 = itemView.findViewById(R.id.instructorName2);
            day = itemView.findViewById(R.id.day);
            todayText = itemView.findViewById(R.id.todayText);
            todayCard = itemView.findViewById(R.id.todayCard);
            noClassText = itemView.findViewById(R.id.noClassTextView);
            link1 = itemView.findViewById(R.id.link1);
            link2 = itemView.findViewById(R.id.link2);
            rowCard = itemView.findViewById(R.id.rowCard);
            imageText = itemView.findViewById(R.id.imageText);
            imageCard = itemView.findViewById(R.id.imageCard);
            status = itemView.findViewById(R.id.status);
            statusShiftedDate = itemView.findViewById(R.id.statusShiftedDate);
            classType = itemView.findViewById(R.id.classType);
            classTypeShiftedDate = itemView.findViewById(R.id.classTypeShiftedDate);
            editButton = itemView.findViewById(R.id.editButton);
            removeButton = itemView.findViewById(R.id.removeButton);
           // instructorImage = itemView.findViewById(R.id.instructorImage);
            UsersApi usersApi = UsersApi.getInstance();
            Log.d("Admin","admin "+usersApi.isAdmin());
            if(usersApi.isAdmin()){
                Log.d("Admin","admin "+usersApi.isAdmin());
                editButton.setVisibility(View.VISIBLE);
            }
            editButton.setOnClickListener(this);
            removeButton.setOnClickListener(this);
            rowCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final Routine routine = routineList.get(position);
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Please wait..");
            Calendar calendar = Calendar.getInstance();
            String dateText = DateFormat.format("MMM d, yyyy", calendar).toString();
            final String s []= routine.getSubjectName().split(" ");
            switch (v.getId()){
                case R.id.editButton :
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Make Change");

                    builder.setMessage(s[0]+" "+routine.getDay()+", "+routine.getDocId());
                    builder.setPositiveButton("Shift it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           Intent intent = new Intent(context, MakeChangeActivity.class);
                           intent.putExtra("status","Shifted");
                           intent.putExtra("docId",routine.getDocId());
                           intent.putExtra("details",s[0]+", "+routine.getDay()+" "+routine.getDocId());
                           context.startActivity(intent);
                        }
                    });
//                    builder.setNeutralButton("Cancel it", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(context, MakeChangeActivity.class);
//                            intent.putExtra("status","Cancelled");
//                            intent.putExtra("docId",routine.getDocId());
//                            context.startActivity(intent);
//
//                        }
//                    });
                    builder.setNegativeButton("Cancel it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MakeChangeActivity.class);
                            intent.putExtra("status","Cancelled");
                            intent.putExtra("docId",routine.getDocId());
                            intent.putExtra("details",s[0]+", "+routine.getDay()+" "+routine.getDocId());
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    break;
                case R.id.removeButton:
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle("Remove from List");
                    dialogBuilder.setMessage("This change is for all users of this class");
                    dialogBuilder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd.show();
                            collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine")
                                    .document(routineList.get(getAdapterPosition()).getDocId()).update("inList",true);
                            Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                    dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.show();

                    break;

                case R.id.rowCard :
                   if(routine.getDate().equals(dateText) && routine.getStatus().equals("No Change")){
                       final AlertDialog.Builder liveDialogBuilder = new AlertDialog.Builder(context);
                       liveDialogBuilder.setTitle("Remind Others");
                       liveDialogBuilder.setMessage("Is class : "+s[0]+" "+routine.getDay()+", "+routine.getDocId()+" is live?");
                       liveDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               pd.show();
                               collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine")
                                       .document(routineList.get(getAdapterPosition()).getDocId()).update("status","Live");
                               Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                               pd.dismiss();
                           }
                       });
                       liveDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });
                       liveDialogBuilder.show();
                   }


            }

        }


    }
}
