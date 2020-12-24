package com.test.mynote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.R
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel

class UpcomingAlarmsAdapter(private val noteViewModel: NoteViewModel) :
    ListAdapter<Note, UpcomingAlarmsAdapter.ViewHolder>(NOTES_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_card, parent, false)
        return ViewHolder(cardView, noteViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.create(note)
    }

    class ViewHolder(view: View, val viewModel: NoteViewModel) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.date_title)
        private val date: TextView = view.findViewById(R.id.date_date)
        fun create(note: Note) {
            title.text = note.title
            val stringDate =
                if(note.nYear*note.nMonth!=0) "${note.nYear}/${note.nMonth}/${note.nDay}"
                else ""
            date.text = stringDate
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
