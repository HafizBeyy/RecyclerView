package com.ananas.recyclerview.entity;

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
    public byte[] image;

    public Upload(String comment, byte[] image) {
        this.comment = comment;
        this.image = image;
    }
}
