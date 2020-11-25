package com.test.mynote.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.NoteDetails
import com.test.mynote.R
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import java.util.*

class NoteListAdapter(private val noteViewModel: NoteViewModel) :
    ListAdapter<Note, NoteListAdapter.ViewHolder>(NOTES_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.create(note, noteViewModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val date: TextView = view.findViewById(R.id.date)
        private val deleteButton: Button = view.findViewById(R.id.button)
        private val cardColor: TextView = view.findViewById(R.id.color)

        fun create(note: Note, noteViewModel: NoteViewModel) {
            title.text = note.title
            date.text = if (note.month > 0) {
                note.year.toString() + "/" + note.month.toString() + "/" +
                        note.day.toString()
            } else null

            //Set the corner's card color
            val rnd = Random()
            val color: Int = Color.argb(
                255,
                rnd.nextInt(256),
                rnd.nextInt(256),
                rnd.nextInt(256)
            )
            cardColor.setBackgroundColor(color)


            itemView.setOnClickListener {
                val intent = Intent(it.context, NoteDetails::class.java)
                intent.putExtra(NoteDetails.EXTRA_REPLY_ID, note.id)
                it.context.startActivity(intent)
            }

            deleteButton.setOnClickListener { view ->

                //Set Dialog
                val builder: AlertDialog.Builder? = this.let {
                    val builder = AlertDialog.Builder(view.context)
                    builder.apply {
                        setPositiveButton(
                            R.string.ok
                        ) { _, _ ->
                            noteViewModel.delete(note.id)
                        }
                        setNegativeButton(
                            R.string.cancel
                        ) { _, _ ->
                        }
                    }
                }
                builder?.setMessage(R.string.dialog_message)
                    ?.setTitle(R.string.dialog_title)
                val dialog: AlertDialog? = builder?.create()
                dialog!!.show()
            }
        }
    }


    companion object {
        private val NOTES_COMPARATOR = object : DiffUtil.ItemCallback<Note>() {
            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }
}

