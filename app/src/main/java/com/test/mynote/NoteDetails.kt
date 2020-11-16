package com.test.mynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory

class NoteDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val saveButton: Button = findViewById(R.id.save_button)
        //   val addImageButton: Button = findViewById(R.id.add_image_button)
        val title: EditText = findViewById(R.id.detail_title)
        val description: EditText = findViewById(R.id.detail_description)

        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)

        val intent = intent
        var isEmpty = true
        var noteId = 0
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                val note = noteViewModel.getNote(noteId).observe(this) {
                    isEmpty=false
                    title.setText(it.title)
                    description.setText(it.detail)
                }
            }
        }


        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                if (isEmpty) {
                    val noteTitle = title.text.toString()
                    val noteDetails = description.text.toString()
                    val array = arrayOf<String>(noteTitle, noteDetails)
                    replyIntent.putExtra(EXTRA_REPLY, array)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                else{
                    val noteTitle = title.text.toString()
                    val noteDetails = description.text.toString()
                    noteViewModel.update(Note(noteId,noteTitle,noteDetails))
                }
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "NoteTitle"
        const val EXTRA_REPLY_ID = "NoteId"

    }
}
