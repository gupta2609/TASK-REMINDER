package com.example.taskreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    TextView title,description,datetime;
    Button close;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        datetime = findViewById(R.id.timeAndDate);
        close = findViewById(R.id.closeButton);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mediaPlayer.start();


        if(getIntent().getExtras() != null) {
            title.setText(getIntent().getStringExtra("TITLE"));
            description.setText(getIntent().getStringExtra("DESC"));
            datetime.setText(getIntent().getStringExtra("DATE") + ", " + getIntent().getStringExtra("TIME"));
        }
        close.setOnClickListener(view -> finish());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}