package com.test.mynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.test.mynote.database.Note
import com.test.mynote.database.NoteRoomDatabase
import kotlinx.coroutines.launch

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

    fun update(note: Note){
        viewModelScope.launch {
            dao.delete(note)
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
