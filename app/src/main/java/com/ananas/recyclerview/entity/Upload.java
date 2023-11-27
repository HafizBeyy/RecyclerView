package com.ananas.recyclerview.entity;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Blob;

@Entity
public class Upload {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "comment")
    public String comment;
    @ColumnInfo(name = "image")
    public String imageBlob;
    public Upload(String comment, String imageBlob) {
        this.comment = comment;
        this.imageBlob = imageBlob;
    }
}
