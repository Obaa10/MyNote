package com.test.mynote

import android.Manifest
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        var deleteImage: MutableLiveData<Int?> = MutableLiveData(null)
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
        val timeButton: ImageButton = findViewById(R.id.time_button)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val imageList: RecyclerView = findViewById(R.id.image_list)
        imageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val imageListAdapter = ImageAdapter()
        imageList.adapter = imageListAdapter
        val title: EditText = findViewById(R.id.detail_title)
        val detail: EditText = findViewById(R.id.detail_description)
        val detailDate: TextView = findViewById(R.id.detail_date)
        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val intent = intent
        var isEmpty = true
        var noteId = 0
        val date = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0)
        var noteLiveData: LiveData<Note>?
        var noteDate = Date()

        //Insert note
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
                    noteDate = Date(it.year, it.month, it.year)
                    if (it.image.size > 0) {
                        images.value = it.image
                    }
                    when (it.important) {
                        1 -> findViewById<RadioButton>(R.id.radio_1).isChecked = true
                        2 -> findViewById<RadioButton>(R.id.radio_2).isChecked = true
                        3 -> findViewById<RadioButton>(R.id.radio_3).isChecked = true
                    }
                }
            }
        }

        //Save or update the note
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
                    date[3] = getImportant(radioGroup)
                    replyIntent.putExtra(EXTRA_REPLY_DATE, date)
                    replyIntent.putExtra("image", imageUri)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val noteTitle = title.text.toString()
                    val noteDetails = detail.text.toString()
                    val imageUri: ArrayList<String> = images.value ?: arrayListOf("")
                    noteViewModel.update(
                        Note(
                            noteId,
                            noteTitle,
                            noteDetails,
                            imageUri,
                            date,
                            getImportant(radioGroup)
                        )
                    )
                }
            }
            finish()
        }

        //Insert image
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
            timeButton.visibility = View.VISIBLE
        }

        //Add alarm
        timeButton.setOnClickListener {
            val mDate = getTime()
            date[4] = mDate[0]
            date[5] = mDate[1]
            date[6] = mDate[2]
            date[7] = mDate[3]
            val day = date[2]-date[4]
            val month = date[1]-date[5]
            val year = date[0]-date[6]
            val alarmMgr =
                getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, NotifyByDate::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            val time = Calendar.getInstance()
            time.timeInMillis = System.currentTimeMillis()
            time.add(Calendar.SECOND, 10)
//            time.add(Calendar.DAY_OF_MONTH, day)
//            time.add(Calendar.MONTH, month)
//            time.add(Calendar.YEAR, year)
            alarmMgr[AlarmManager.RTC_WAKEUP, time.timeInMillis] = pendingIntent
        }

        /* //Remove image
         removeButton.setOnClickListener {
             fullPhotoUri = null
             noteImage.setImageDrawable(null)
             removeButton.visibility = View.INVISIBLE
         }
    */

        deleteImage.observe(this)
        {
            if (images.value != null && it != null)
                images.value!!.removeAt(it)
        }

        images.observe(this)
        {
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

    private fun getImportant(radioGroup: RadioGroup): Int {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_2 -> {
                2
            }
            R.id.radio_3 -> {
                3
            }
            else -> {
                1
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val mlist = images.value ?: arrayListOf("")
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

    private fun getTime(): ArrayList<Int> {
        val listItems = arrayOf("Two days", "One day", "One hour", "Custom time")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Notify me..")
        val mDate = arrayListOf<Int>(0, 0, 0, 0)
        mBuilder.setSingleChoiceItems(listItems, -1,
            DialogInterface.OnClickListener { dialogInterface, i ->
                when (i) {
                    1 -> mDate[2] = 2
                    2 -> mDate[2] = 1
                    3 -> mDate[3] = 1
                    4 -> {
                        val myDate = getDate()
                        mDate[0] = myDate.year
                        mDate[1] = myDate.month
                        mDate[2] = myDate.day
                    }
                }
                dialogInterface.dismiss()
            })
        val mDialog = mBuilder.create()
        mDialog.show()
        return mDate
    }

    private fun getDate(): Date {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var date = Date(year, month, day)
        val datetime = DatePickerDialog(
            this,
            OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                date = Date(year, monthOfYear, dayOfMonth)
            }, year, month, day
        )
        datetime.show()
        return date
    }
}

