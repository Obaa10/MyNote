package com.test.mynote.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
        @PrimaryKey(autoGenerate = true)
        int id;
        @ColumnInfo(name = "title")
        public String title;
        @ColumnInfo(name = "detail")
        public String detail;
        public Note(){
        }
        public Note(String title, String detail) {
            this.title = title;
            this.detail = detail;
        }
    }