package com.test.mynote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NoteDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        val saveButton: Button = findViewById(R.id.save_button)
        //   val addImageButton: Button = findViewById(R.id.add_image_button)
        val title: EditText = findViewById(R.id.detail_title)
        val description: EditText = findViewById(R.id.detail_description)
        var first = "f"
        val intent = intent
        intent?.let {
            val detailArray = it.getStringArrayExtra(EXTRA_REPLY)
            detailArray?.let {
                first="e"
                title.setText(detailArray[0])
                description.setText(detailArray[1])
            }
        }

        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val noteTitle = title.text.toString()
                val noteDetails = description.text.toString()
                val array = arrayOf<String>(noteTitle, noteDetails,"e")
                replyIntent.putExtra(EXTRA_REPLY, array)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "NoteTitle"
    }
}
