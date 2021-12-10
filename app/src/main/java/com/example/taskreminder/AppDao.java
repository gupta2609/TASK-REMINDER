package com.example.taskreminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Dao
public interface AppDao {
    @Query("SELECT * FROM TaskDetails")
    List<UserEntity> getAll();
    @Insert
    void insert(UserEntity userEntity);

    @Query("DELETE FROM TaskDetails WHERE Id = :taskId")
    void deleteTaskFromId(int taskId);

    @Query("SELECT * FROM TaskDetails WHERE Id = :taskId")
    UserEntity selectDataFromAnId(int taskId);

    @Query("Update TaskDetails SET Title = :sTitle,Description=:sDis,Date = :sDate,Time = :sTime Where id = :sID")
    void update(int sID, String sTitle, String sDis, String sDate, String sTime);
}
