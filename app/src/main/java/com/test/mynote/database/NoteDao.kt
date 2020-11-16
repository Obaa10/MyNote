package com.test.mynote.database

import android.icu.text.CaseMap
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert//(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT *  FROM note_table")
     fun getAllNote(): Flow<List<Note>?>

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    @Query("DELETE FROM note_table WHERE id =:key")
    suspend fun delete(key: Int)

    @Query("Select * from note_table WHERE id = :key")
    fun getNote(key :Int): Flow<Note>
}