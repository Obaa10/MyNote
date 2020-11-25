package com.test.mynote

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        const val EXTRA_REPLY_DATE = "NoteDate"
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val saveButton: Button = findViewById(R.id.save_button)
        val dateButton: FloatingActionButton = findViewById(R.id.date_button)
        val addImageButton: FloatingActionButton = findViewById(R.id.add_image_button)
        noteImage = findViewById(R.id.note_image)
        val title: EditText = findViewById(R.id.detail_title)
        val detail: EditText = findViewById(R.id.detail_description)
        val detailDate: TextView = findViewById(R.id.detail_date)

        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val intent = intent
        var isEmpty = true
        var noteId = 0
        val date = arrayListOf(0, 0, 0)
        var noteLiveData: LiveData<Note>?


        //insert note
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                noteLiveData = noteViewModel.getNote(noteId)
                noteLiveData!!.observe(this) {
                    isEmpty = false
                    title.setText(it.title)
                    detail.setText(it.detail)
                    if (it.year != 0)
                        detailDate.text = it.year.toString() + "/" + it.month.toString() + "/" +
                                it.day.toString()
                    date[0] = it.year
                    date[1] = it.month
                    date[2] = it.day
                    if (it.image.isNotEmpty()) {
                        fullPhotoUri = it.image.toUri()
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
        }

        //save or update the note
        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                if (isEmpty) {
                    val noteTitle = title.text.toString()
                    val noteDetails = detail.text.toString()
                    val imageUri: String = fullPhotoUri?.toString() ?: ""
                    val array: Array<String> = arrayOf(noteTitle, noteDetails, imageUri)
                    replyIntent.putExtra(EXTRA_REPLY, array)
                    replyIntent.putExtra(EXTRA_REPLY_DATE, date)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = detail.text.toString()
                    val imageUri: String = fullPhotoUri?.toString() ?: ""
                    noteViewModel.update(Note(noteId, noteTitle, noteDetails, imageUri, date))
                }
            }
            finish()
        }

        //insert image
        addImageButton.setOnClickListener {
            requestRead()
        }

        //Insert date
        dateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datetime = DatePickerDialog(
                this,
                OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                    date[0] = years
                    date[1] = monthOfYear
                    date[2] = dayOfMonth
                    detailDate.text = years.toString() + "/" +
                            monthOfYear.toString() + "/" +
                            dayOfMonth.toString()
                }, year, month, day
            )
            datetime.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    private fun requestRead() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            readFile()
        }
    }

    private fun readFile() {
        val intent1 = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }
        startActivityForResult(intent1, 2)
    }
}

