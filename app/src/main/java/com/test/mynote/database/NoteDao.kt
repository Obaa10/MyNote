package com.test.mynote.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert//(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT *  FROM note_table")
    fun getAllNote(): Flow<List<Note>?>

    @Query("SELECT images  FROM note_table WHERE id =:key")
    fun getImage(key: Int): Flow<List<String>?>

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    @Query("DELETE FROM note_table WHERE id =:key")
    suspend fun delete(key: Int)

    @Query("Select * from note_table WHERE id = :key")
    fun getNote(key: Int): Flow<Note>

    @Query("Select * from note_table WHERE title = :key")
    fun getNoteByTitle(key: String): Note?

    @Query("Select * from note_table WHERE archived = :key")
    fun getAllArchivedNotes(key : Boolean = true) : Flow<List<Note>?>

    @Query("Select * from note_table WHERE has_alarm = :key")
    fun getAllAlarms(key : Boolean = true) : Flow<List<Note>?>


}