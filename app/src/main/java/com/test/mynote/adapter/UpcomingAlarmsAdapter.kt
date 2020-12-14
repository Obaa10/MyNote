package com.test.mynote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.R
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel

class UpcomingAlarmsAdapter(private val noteViewModel: NoteViewModel) :
    ListAdapter<Note, UpcomingAlarmsAdapter.ViewHolder>(NOTES_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingAlarmsAdapter.ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_card, parent, false)
        return UpcomingAlarmsAdapter.ViewHolder(cardView,noteViewModel)
    }

    override fun onBindViewHolder(holder: UpcomingAlarmsAdapter.ViewHolder, position: Int) {
        val note = getItem(position)
        holder.create(note)
    }

    class ViewHolder(view: View,val viewModel: NoteViewModel) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.date_title)
        private val date: TextView = view.findViewById(R.id.date_date)
        fun create(note: Note) {
            title.text = note.title
            val stringDate =
                "${note.nYear}/${note.nMonth}/${note.nDay}" + if (note.nHours > 0) "${note.nHours}"
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
