package com.test.mynote.adapter

import android.app.Activity
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.mynote.NoteDetails
import com.test.mynote.R
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel

class NoteListAdapter(val noteViewModel: NoteViewModel) : ListAdapter<Note, NoteListAdapter.ViewHolder>(NOTES_COMPARATOR) {


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

        holder.button.setOnClickListener {view->
            val builder: AlertDialog.Builder? = this.let {
                val builder = AlertDialog.Builder(view.context)
                builder.apply {
                    setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            noteViewModel.delete(note.id)
                        })
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                }
            }
            builder?.setMessage(R.string.dialog_message)
                ?.setTitle(R.string.dialog_title)
            val dialog: AlertDialog? = builder?.create()
            dialog!!.show()

        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val date: TextView = view.findViewById(R.id.date)
        val button : Button = view.findViewById(R.id.button)
        fun create(note: Note) {
            title.text = note.title
            if(note.year!=0)
            date.text = note.year.toString() + "/"+ note.month.toString() + "/"+
                    note.day.toString()
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

