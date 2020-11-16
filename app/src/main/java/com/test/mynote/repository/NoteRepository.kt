package com.test.mynote.repository

import androidx.annotation.WorkerThread
import com.test.mynote.database.Note
import com.test.mynote.database.NoteDao

class NoteRepository(private  val noteDao: NoteDao){


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(note: Note){
        noteDao.insert(note)
    }
}