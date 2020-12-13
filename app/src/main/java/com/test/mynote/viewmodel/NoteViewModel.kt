package com.test.mynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.test.mynote.database.Note
import com.test.mynote.database.NoteRoomDatabase
import kotlinx.coroutines.launch
import java.time.Month
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class NoteViewModel(application: Application) : ViewModel() {

    //get an instance of the RoomDatabase
    private val noteRoomDatabase = NoteRoomDatabase.getDatabase(application)
    private val dao = noteRoomDatabase.noteDao()
    val removeAlarm = MutableLiveData<Boolean>(false)
    val editAlarm = MutableLiveData<ArrayList<Int>>(arrayListOf(-1,0,0,0,0,0,0))
    val yearList = arrayListOf<Date>()
    var isCompleted = MutableLiveData<Boolean>(false)
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

    fun updateAlarm(id: Int,year: Int,month: Int,day :Int){
        viewModelScope.launch {
            dao.updateAlarm(id, year, month, day)
        }
    }

    fun getNoteByTitle(title : String) = dao.getNoteByTitle(title)

    fun getNote(noteId: Int) = dao.getNote(noteId).asLiveData()

    fun getImage(noteId: Int) = dao.getImage(noteId).asLiveData()

    fun getAllArchivedNotes() = dao.getAllArchivedNotes().asLiveData()

    fun getAllAlarms() = dao.getAllAlarms().asLiveData()

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
