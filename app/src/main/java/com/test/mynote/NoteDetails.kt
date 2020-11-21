package com.test.mynote

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.io.FileDescriptor

class NoteDetails : AppCompatActivity() {

    lateinit var noteImage: ImageView
    var fullPhotoUri: Uri? = null

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
        val deleteButton: FloatingActionButton = findViewById(R.id.delete_button)
        val addImageButton: FloatingActionButton = findViewById(R.id.add_image_button)
        noteImage = findViewById(R.id.imageView2)
        val title: EditText = findViewById(R.id.detail_title)
        val description: EditText = findViewById(R.id.detail_description)
        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val intent = intent
        var isEmpty = true
        var noteId = 0
        var noteLiveData: LiveData<Note>? = null


        //insert note
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                noteLiveData = noteViewModel.getNote(noteId)
                noteLiveData!!.observe(this) {
                    isEmpty = false
                    title.setText(it.title)
                    description.setText(it.detail)
                    if (it.image.isNotEmpty()) {
                        val parcelFileDescriptor: ParcelFileDescriptor? =
                            contentResolver.openFileDescriptor(it.image.toUri(), "r")
                        parcelFileDescriptor?.let {
                            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                            val original = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                            noteImage.setImageBitmap(original)
                        }
                    }
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
                    val imageUri: String = fullPhotoUri?.toString() ?: ""
                    val array: Array<String> = arrayOf(noteTitle, noteDetails, imageUri)
                    replyIntent.putExtra(EXTRA_REPLY, array)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = description.text.toString()
                    val imageUri: String = fullPhotoUri?.toString() ?: ""
                    noteViewModel.update(Note(noteId, noteTitle, noteDetails, imageUri))
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

        //insert image
        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 2)
        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            fullPhotoUri = data.data
            val parcelFileDescriptor: ParcelFileDescriptor? =
                fullPhotoUri?.let { contentResolver.openFileDescriptor(it, "r") }
            parcelFileDescriptor?.let {
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val original = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                noteImage.setImageBitmap(original)
            }
        }
    }
}

