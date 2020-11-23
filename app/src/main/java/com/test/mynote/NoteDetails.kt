package com.test.mynote

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.io.FileDescriptor
import java.util.*


class NoteDetails : AppCompatActivity() {

    lateinit var noteImage: ImageView
    private var fullPhotoUri: Uri? = null

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
        val date = arrayListOf<Int>(0,0,0)
        var addDate = false

        //insert note
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                noteLiveData = noteViewModel.getNote(noteId)
                noteLiveData!!.observe(this) {
                    isEmpty = false
                    title.setText(it.title)
                    description.setText(it.detail)
                    date[0]=it.year
                    date[1]=it.month
                    date[2]=it.day
                    if (it.image.isNotEmpty()) {
                        fullPhotoUri = it.image.toUri()
                        Picasso.get().load(fullPhotoUri).resize(0, noteImage.width).onlyScaleDown()
                            .centerInside().into(noteImage)
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
                    replyIntent.putExtra("date",date)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = description.text.toString()
                    val imageUri: String = fullPhotoUri?.toString() ?: ""
                    if(date[0]!=0)
                        noteViewModel.update(Note(noteId, noteTitle, noteDetails, imageUri,date))
                    else
                        noteViewModel.update(Note(noteId, noteTitle, noteDetails, imageUri))

                }
            }
            finish()
        }

        //insert image
        addImageButton.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).
            apply {
                type = "image/*"
            }
            startActivityForResult(intent1, 2)
        }

        //delete note
        deleteButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datetime = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    date[0]=year
                    date[1]=monthOfYear
                    date[2]=dayOfMonth
                    addDate=true
                }, year, month, day
            )
            datetime.show()
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


/*
  val builder: AlertDialog.Builder? = this.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                }
            }
            builder?.setMessage(R.string.dialog_message)
                ?.setTitle(R.string.dialog_title)
            val dialog: AlertDialog? = builder?.create()
            dialog!!.show()
 */