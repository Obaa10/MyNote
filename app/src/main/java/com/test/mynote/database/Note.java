package com.test.mynote.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collections;

import ir.mirrajabi.searchdialog.core.Searchable;

@Entity(tableName = "note_table")
public class Note implements Searchable {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "detail")
    public String detail;
    @ColumnInfo(name = "images")
    public ArrayList<String> image;
    @ColumnInfo(name = "year")
    public Integer year;
    @ColumnInfo(name = "month")
    public Integer month;
    @ColumnInfo(name = "day")
    public Integer day;
    @ColumnInfo(name = "important")
    public Integer important;

    public Note(String title, String detail) {
        image.add("");
        this.title = title;
        this.detail = detail;
    }

    public Note(Integer id, String title, String detail, ArrayList<String> image, ArrayList<Integer> date, Integer important) {
        this.id = id;
        this.important = important;
        this.title = title;
        this.detail = detail;
        this.image = image;
        year = date.get(0);
        month = date.get(1);
        day = date.get(2);
    }

    public Note(Integer id, String title, String detail, ArrayList<String> image,Integer important) {
        this.image.add("");
        this.id = id;
        this.important = important;
        this.title = title;
        this.detail = detail;
        this.image.addAll(image);
    }

    public Note(String title, String detail, ArrayList<String> image, ArrayList<Integer> date,Integer important) {
        this.image = image;
        this.title = title;
        this.important = important;
        this.detail = detail;
        year = date.get(0);
        month = date.get(1);
        day = date.get(2);
    }

    public Note(String title, String detail, ArrayList<String> image,Integer important) {
        this.image.add("");
        this.important = important;
        this.image.addAll(image);
        this.title = title;
        this.detail = detail;
    }

    public Note() {
    }

    @Override
    public String getTitle() {
        return title;
    }
}