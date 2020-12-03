package com.test.mynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.test.mynote.database.Note
import com.test.mynote.database.NoteRoomDatabase
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : ViewModel() {

    //get an instance of the RoomDatabase
    private val noteRoomDatabase = NoteRoomDatabase.getDatabase(application)
    private val dao = noteRoomDatabase.noteDao()

    //List represent all the user's notes
    var allNote: LiveData<List<Note>?>

    lateinit var images : MutableLiveData<ArrayList<String>>
    //Initialize the (allNote) List
    init {
        allNote = dao.getAllNote().asLiveData()
    }

    fun insert(note: Note) {
        viewModelScope.launch {
            dao.insert(note)
        }
    }

    fun getNote(noteId: Int) = dao.getNote(noteId).asLiveData()

    fun getImage(noteId: Int) = dao.getImage(noteId).asLiveData()

    fun update(note: Note) {
        viewModelScope.launch {
            dao.updateNote(note)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            dao.delete(id)
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
