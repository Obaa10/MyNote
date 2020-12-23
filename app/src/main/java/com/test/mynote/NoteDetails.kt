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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.test.mynote.notifyactivity.NotifyByDate
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.io.FileDescriptor
import java.util.*
import kotlin.math.absoluteValue


class NoteDetails : AppCompatActivity() {

    companion object {
        const val EXTRA_REPLY = "NoteTitle"
        const val EXTRA_REPLY_ID = "NoteId"
        const val EXTRA_REPLY_DATE = "NoteDate"
        const val EXTRA_REPLY_IMAGES = "image"
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
        var deleteImage: MutableLiveData<Int?> = MutableLiveData(null)
    }

    private lateinit var images: MutableLiveData<ArrayList<String>>
    val date = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0,0,0,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        images = MutableLiveData(arrayListOf(""))
        val dateButton: ImageButton = findViewById(R.id.date_button)
        val alarmButton: ImageButton = findViewById(R.id.time_button)
        val saveButton: FloatingActionButton = findViewById(R.id.save_button)
        val addImageButton: FloatingActionButton = findViewById(R.id.add_image_button)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val noteTitle: EditText = findViewById(R.id.detail_title)
        val noteDetail: EditText = findViewById(R.id.detail_description)
        val noteDate: TextView = findViewById(R.id.detail_date)
        val noteViewModel = NoteViewModelFactory(application).create(NoteViewModel::class.java)
        val intent = intent

        val imageList: RecyclerView = findViewById(R.id.image_list)
        imageList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        val imageListAdapter = ImageAdapter()
        imageList.adapter = imageListAdapter

        var isEmpty = true
        var noteId = 0
        var noteLiveData: LiveData<Note>?
        var nDate = Date()
        var hasAlarm = false

        //Insert note
        intent?.let {
            noteId = intent.getIntExtra(EXTRA_REPLY_ID, 0)
            if (noteId > 0) {
                noteLiveData = noteViewModel.getNote(noteId)
                noteLiveData!!.observe(this) {
                    isEmpty = false
                    hasAlarm = it.hasAlarm
                    noteTitle.setText(it.title)
                    noteDetail.setText(it.detail)
                    if (it.year != 0) {
                        noteDate.text = "${it.year}/${it.month}/${it.day}"
                        alarmButton.visibility = View.VISIBLE
                    }
                    date[0] = it.year
                    date[1] = it.month
                    date[2] = it.day
                    date[4] = it.nYear
                    date[5] = it.nMonth
                    date[6] = it.nDay
                    date[7] = it.nHours
                    nDate = Date(it.year, it.month, it.year)
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
            if (TextUtils.isEmpty(noteTitle.text)) {
                val show: Any = AlertDialog.Builder(this)
                    .setTitle("You Have To Insert Title")
                    .setMessage("Exite without Save")
                    .setPositiveButton("No"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }.setNegativeButton("Exite"
                    ) { dialog, _ ->
                        setResult(Activity.RESULT_CANCELED, replyIntent)
                        finish()
                        dialog.dismiss()
                    }.show()
            } else {
                if (isEmpty) {
                    val imageUri: ArrayList<String> = images.value ?: arrayListOf("")
                    val array: Array<String> = arrayOf(
                        noteTitle.text.toString(),
                        noteDetail.text.toString()
                    )
                    replyIntent.putExtra(EXTRA_REPLY, array)
                    date[3] = getImportant(radioGroup)
                    replyIntent.putExtra(EXTRA_REPLY_DATE, date)
                    replyIntent.putExtra(EXTRA_REPLY_IMAGES, imageUri)
                    setResult(Activity.RESULT_OK, replyIntent)
                } else {
                    val imageUri: ArrayList<String> = images.value ?: arrayListOf("")
                    noteViewModel.update(
                        Note(
                            noteId,
                            noteTitle.text.toString(),
                            noteDetail.text.toString(),
                            imageUri,
                            date,
                            getImportant(radioGroup),
                            hasAlarm
                        )
                    )
                }
                finish()
            }
            if (hasAlarm) {
                val c = Calendar.getInstance()
                val cYear = c.get(Calendar.YEAR)
                val cMonth = c.get(Calendar.MONTH)
                val cDay = c.get(Calendar.DAY_OF_MONTH)
                val alarmMgr =
                    getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(this, NotifyByDate::class.java)
                alarmIntent.putExtra("date", date)
                alarmIntent.putExtra("name", noteTitle.text.toString())
                val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
                val time = Calendar.getInstance()
                if (Date(date[0], date[1], date[2],date[7],date[8]) < Date(date[4],date[5],date[6],date[9],date[10])
                    || Date(date[4], date[5], date[6]) < Date(cYear, cMonth, cDay)) {
                } else {
                    var day =  (date[0] - date[4]) * 360 + (((date[1] - date[5]) * 30).absoluteValue + date[2] - date[6])
                    val noteDay = day
                    day = day * 24 * 60 * 60 +((((date[7]*60)+date[8])-((date[9]*60)+date[10])).absoluteValue*60)
                    time.timeInMillis = System.currentTimeMillis()
                    Toast.makeText(this, "$noteDate day left to your alarm",Toast.LENGTH_LONG).show()
                    time.add(Calendar.SECOND, (day))
                    alarmMgr[AlarmManager.RTC_WAKEUP, time.timeInMillis] = pendingIntent
                }
                noteViewModel.removeAlarm.observe(this) {
                    if (it) {
                        alarmMgr.cancel(pendingIntent)
                    }
                }
                noteViewModel.removeAlarm.value = false
            }
        }

        //Insert image
        addImageButton.setOnClickListener {
            requestRead()
        }

        //Insert date
        dateButton.setOnClickListener {
            pickDateTime(true)
            alarmButton.visibility = View.VISIBLE
        }

        //Add alarm
        alarmButton.setOnClickListener {
            pickDateTime(false)
        }

        deleteImage.observe(this)
        {
            if (images.value != null && it != null)
                images.value!!.removeAt(it)
        }

        images.observe(this)
        { listOfImages ->
            val imageBitmap: ArrayList<Bitmap?> = arrayListOf()
            for (image in listOfImages) {
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

    override fun onBackPressed() {
        val show: Any = AlertDialog.Builder(this)
            .setTitle("Note will not be save")
            .setMessage("Are you sour you want to leave ?")
            .setPositiveButton("YES"
            ) { dialog, _ -> // The user wants to leave - so dismiss the dialog and exit
                finish()
                dialog.dismiss()
            }.setNegativeButton("BACK"
            ) { dialog, _ -> // The user is not sure, so you can exit or just stay
                dialog.dismiss()
            }.show()

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
            val listOfImages = images.value ?: arrayListOf("")
            val list = arrayListOf(data.data.toString())
            list.addAll(listOfImages)
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

    private fun pickDateTime(isDate : Boolean) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                if(isDate) {
                    date[0] = year
                    date[1] = month
                    date[2] = day
                    date[7] = hour
                    date[8] = minute
                }
                else{
                    when {
                        Date(date[0], date[1], date[2],date[7],date[8]) < Date(year,month,day,hour,minute) -> {
                            alarmIllogical(false)
                        }
                        Date(date[4], date[5], date[6]) < Date(startYear, startMonth, startDay) -> {
                            alarmIllogical(true)
                        }
                        else -> {
                            date[4] = year
                            date[5] = month
                            date[6] = day
                            date[9] = hour
                            date[10] = minute
                        }
                    }
                }
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun alarmIllogical(before : Boolean){
        val error = if(before) "Alarm is in the past"
        else "Alarm is after the note's date"
        val show: Any = AlertDialog.Builder(this)
            .setTitle("Alarm time is illogical")
            .setMessage(error)
            .setPositiveButton("OK"
            ) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    /*  private fun getTime(date: ArrayList<Int>) {
          val listItems = arrayOf("Two days", "One day", "One hour", "Custom time")
          val mBuilder = AlertDialog.Builder(this)
          mBuilder.setTitle("Notify me..")
          val mDates = arrayListOf<Int>(0, 0, 0, 0)
          mBuilder.setSingleChoiceItems(listItems, -1,
              DialogInterface.OnClickListener { dialogInterface, i ->
                  when (i) {
                      0 -> {
                          mDates[2] = date[2] - 2
                          mDates[0] = date[0]
                          mDates[1] = date[1]
                      }
                      1 -> {
                          mDates[2] = date[2] - 1
                          mDates[0] = date[0]
                          mDates[1] = date[1]
                      }
                      2 -> {
                          mDates[3] = 1
                          mDates[2] = date[2]
                          mDates[0] = date[0]
                          mDates[1] = date[1]
                      }
                      3 -> {
                          val c = Calendar.getInstance()
                          val year = c.get(Calendar.YEAR)
                          val month = c.get(Calendar.MONTH)
                          val day = c.get(Calendar.DAY_OF_MONTH)
                          val datetime = DatePickerDialog(
                              this,
                              OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                                  mDates[0] = years
                                  mDates[1] = monthOfYear
                                  mDates[2] = dayOfMonth
                                  mDate.value=mDates
                              }, year, month, day
                          )
                          datetime.show()
                      }
                  }
                  mDate.value = mDates
                  dialogInterface.dismiss()
              })
          val mDialog = mBuilder.create()
          mDialog.show()
      }*/
}

