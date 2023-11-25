package com.ananas.recyclerview.database;

import androidx.room.RoomDatabase;

import com.ananas.recyclerview.entity.Upload;

@androidx.room.Database(entities = {Upload.class},version = 1)
public abstract class Database extends RoomDatabase {
public abstract Dao dao();
public static volatile Database INSTANCE;
}
