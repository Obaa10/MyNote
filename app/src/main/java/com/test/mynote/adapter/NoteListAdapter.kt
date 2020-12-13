package com.test.mynote.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.mynote.NoteDetails
import com.test.mynote.R
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import java.util.*

class NoteListAdapter(private val noteViewModel: NoteViewModel) :
    ListAdapter<Note, NoteListAdapter.ViewHolder>(NOTES_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_card, parent, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.create(note, noteViewModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.note_title)
        private val date: TextView = view.findViewById(R.id.date)
        private val detail: TextView = view.findViewById(R.id.card_detail)
        private val deleteButton: ImageButton = view.findViewById(R.id.button)
        private val cardColor: TextView = view.findViewById(R.id.color)
        private val cardView : CardView = view.findViewById(R.id.note_card)

        fun create(note: Note, noteViewModel: NoteViewModel) {
            title.text = note.title
            detail.text = note.detail
            date.text = if (note.month > 0) {
                if (!noteViewModel.yearList.contains(Date(note.year, note.month, note.day))) {
                    val date = Date(note.year, note.month, note.day)
                    noteViewModel.yearList.add(date)
                    "${note.year}/${note.month}/${note.day}"
                } else ""
            } else null

            //Set the corner's card color
            val color: Int = when (note.important) {
                1 -> R.color.normal
                2 -> R.color.important
                3 -> R.color.very_important
                else -> R.color.normal
            }
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

    fun completedNote(id: Int, view: View) {
        val note = getItem(id)
        if (note.completed) {
            note.archived=true
            noteViewModel.update(note)
            notifyItemChanged(id)
            val snackbar: Snackbar = Snackbar.make(
                view, R.string.snack_bar_delete_text,
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction(R.string.snack_bar_undo) { v -> noteViewModel.insert(note) }
            snackbar.show()
        } else {
            note.completed=true
            noteViewModel.isCompleted.value=true
            noteViewModel.update(note)
            notifyItemChanged(id)
            val snackbar: Snackbar = Snackbar.make(
                view, R.string.snack_bar_completed_text,
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction(R.string.snack_bar_non) { v -> note.completed=false }
            snackbar.show()
        }
    }
}