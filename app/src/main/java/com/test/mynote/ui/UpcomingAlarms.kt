package com.test.mynote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.R
import com.test.mynote.adapter.UpcomingAlarmsAdapter
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory

class UpcomingAlarms : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_upcoming_alarms, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.upcoming_list)
        val noteViewModel =
            NoteViewModelFactory(activity!!.application).create(NoteViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val archiveNoteAdapter = UpcomingAlarmsAdapter(noteViewModel)
        recyclerView.adapter = archiveNoteAdapter
        noteViewModel.getAllAlarms().observe(this){
            archiveNoteAdapter.submitList(it)
        }
        return view    }
}