package com.test.mynote.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "detail")
    public String detail;
    @ColumnInfo(name = "image")
    public String image = "" ;

    public Note(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public Note(Integer id, String title, String detail) {
        this.id = id;
        this.title = title;
        this.detail = detail;
    }

    public Note(String title, String detail, String image) {
        this.image = image;
        this.title = title;
        this.detail = detail;
    }

    public Note() {
    }
}