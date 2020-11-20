package com.test.mynote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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

        noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val addButton: FloatingActionButton = findViewById(R.id.add_button)

        //Define the recyclerView and it's adapter
        val recyclerView: RecyclerView = findViewById(R.id.notes_list)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(
                2
                , StaggeredGridLayoutManager.VERTICAL
            )
        val recyclerViewAdapter = NoteListAdapter()
        recyclerView.adapter = recyclerViewAdapter


        noteViewModel.allNote.observe(this) { notes ->
            notes.let { recyclerViewAdapter.submitList(it) }
        }


        addButton.setOnClickListener {
            val intent = Intent(this, NoteDetails::class.java)
            startActivityForResult(intent, 1)
        }
    }


    protected override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        var imageUri: String? = null
            if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK) {
                val isDelete = intentData?.getBooleanExtra("delete", false)!!
                if (!isDelete) {
                    intentData.getStringArrayExtra(NoteDetails.EXTRA_REPLY)?.let { reply ->
                        if (imageUri != null) {
                            val note = Note(reply[0], reply[1], imageUri)
                            noteViewModel.insert(note)
                        } else {
                            val note = Note(reply[0], reply[1])
                            noteViewModel.insert(note)
                        }
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