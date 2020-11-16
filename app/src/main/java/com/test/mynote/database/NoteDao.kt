package com.test.mynote.database

import android.icu.text.CaseMap
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(vararg  note: Note)

    @Query("SELECT *  FROM note_table")
     fun getAllNote(): Flow<List<Note>?>

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(vararg note: Note)
}