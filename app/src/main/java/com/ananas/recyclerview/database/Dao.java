package com.ananas.recyclerview.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ananas.recyclerview.entity.Upload;

import java.util.List;


@androidx.room.Dao
public interface Dao {
    @Query("SELECT * FROM Upload")
    List<Upload> getAll();
    @Insert
    void insert(Upload v);
    @Delete
    void delete(Upload v);
}
