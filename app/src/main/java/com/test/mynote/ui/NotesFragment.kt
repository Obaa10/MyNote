package com.test.mynote.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.MainActivity
import com.test.mynote.NoteDetails
import com.test.mynote.R
import com.test.mynote.adapter.NoteListAdapter
import com.test.mynote.adapter.SwipeToDeleteCallback
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import java.util.*

class NotesFragment : Fragment() {
    private val newNoteActivityRequestCode = 1
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        val addButton: FloatingActionButton = view.findViewById(R.id.add_button)
        val noNoteToShowText: TextView = view.findViewById(R.id.no_note_text)
        val noNoteToShowImage: ImageView = view.findViewById(R.id.no_note_image)
        val searchButton: ImageButton = view.findViewById(R.id.search)
        val filterButton: ImageButton = view.findViewById(R.id.filter_button)
        //Define the recyclerView and it's adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.notes_list)
        var checkedItems = booleanArrayOf(true, true, true)
        noteViewModel =
            NoteViewModelFactory(activity!!.application).create(NoteViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val recyclerViewAdapter = NoteListAdapter(noteViewModel)
        recyclerView.adapter = recyclerViewAdapter
        val swipeHandler = object : SwipeToDeleteCallback(activity!!.applicationContext, noteViewModel, MainActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                recyclerViewAdapter.completedNote(
                    viewHolder.adapterPosition,
                    view
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        val filter = MutableLiveData<Int>(0)
        val mNotes = arrayListOf<Note>()


        //Set the visibility of "noNoteToShow" && Observe the movie list
        filter.observe(this) {
            noteViewModel.allNote.observe(this) { notes ->
                notes.let { list ->
                    mNotes.addAll(notes!!)
                    noteViewModel.yearList.clear()
                    var mList = list?.sortedBy { Date(it.year, it.month, it.day) }
                    val checkedId = arrayListOf<Int>(5, 5, 5)
                    checkedItems.forEachIndexed { index, b -> if (b) checkedId[index] = index + 1 }
                    mList = mList?.filter { checkedId.contains(it.important) }
                    recyclerViewAdapter.submitList(mList)
                    if (mList?.size != 0) {
                        noNoteToShowText.visibility = View.INVISIBLE
                        noNoteToShowImage.visibility = View.INVISIBLE
                    } else {
                        noNoteToShowText.visibility = View.VISIBLE
                        noNoteToShowImage.visibility = View.VISIBLE
                    }
                }
            }
        }

        //Add new note
        addButton.setOnClickListener {
            val intent = Intent(activity, NoteDetails::class.java)
            startActivityForResult(intent, 1)
        }

        //Search note
        searchButton.setOnClickListener {
            SimpleSearchDialogCompat<Note>(activity, "Search by title",
                "Enter the title", null, mNotes,
                SearchResultListener<Note> { _, item, _ ->
                    val intent = Intent(activity, NoteDetails::class.java)
                    intent.putExtra(NoteDetails.EXTRA_REPLY_ID, item.id)
                    it.context.startActivity(intent)
                }).show()
        }

        //Filter button
        filterButton.setOnClickListener {
            checkedItems = getImportant(checkedItems, filter)
        }

        return view
    }

    private fun getImportant(
        booleanArray: BooleanArray,
        mutableLiveData: MutableLiveData<Int>
    ): BooleanArray {
        val listItems = arrayOf("Normal", "Important", "Very Important")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Important")
        builder.setMultiChoiceItems(listItems, booleanArray)
        { dialog, which, isChecked ->
            booleanArray[which] = isChecked
        }

        builder.setPositiveButton("Ok")
        { dialog, which -> mutableLiveData.value = 1 }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
        return booleanArray
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intentData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK && intentData != null) {
            intentData.getStringArrayExtra(NoteDetails.EXTRA_REPLY)?.let { reply ->
                intentData.getIntegerArrayListExtra(NoteDetails.EXTRA_REPLY_DATE)?.let { date ->
                    val image = intentData.getStringArrayListExtra("image") ?: arrayListOf("")
                    val important = date[3]
                    date.removeAt(3)
                    val note = Note(reply[0], reply[1], image, date, important)
                    noteViewModel.insert(note)
                }
            }
        } else {
            Toast.makeText(
                activity!!.applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}