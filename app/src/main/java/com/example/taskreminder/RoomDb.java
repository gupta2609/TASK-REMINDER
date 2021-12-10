package com.example.taskreminder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class},version = 1,exportSchema = false)
public abstract class RoomDb extends RoomDatabase {
    private static RoomDb INSTANCE;

    public static  RoomDb getDbInstance(Context context) {

        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class, "DB_NAME")
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }

    public abstract AppDao userDao();
}
