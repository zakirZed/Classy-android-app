package com.zakir.classy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import model.Routine;
import util.ClassApi;
import util.UsersApi;

public class MakeChangeActivity extends AppCompatActivity {
    private String status,docId,makeChangeDateText,details,shiftedDate;
    private Button button;
    private TextView textView,detailsText;
    private ProgressBar progressBar;
    private Routine routine=new Routine();

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");
    private Date date1;
    private Timestamp queryTime;
    private String day,shiftedDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_change);
        button = findViewById(R.id.makeChangeButton);
        textView = findViewById(R.id.makeChangeText);
        detailsText = findViewById(R.id.detailsText);
        progressBar = findViewById(R.id.makeChangeProgressBar);
        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            status = extra.getString("status");
            docId = extra.getString("docId");
            details = extra.getString("details");
            detailsText.setText("Make change to : "+details);
        }
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(status.equals("Shifted")){
            actionBar.setTitle("Shift Class");
            textView.setVisibility(View.VISIBLE);
            button.setText("Shift it");
            button.setVisibility(View.VISIBLE);
        }else if(status.equals("Cancelled")){
            actionBar.setTitle("Cancel Class");
            button.setText("Cancel it");
            button.setVisibility(View.VISIBLE);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.equals("Shifted")){
                  if(textView.getText().equals("Click me to chose shift date")){
                      Toast.makeText(MakeChangeActivity.this,"Please chose a date to shift",Toast.LENGTH_LONG).show();
                  }else getRoutine(docId);


                }else if(status.equals("Cancelled")){
                    cancelRoutine(docId);
                }
            }
        });

    }

    private void cancelRoutine(String docId) {
        progressBar.setVisibility(View.VISIBLE);
        collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine").document(docId).update("status",status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MakeChangeActivity.this,"Successfully Cancelled",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                });
    }

    private void shiftRoutine(Routine routine) {
        collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine").document(routine.getDocId())
                .set(routine).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(MakeChangeActivity.this,"Successfully Shifted",Toast.LENGTH_LONG).show();
                    finish();

            }
        })
          .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  progressBar.setVisibility(View.INVISIBLE);
                  //TODO
              }
          })  ;

    }

    private void getRoutine(final String docId) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("MakeChange","routine docId is "+docId);
        collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine").whereEqualTo("docId",docId).get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       if(!queryDocumentSnapshots.isEmpty()){
                           for(QueryDocumentSnapshot r:queryDocumentSnapshots){

                               routine=r.toObject(Routine.class);
                               Log.d("MakeChange","rutine Image is "+routine.getCardImage());
                           }
                           routine.setDocId(shiftedDocId);
                           routine.setClassType("Shifted Class");
                           routine.setStatus("No Change");
                           routine.setQueryTime(queryTime);
                           routine.setDate(shiftedDate);
                           routine.setClassTypeShiftedDate("From : "+routine.getDay()+" "+docId);
                           routine.setDay(day);
                           routine.setIamRoutine(false);
                           Log.d("MakeChange","routine Code is "+routine.getSubjectCode());
                           shiftRoutine(routine);
                           collectionReference.document(UsersApi.getInstance().getClassId()).collection("Routine").document(docId).update("status",status,"shiftedDate","To : "+makeChangeDateText);

                       }

                   }
               })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //TODO
                    }
                });

    }


    private void showDateTimeDialog() {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        // SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");
                        String timeText = DateFormat.format("h:mm a", calendar).toString();
                        day =  DateFormat.format("EEEE", calendar).toString();
                       // makeChangeDateText =  DateFormat.format("EEEE, MMM d, yyyy HH:mm", calendar).toString();
                        String dateText1 = DateFormat.format("MMM d, yyyy HH:mm", calendar).toString();
                        String dateText2 = DateFormat.format("MMM d, yyyy", calendar).toString();
                        shiftedDate=dateText2;
                        date1 = new Date(dateText1);
                        Log.d("TS","DATE :"+date1);

                        Timestamp Timestamp = new Timestamp (date1);
                        queryTime = Timestamp;
                      //  long sec = queryTime.getSeconds();
                    //    Date d = new Date(date1.getTime()+604800000L);
                    //    Timestamp Times = new Timestamp(d);
                    //    Log.d("TS","NEXT DATE :"+d);
                        Log.d("TS","timestamp :"+Timestamp);
                   //     Log.d("TS","next timestamp :"+Times);
                        //  Date d = new Date();
                        String s= Timestamp.toDate().toString();
                        Log.d("TS","date :"+s);
                        makeChangeDateText = day+" "+dateText2+" "+timeText;
                        shiftedDocId = dateText2+" "+timeText;
                        textView.setText("Shift to : "+makeChangeDateText);

                    }
                };

                new TimePickerDialog(MakeChangeActivity .this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(MakeChangeActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}