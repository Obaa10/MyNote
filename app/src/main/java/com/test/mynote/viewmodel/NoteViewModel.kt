package com.test.mynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.test.mynote.database.Note
import com.test.mynote.database.NoteRoomDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class NoteViewModel (val application : Application) : ViewModel() {

    private val noteRoomDatabase = NoteRoomDatabase.getDatabase(application)
    private val dao = noteRoomDatabase.noteDao()
    var allNote : LiveData<List<Note>?>

    init {
        allNote = dao.getAllNote().asLiveData()
    }

    fun insert(note: Note) {
        viewModelScope.launch {
            dao.insert(note)
        }
    }
    fun getNote(noteId: Int) = dao.getNote(noteId).asLiveData()
    fun update(note: Note){
        viewModelScope.launch {
            dao.updateNote(note)
        }
    }
    fun delete(note :Note){
        viewModelScope.launch {
            dao.delete(note.id)
        }
    }
}

class NoteViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
