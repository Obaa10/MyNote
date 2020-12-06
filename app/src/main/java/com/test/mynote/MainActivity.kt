package com.test.mynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.adapter.NoteListAdapter
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private val newNoteActivityRequestCode = 1
    private lateinit var noteViewModel: NoteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val addButton: FloatingActionButton = findViewById(R.id.add_button)
        val noNoteToShowText: TextView = findViewById(R.id.no_note_text)
        val noNoteToShowImage: ImageView = findViewById(R.id.no_note_image)
        val searchButton : ImageButton = findViewById(R.id.search)

        //Define the recyclerView and it's adapter
        val recyclerView: RecyclerView = findViewById(R.id.notes_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerViewAdapter = NoteListAdapter(noteViewModel)
        recyclerView.adapter = recyclerViewAdapter


        //Set the visibility of "noNoteToShow" && Observe the movie list
        noteViewModel.allNote.observe(this) { notes ->
            notes.let { list ->
                noteViewModel.yearList.clear()
                val mList = list?.sortedBy { Date(it.year,it.month,it.day)  }
                recyclerViewAdapter.submitList(mList)
                if (list?.size != 0) {
                    noNoteToShowText.visibility = View.INVISIBLE
                    noNoteToShowImage.visibility = View.INVISIBLE
                } else {
                    noNoteToShowText.visibility = View.VISIBLE
                    noNoteToShowImage.visibility = View.VISIBLE
                }
            }
        }

        //Add new note
        addButton.setOnClickListener {
            val intent = Intent(this, NoteDetails::class.java)
            startActivityForResult(intent, 1)
        }

        //Search note
        searchButton.setOnClickListener {

           // noteViewModel.getNoteByTitle()
        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intentData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK && intentData != null) {
            intentData.getStringArrayExtra(NoteDetails.EXTRA_REPLY)?.let { reply ->
                intentData.getIntegerArrayListExtra(NoteDetails.EXTRA_REPLY_DATE)?.let { date ->
                    val image = intentData.getStringArrayListExtra("image")?: arrayListOf("")
                    val important = date[3]
                    date.removeAt(3)
                    val note = Note(reply[0], reply[1], image, date,important)
                    noteViewModel.insert(note)
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}