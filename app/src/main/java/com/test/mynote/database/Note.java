package com.test.mynote.database;

import android.text.format.Time;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

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
    @ColumnInfo(name = "date")
    public String date;

    public Note(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public Note(Integer id, String title, String detail,String image,ArrayList<Integer> date) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.image = image;
        this.date = date.get(0).toString() +"/"
                + date.get(1).toString() +"/"
                + date.get(2).toString() ;
    }

    public Note(Integer id, String title, String detail,String image) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.image = image;
    }

    public Note(String title, String detail, String image,ArrayList<Integer> date) {
        this.image = image;
        this.title = title;
        this.detail = detail;
        this.date = date.get(0).toString() +"/"
                + date.get(1).toString() +"/"
                + date.get(2).toString() ;
    }
    public Note(String title, String detail, String image) {
        this.image = image;
        this.title = title;
        this.detail = detail;
    }

    public Note() {
    }
}