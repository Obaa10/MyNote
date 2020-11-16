package com.test.mynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.adapter.NoteListAdapter
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    private val newNoteActivityRequestCode = 1
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.notes_list)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val recyclerViewAdapter = NoteListAdapter()
        recyclerView.adapter = recyclerViewAdapter

        noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)

        noteViewModel.allNote.observe(this) { notes ->
            notes.let { recyclerViewAdapter.submitList(it) }
        }

        val addButton: FloatingActionButton = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            val intent = Intent(this, NoteDetails::class.java)
            startActivityForResult(intent, newNoteActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringArrayExtra(NoteDetails.EXTRA_REPLY)?.let { reply ->
                val note = Note(reply[0], reply[1])
                noteViewModel.insert(note)
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