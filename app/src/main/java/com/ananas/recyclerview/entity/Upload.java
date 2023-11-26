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
    public String imageuri;

    public String getComment() {
        return comment;
    }
    public String getImageuri(){
        return imageuri;
    }

    public Upload(String comment, String imageuri) {
        this.comment = comment;
        this.imageuri = imageuri;
    }
}
