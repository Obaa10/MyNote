package com.test.mynote

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory


class NoteDetails : AppCompatActivity() {

    companion object {
        const val EXTRA_REPLY = "NoteTitle"
        const val EXTRA_REPLY_ID = "NoteId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val saveButton: Button = findViewById(R.id.save_button)
        val deleteButton: FloatingActionButton = findViewById(R.id.add_image_button)
        val title: EditText = findViewById(R.id.detail_title)
        val description: EditText = findViewById(R.id.detail_description)
        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val intent = intent
        var isEmpty = true
        var noteId = 0
        var noteLiveData : LiveData<Note>? = null

        //insert note
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                noteLiveData = noteViewModel.getNote(noteId)
                noteLiveData!!.observe(this) {
                    isEmpty = false
                    title.setText(it.title)
                    description.setText(it.detail)
                }
            }
        }

        //save or update the note
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
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = description.text.toString()
                    noteViewModel.update(Note(noteId, noteTitle, noteDetails))
                }
            }
            finish()
        }

        //delete note
        deleteButton.setOnClickListener {
            val builder: AlertDialog.Builder? = this.let { it ->
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            val replyIntent = Intent()
                            if (TextUtils.isEmpty(title.text)) {
                                setResult(Activity.RESULT_CANCELED, replyIntent)
                            } else {
                                replyIntent.putExtra("delete", true)
                                noteLiveData?.let {
                                    if (it.hasObservers()) {
                                        noteLiveData!!.removeObservers(this@NoteDetails)
                                        setResult(Activity.RESULT_OK, replyIntent)
                                    }
                                }
                                noteViewModel.delete(noteId)
                            }
                            finish()
                        })
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                }
            }
            builder?.setMessage(R.string.dialog_message)
                ?.setTitle(R.string.dialog_title)
            val dialog: AlertDialog? = builder?.create()
            dialog!!.show()
        }
    }
}
//get image from the mobile as (Bitmap or Uri)
/*
 fun selectImage() {
      val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
          type = "image/*"
      }
      if (intent.resolveActivity(packageManager) != null) {
          val encodeByte: ByteArray = Base64.decode(imageUr, Base64.DEFAULT)
          val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
          startActivityForResult(intent, REQUEST_IMAGE_GET)
      }
    }
 */