package com.example.taskreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    FloatingActionButton add;
    RoomDb db;
    List<UserEntity> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSavedTask();
        add = findViewById(R.id.addTask);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AddTask.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
    }

    public void getSavedTask(){
        class GetSavedTasks extends AsyncTask<Void, Void, List<UserEntity>> {
            @Override
            protected List<UserEntity> doInBackground(Void... voids) {
                datalist = RoomDb.getDbInstance(getApplicationContext()).userDao().getAll();
                return datalist;
            }

            @Override
            protected void onPostExecute(List<UserEntity> tasks) {
                super.onPostExecute(tasks);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }
    public void setUpAdapter(){
        recyclerView = findViewById(R.id.taskRecycler);
        mainAdapter = new MainAdapter(this,datalist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mainAdapter);
    }
}