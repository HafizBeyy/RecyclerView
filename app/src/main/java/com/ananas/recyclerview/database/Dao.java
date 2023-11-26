package com.ananas.recyclerview.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ananas.recyclerview.entity.Upload;

import java.util.List;


@androidx.room.Dao
public interface Dao {
    @Query("SELECT comment FROM Upload")
    List<String> getComment();
    @Query("SELECT image FROM Upload")
    List<String> getImage();


    @Insert
    void insert(Upload v);
    @Delete
    void delete(Upload v);
}
