package com.test.mynote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.R
import com.test.mynote.adapter.ArchiveNoteAdapter
import com.test.mynote.adapter.NoteListAdapter
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory

class ArchivedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_archived, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.archived_list)
        val noteViewModel =
            NoteViewModelFactory(activity!!.application).create(NoteViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val archiveNoteAdapter = ArchiveNoteAdapter(noteViewModel)
        recyclerView.adapter = archiveNoteAdapter
        noteViewModel.getAllArchivedNotes().observe(this){
            archiveNoteAdapter.submitList(it)
        }
        return view
    }
}