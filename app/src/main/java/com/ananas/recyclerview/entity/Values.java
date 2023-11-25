package com.ananas.recyclerview.entity;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Values {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "comment")
    public String comment;
    @ColumnInfo(name = "imagedata")
    public Uri imageData;
}
