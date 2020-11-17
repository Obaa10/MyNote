package com.test.mynote.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.NoteDetails
import com.test.mynote.R
import com.test.mynote.database.Note

class NoteListAdapter() : ListAdapter<Note, NoteListAdapter.ViewHolder>(NOTES_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.create(note)
        //holder.itemView.setOnLongClickListener { }
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, NoteDetails::class.java)
            intent.putExtra(NoteDetails.EXTRA_REPLY_ID, note.id)
            it.context.startActivity(intent)
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
        fun create(note: Note) {
            title.text = note.title
            description.text = note.detail
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