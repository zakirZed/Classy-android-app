package com.zakir.classy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import model.ClassUsers;
import model.Routine;
import util.ClassApi;
import util.UsersApi;


public class CreateClassActivity extends AppCompatActivity implements View.OnClickListener {
    private String cName,classId;
    private  int  dayId =0,dateListSize=0;
    private EditText sName,sCode,inst1,inst2,link1,link2;
    private CardView progCard;
    private TextView cardTime, cardDay,cardDate1,cardDate2,cardDate3,cardDate4,cardDate5,cardDate6;
    private ImageView addImage,removeImage;
   // private Timestamp queryTime;
    private LinearLayout addDayLayout;
    private int[] dayLayoutArray = new int[]{
            R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6
    };
    private Routine[] dateList = new Routine[6];
    private ArrayList<Integer> dateArrayList = new ArrayList<>(Arrays.asList(R.id.editDate1, R.id.editDate2, R.id.editDate3,
            R.id.editDate4, R.id.editDate5,R.id.editDate6));
    private ArrayList<Integer> timeArrayList = new ArrayList<>(Arrays.asList(R.id.editTime1, R.id.editTime2, R.id.editTime3,
            R.id.editTime4, R.id.editTime5,R.id.editTime6));

    private static final String TAG = "CreateClassActivity";

    private Date timestampDate;

    private Button saveButton,doneButton;
   // private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Classes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

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


        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            cName = extra.getString("ClassName");
            classId = extra.getString("ClassId");

        }
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(cName);
        actionBar.setDisplayHomeAsUpEnabled(true);


     //   cardDay = findViewById(R.id.editDay1);
        progCard = findViewById(R.id.editProgressBarCard);
        sName = findViewById(R.id.editsubName);
        sCode = findViewById(R.id.editsubCode);
        inst1 = findViewById(R.id.editInstructorName1);
        inst2 = findViewById(R.id.editInstructorName2);
        link1 = findViewById(R.id.editLink1);
        link2 = findViewById(R.id.editLink2);
        cardDate1 = findViewById(R.id.editDate1);
        cardDate2 = findViewById(R.id.editDate2);
        cardDate3 = findViewById(R.id.editDate3);
        cardDate4 = findViewById(R.id.editDate4);
        cardDate5 = findViewById(R.id.editDate5);
        cardDate6 = findViewById(R.id.editDate6);
      //  cardTime = findViewById(R.id.editTime1);
        saveButton = findViewById(R.id.editSaveButton);
        doneButton = findViewById(R.id.doneButton);
        addImage = findViewById(R.id.addImage);
        removeImage = findViewById(R.id.removeImage);
      //  addMoreDay = findViewById(R.id.addMoreDay);
//        removeDay1 = findViewById(R.id.removeDay1);
//        removeDay2 = findViewById(R.id.removeDay2);
//        removeDay3 = findViewById(R.id.removeDay3);
//        removeDay4 = findViewById(R.id.removeDay4);
//        removeDay5 = findViewById(R.id.removeDay5);
//        day1=findViewById(R.id.day1);
//        day2=findViewById(R.id.day2);
//        day3=findViewById(R.id.day3);
//        day4=findViewById(R.id.day4);
//        day5=findViewById(R.id.day5);
//        day6=findViewById(R.id.day6);
       // progressBar = findViewById(R.id.editProgressBar);

        cardDate1.setOnClickListener(this);
        cardDate2.setOnClickListener(this);
        cardDate3.setOnClickListener(this);
        cardDate4.setOnClickListener(this);
        cardDate5.setOnClickListener(this);
        cardDate6.setOnClickListener(this);
      //  cardTime.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        addImage.setOnClickListener(this);
        removeImage.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(dateArrayList.contains(v.getId())){
            showDateTimeDialog(dateArrayList.indexOf(v.getId()));

        }
        switch (v.getId()){
//            case R.id.editDate1:
//            case R.id.editTime1:
//                // setDate();
//                showDateTimeDialog();
//                break;
//          //    setTime();
            case R.id.addImage :
                if(dayId<5){
                    dayId++;
                    addDayLayout = findViewById(dayLayoutArray[dayId]);
                    addDayLayout.setVisibility(View.VISIBLE);
                    removeImage.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.removeImage :
                if(dayId>0){
                    addDayLayout = findViewById(dayLayoutArray[dayId]);
                    cardDay = findViewById(dateArrayList.get(dayId));
                    cardTime = findViewById(timeArrayList.get(dayId));
                    if(!TextUtils.isEmpty(cardDay.getText().toString().trim())){
                        dateList[dayId]=null;
                        dateListSize--;
                        cardDay.setText(null);
                        cardTime.setText(null);
                    }
                    addDayLayout.setVisibility(View.GONE);
                    dayId--;
                    if(dayId==0) removeImage.setVisibility(View.GONE);
                }

                break;
            case R.id.editSaveButton :
                Routine routine = new Routine();
                String subName = sName.getText().toString().trim();
                String subCode = sCode.getText().toString().trim();
                String instruct1 = inst1.getText().toString().trim();
                String instruct2 = inst2.getText().toString().trim();
                String Link1 = link1.getText().toString().trim();
                String Link2 = link2.getText().toString().trim();
                if(!TextUtils.isEmpty(subName)&&!TextUtils.isEmpty(instruct1)&&dateListSize==dayId+1) {

                  for(int index = 0;index<dateListSize;index++){
                      String instImage1="",instImage2="";
                       progCard.setVisibility(View.VISIBLE);
                      String date = dateList[index].getDate();
                      String time=dateList[index].getTime();
                       routine.setSubjectName(subName);
                       if(!TextUtils.isEmpty(subCode))  routine.setSubjectCode(subCode);
                       if(!TextUtils.isEmpty(instruct1)){
                           routine.setInstructor1(instruct1);
                           int i=0;
                           String[] s1 = instruct1.split(" ");
                           if (((s1[0].equals("Dr.")) || (s1[0].equals("Dr")))) i=1;
                           for (int j=i;j<s1.length;j++) {
                               instImage1 = instImage1 + s1[j].charAt(0);
                           }
                           routine.setCardImage(instImage1);

                       }
                       if(!TextUtils.isEmpty(instruct2)){
                           routine.setInstructor2(instruct2);
                           int i=0;
                           String[] s2 = instruct2.split(" ");
                           if (((s2[0].equals("Dr.")) || (s2[0].equals("Dr")))) i=1;
                           for (int j=i;j<s2.length;j++) {
                               instImage2 = instImage2 + s2[j].charAt(0);
                           }

                       }
                       if(!TextUtils.isEmpty(Link1))  routine.setLink1(instImage1+" : "+Link1);
                       if(!TextUtils.isEmpty(Link2))  routine.setLink2(instImage2+" : "+Link2);

                       ClassApi classApi = ClassApi.getInstance();
                       if(classApi.getClassId()!=null) routine.setClassId(classApi.getClassId());
                       routine.setDay(dateList[index].getDay());
                       routine.setClassName(cName);
                       routine.setClassType("Usual Class");
                       routine.setStatus("No Change");
                       routine.setIamRoutine(true);
                       Timestamp queryTime = dateList[index].getQueryTime();
                       Calendar calendar=Calendar.getInstance();
                       for(int i=0;i<12;i++){
                           progCard.setVisibility(View.VISIBLE);

                           routine.setDate(date);
                           routine.setDocId(date+" "+time);
                           if(queryTime!=null) routine.setQueryTime(queryTime);
                           Log.d("TS","for loopStart "+date);
                           saveToFireStore(routine,index+i);
                           routine.setIamRoutine(false);
                           Date nextDate =new Date((queryTime.getSeconds()*1000L)+604800000L);
                           Timestamp nextTimestamp = new Timestamp(nextDate);
                           queryTime = nextTimestamp;
                           //  String s= queryTime.toDate().toString();
                           calendar.setTime(nextDate);
                           date = DateFormat.format("MMM d, yyyy", calendar).toString();
                           Log.d("TS","for loopEnd "+date);

                       }


                   }
               }else{
                    Snackbar.make(v,"Your some required field is empty",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.doneButton :
                ClassApi classApi = ClassApi.getInstance();
                if(classApi.getClassId()!=null){
                    showD();
                }
                else
                    Toast.makeText(CreateClassActivity.this,"Please enter your routine first",Toast.LENGTH_LONG).show();

                break;
        }

    }

    private void saveToFireStore(Routine r, final int i) {
        if(r!=null){
            progCard.setVisibility(View.VISIBLE);
            ClassApi classApi = ClassApi.getInstance();
            UsersApi usersApi = UsersApi.getInstance();
            if(classId==null){
                String cId = collectionReference.document()
                        .getId();
                r.setClassId(cId);
                Log.d("Main", "cId "+cId);
                classId = cId;
                classApi.setClassId(cId);
                usersApi.setClassId(cId);
            }

           collectionReference.document(classApi.getClassId()).collection("Routine").document(r.getDocId()).set(r)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           if(i==dateListSize){
                               updateUi();
                               Toast.makeText(CreateClassActivity.this,"Saved",Toast.LENGTH_LONG).show();
                               progCard.setVisibility(View.INVISIBLE);
                           }

                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           //TODO
                       }
                   });
        }

    }

    private void updateUi() {
                           sName.setText(null);
                           sCode.setText(null);
                           inst1.setText(null);
                           inst2.setText(null);
                           link1.setText(null);
                           link2.setText(null);
                           for(int i = 0;i<dateListSize;i++){
                               addDayLayout = findViewById(dayLayoutArray[i+1]);
                               cardDay = findViewById(dateArrayList.get(i));
                               cardTime = findViewById(timeArrayList.get(i));
                                   cardDay.setText(null);
                                   cardTime.setText(null);
                               addDayLayout.setVisibility(View.GONE);
                               dateList[i]=null;
                           }
                           removeImage.setVisibility(View.GONE);
                           dateListSize=0;
                           dayId=0;
    }

//    private void setDate() {
//        Calendar calendar = Calendar.getInstance();
//        int YEAR = calendar.get(Calendar.YEAR);
//        int MONTH = calendar.get(Calendar.MONTH);
//        int DATE = calendar.get(Calendar.DATE);
//        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
//        int MINUTE = calendar.get(Calendar.MINUTE);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
//
//                Calendar calendar1 = Calendar.getInstance();
//                calendar1.set(Calendar.YEAR, year);
//                calendar1.set(Calendar.MONTH, month);
//                calendar1.set(Calendar.DATE, date);
//               // String dateText = DateFormat.format("EEEE, MMM d, yyyy", calendar1).toString();
//               String dateText1 = DateFormat.format("MM/dd/yyyy", calendar1).toString();
//               Date date1 = new Date(dateText1);
//                Log.d("TS","DATE :"+date1);
//               Timestamp Timestam = new Timestamp (date1);
//
//               Log.d("TS","timestamp :"+Timestam);
//             //  Date d = new Date();
//               String s= Timestam.toDate().toString();
//               Log.d("TS","date :"+s);
//                cardDate.setText(dateText1);
//            }
//        }, YEAR, MONTH, DATE);
//
//        datePickerDialog.show();
//    }
//
//    private void setTime() {
//        Calendar calendar = Calendar.getInstance();
//        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
//        int MINUTE = calendar.get(Calendar.MINUTE);
//        Log.i(TAG, "onTime: " + HOUR +":" +MINUTE);
//        boolean is24HourFormat = DateFormat.is24HourFormat(this);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//
//                Calendar calendar1 = Calendar.getInstance();
//                calendar1.set(Calendar.HOUR_OF_DAY, hour);
//                calendar1.set(Calendar.MINUTE, minute);
//                Log.i(TAG, "onTimeSet: " + hour+":" +minute);
//                String dateText2 = DateFormat.format("h:mm a", calendar1).toString();
//                cardTime.setText(dateText2);
//            }
//        }, HOUR, MINUTE,is24HourFormat);
//
//        timePickerDialog.show();
//
//    }

    private void showDateTimeDialog(final int index) {
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
                       String day = DateFormat.format("EEEE", calendar).toString();
                        String timestampDateText = DateFormat.format("MMM d, yyyy HH:mm", calendar).toString();
                        String dateText = DateFormat.format("MMM d, yyyy", calendar).toString();
                        timestampDate = new Date(timestampDateText);
                        Log.d("TS","DATE :"+ timestampDate);
                        Timestamp timestamp = new Timestamp (timestampDate);
                       // queryTime = timestamp;
//                        long sec = queryTime.getSeconds();
//                        Date d = new Date(date1.getTime()+604800000L);
//                        Timestamp Times = new Timestamp(d);
//                        Log.d("TS","NEXT DATE :"+d);
//                        Log.d("TS","timestamp :"+timestamp);
//                        Log.d("TS","next timestamp :"+Times);
//                          Date d = new Date();
//                        String s= timestamp.toDate().toString();
//                        Log.d("TS","date :"+s);


                        //cardDate.setText(simpleDateFormat.format(calendar.getTime()));
                      //  cardDate.setText(dateText);
                        Routine routine = new Routine();
                        routine.setDate(dateText);
                        routine.setTime(timeText);
                        routine.setQueryTime(timestamp);
                        routine.setDay(day);
                        if(dateList[index]==null) dateListSize++;
                        dateList[index]=routine;
                        Log.d(TAG,"dateListSize"+" "+dateListSize);
                        Log.d(TAG,"index"+" "+index);
                        cardDay = findViewById(dateArrayList.get(index));
                        cardTime = findViewById(timeArrayList.get(index));
                        cardDay.setText(day);
                        cardTime.setText(timeText);
                       // cardDay.setText(day);
                    }
                };

                new TimePickerDialog(CreateClassActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(CreateClassActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void showD() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this);
        builder.setCancelable(false);
        builder.setTitle("ClassID");
        final TextView textView = new TextView(CreateClassActivity.this);
        textView.setHintTextColor(Color.GRAY);
        builder.setView(textView);
        builder.setMessage("Please copy this classId to join");
        textView.setPadding(150,0,0,0);
        textView.setTextSize(15L);
        UsersApi usersApi = UsersApi.getInstance();
       // UsersApi.getInstance().setClassId(classApi.getClassId());
        textView.setText(usersApi.getClassId());
        textView.setTextIsSelectable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progCard.setVisibility(View.VISIBLE);
                updateUserData();
                finish();

            }
        });
        builder.show();
    }

    private void updateUserData() {
        Log.d("Main", "success1");
        final UsersApi usersApi = UsersApi.getInstance();
        usersApi.setAdmin(true);
        usersApi.setClassName(cName);
        db.collection("Users").document(usersApi.getUserName()+"_"+usersApi.getUserId())
                .update("classId",usersApi.getClassId(),"className",cName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Main", "success2");
                ClassUsers people = new ClassUsers();
                people.setUsersName(usersApi.getUserName());
                people.setUsersId(usersApi.getUserId());
                people.setAdmin(true);
                String[] userImageString = usersApi.getUserName().split(" ");
                String userImage="";
                for (int j=0;j<userImageString.length;j++) {
                    userImage = userImage + userImageString[j].charAt(0);
                }
                people.setUsersImage(userImage);
                collectionReference.document(usersApi.getClassId()).collection("ClassUsers")
                        .document(usersApi.getUserName()+"_"+usersApi.getUserId()).set(people)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Main", "success");
                                FirebaseMessaging.getInstance().subscribeToTopic(usersApi.getClassId())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {

                                                }
                                                Log.d("Main", "success");
                                                Toast.makeText(CreateClassActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                progCard.setVisibility(View.GONE);
                                                finish();

                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progCard.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progCard.setVisibility(View.GONE);
                        Log.e(TAG,e.getLocalizedMessage());
                    }
                });

    }
}