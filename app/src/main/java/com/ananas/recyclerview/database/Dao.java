package com.ananas.recyclerview.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.ananas.recyclerview.entity.Upload;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


@androidx.room.Dao
public interface Dao {
    @Query("SELECT * FROM Upload")
    Flowable<List<Upload>> getAll();
    @Insert
    Completable insert(Upload v);
    @Delete
    Completable delete(Upload v);
}
