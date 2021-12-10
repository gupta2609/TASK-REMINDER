package com.example.taskreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AddTask extends AppCompatActivity {

    EditText title,discription,date,time;
    Button addtask;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    int taskId;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    RoomDb db;
    public static int count=0;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        title = findViewById(R.id.addTaskTitle);
        discription = findViewById(R.id.addTaskDescription);
        date = findViewById(R.id.taskDate);
        time = findViewById(R.id.taskTime);
        addtask = findViewById(R.id.addTask);
        db = RoomDb.getDbInstance(this);
        alarmManager = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(view.getContext(),
                            (view1, year, monthOfYear, dayOfMonth) -> {
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                datePickerDialog.dismiss();
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
                return true;
            }
        });

        time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    timePickerDialog = new TimePickerDialog(view.getContext(),
                            (view12, hourOfDay, minute) -> {
                                time.setText(hourOfDay + ":" + minute);
                                timePickerDialog.dismiss();
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
                return true;
            }
        });

        taskId = getIntent().getExtras().getInt("flag");
        if(taskId!=0){
            setData(taskId);
        }

        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    createTask();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    public boolean validate(){

        if(title.getText().length()==0||discription.getText().length()==0||date.getText().length()==0||time.getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please fill all the details",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
    public void createTask(){
        UserEntity userEntity = new UserEntity();
        userEntity.setTitle(title.getText().toString());
        userEntity.setDescription(discription.getText().toString());
        userEntity.setDate(date.getText().toString());
        userEntity.setTime(time.getText().toString());
        userEntity.setStatus("ACTIVE");
        if(taskId==0){
            db.userDao().insert(userEntity);
        }
        else{
            db.userDao().update(taskId,title.getText().toString(),discription.getText().toString(),date.getText().toString(),time.getText().toString());
        }
        createAlaram();

    }
    public void setData(int id){
        class showTaskFromId extends AsyncTask<Void, Void, Void> {
            UserEntity userEntity;
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                RoomDb DB = RoomDb.getDbInstance(AddTask.this);
                userEntity = DB.userDao().selectDataFromAnId(id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                title.setText(userEntity.getTitle());
                discription.setText(userEntity.getDescription());
                date.setText(userEntity.getDate());
                time.setText(userEntity.getTime());
            }
        }
        showTaskFromId st = new showTaskFromId();
        st.execute();
    }
    public void createAlaram(){
        try {

            String[] items1 = date.getText().toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = time.getText().toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd));

            Intent alarmIntent = new Intent(AddTask.this, AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", title.getText().toString());
            alarmIntent.putExtra("DESC", discription.getText().toString());
            alarmIntent.putExtra("DATE", date.getText().toString());
            alarmIntent.putExtra("TIME", time.getText().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddTask.this,count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
                count ++;

                PendingIntent intent = PendingIntent.getBroadcast(AddTask.this, count, alarmIntent, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
                    }
                }
                count ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}