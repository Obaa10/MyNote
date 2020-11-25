package com.test.mynote.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "detail")
    public String detail;
    @ColumnInfo(name = "image")
    public String image = "";
    @ColumnInfo(name = "year")
    public Integer year;
    @ColumnInfo(name = "month")
    public Integer month;
    @ColumnInfo(name = "day")
    public Integer day;

    public Note(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public Note(Integer id, String title, String detail, String image, ArrayList<Integer> date) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.image = image;
        year = date.get(0);
        month = date.get(1);
        day = date.get(2);
    }

    public Note(Integer id, String title, String detail, String image) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.image = image;
    }

    public Note(String title, String detail, String image, ArrayList<Integer> date) {
        this.image = image;
        this.title = title;
        this.detail = detail;
        year = date.get(0);
        month = date.get(1);
        day = date.get(2);
    }

    public Note(String title, String detail, String image) {
        this.image = image;
        this.title = title;
        this.detail = detail;
    }

    public Note() {
    }
}