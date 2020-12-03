package com.test.mynote

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.adapter.ImageAdapter
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.io.FileDescriptor
import java.util.*


class NoteDetails : AppCompatActivity() {

    lateinit var images: MutableLiveData<ArrayList<String>>

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

        images = MutableLiveData(arrayListOf(""))
        val dateButton: ImageButton = findViewById(R.id.date_button)
        val saveButton: FloatingActionButton = findViewById(R.id.save_button)
        val addImageButton: FloatingActionButton = findViewById(R.id.add_image_button)
        val imageList: RecyclerView = findViewById(R.id.image_list)
        imageList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        val imageListAdapter = ImageAdapter()
        imageList.adapter = imageListAdapter
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
                    if (it.image.size > 0) {
                        images.value = it.image
                    }
                    /*for(image in it.image) {
                        fullPhotoUri = image.toUri()
                        val parcelFileDescriptor: ParcelFileDescriptor? =
                            fullPhotoUri?.let { contentResolver.openFileDescriptor(it, "r") }
                        parcelFileDescriptor?.let {
                            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                            val original = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                            noteImage.setImageBitmap(original)
                        }
                        removeButton.visibility=View.VISIBLE
                    }*/
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
                    val imageUri: ArrayList<String> = images.value ?: arrayListOf("")
                    val array: Array<String> = arrayOf(noteTitle, noteDetails)
                    replyIntent.putExtra(EXTRA_REPLY, array)
                    replyIntent.putExtra(EXTRA_REPLY_DATE, date)
                    replyIntent.putExtra("image", imageUri)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = detail.text.toString()
                    val imageUri: ArrayList<String> = images.value ?: arrayListOf("")
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

        /* //Remove image
         removeButton.setOnClickListener {
             fullPhotoUri = null
             noteImage.setImageDrawable(null)
             removeButton.visibility = View.INVISIBLE
         }
 */

        images.observe(this) {
            val imageBitmap: ArrayList<Bitmap?> = arrayListOf()
            for (image in it) {
                if (image.isNotEmpty()) {
                    val uri = image.toUri()
                    val parcelFileDescriptor: ParcelFileDescriptor? =
                        uri.let { contentResolver.openFileDescriptor(it, "r") }
                    parcelFileDescriptor?.let {
                        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                        val original = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                        imageBitmap.add(original)
                    }
                }
            }
            imageListAdapter.submitList(imageBitmap.toList())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val mlist = images.value?: arrayListOf("")
            val list = arrayListOf<String>(data.data.toString())
            list.addAll(mlist)
            images.value = list
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

