package com.example.taskmasterapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao{

    @Insert
    void addOne(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM Task WHERE task_name LIKE :name")
    Task findTaskByName(String name);

    @Query("SELECT * FROM Task WHERE uid LIKE :id")
    Task findTaskById(int id);

    @Query("SELECT * FROM Task")
    List<Task> findAll();
}
