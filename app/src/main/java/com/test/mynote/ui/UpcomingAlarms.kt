package com.test.mynote.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.R
import com.test.mynote.adapter.UpcomingAlarmsAdapter
import com.test.mynote.database.Note
import com.test.mynote.notifyactivity.NotifyByDate
import com.test.mynote.swipehelper.SwipeHelper
import com.test.mynote.swipehelper.SwipeHelper.UnderlayButtonClickListener
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.util.*
import kotlin.math.absoluteValue


class UpcomingAlarms : Fragment() {
    var list = arrayListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming_alarms, container, false)

        //Define recycler view
        val recyclerView: RecyclerView = view.findViewById(R.id.upcoming_list)
        val noteViewModel =
            NoteViewModelFactory(activity!!.application).create(NoteViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val archiveNoteAdapter = UpcomingAlarmsAdapter(noteViewModel)
        recyclerView.adapter = archiveNoteAdapter
        val backGround: ImageView = view.findViewById(R.id.upcoming_background)

        noteViewModel.getAllAlarms().observe(this) {
            archiveNoteAdapter.submitList(it)
            list = it as ArrayList<Note>
            if (it.isEmpty()) backGround.visibility = View.VISIBLE
            else backGround.visibility = View.INVISIBLE
        }

        object : SwipeHelper(activity, recyclerView) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    "Edit",
                    0,
                    Color.parseColor("#FF3C30"),
                    UnderlayButtonClickListener {
                        val note = list[it]
                        noteViewModel.editAlarm.value = arrayListOf(
                            note.id,
                            note.year,
                            note.nYear,
                            note.month,
                            note.nMonth,
                            note.day,
                            note.nDay
                        )
                    }
                ))
                underlayButtons.add(UnderlayButton(
                    "Delete",
                    0,
                    Color.parseColor("#FF9502"),
                    UnderlayButtonClickListener {
                        val show: Any = AlertDialog.Builder(activity!!)
                            .setTitle("Delete Alarm")
                            .setMessage("Are you sour you want to delete this alarm ?")
                            .setPositiveButton(
                                "YES"
                            ) { dialog, _ -> // The user wants to leave - so dismiss the dialog and exit
                                noteViewModel.removeAlarm.value = true
                                list[it].hasAlarm = false
                                noteViewModel.update(list[it])
                                dialog.dismiss()
                            }.setNegativeButton(
                                "NO"
                            ) { dialog, _ -> // The user is not sure, so you can exit or just stay
                                archiveNoteAdapter.notifyItemChanged(it)
                                dialog.dismiss()
                            }.show()
                    }
                ))
            }
        }

        noteViewModel.editAlarm.observe(this) {
            if (it[0] >= 0) {
                noteViewModel.removeAlarm.value = true
                pickDateTime(it[1],it[3],it[5],noteViewModel,it[0])
            }
        }
        return view
    }

    private fun pickDateTime(
        nYear: Int, nMonth: Int, nDay: Int,
        noteViewModel: NoteViewModel, id: Int
    ) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(activity!!, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                when {
                    Date(year, month, day, hour, minute)
                            < Date(startYear, startMonth,startDay,startHour,startMinute) -> {
                        alarmIllogical(true)
                    }
                    Date(nYear, nMonth, nDay) < Date(year, month, day) -> {
                        alarmIllogical(false)
                    }
                    else -> {
                        addAlarm(
                            year,
                            startYear,
                            month,
                            startMonth,
                            day,
                            startDay,
                            noteViewModel,
                            id,
                            hour,
                            startHour,
                            minute,
                            startMinute
                        )
                    }
                }
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun alarmIllogical(before: Boolean) {
        val error = if (before) "Alarm is in the past"
        else "Alarm is after the note's date"
        val show: Any = AlertDialog.Builder(activity!!)
            .setTitle("Alarm time is illogical")
            .setMessage(error)
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun addAlarm(
        year: Int, nYear: Int, month: Int, nMonth: Int, day: Int, nDay: Int
        , noteViewModel: NoteViewModel, id: Int, hours: Int,nHour:Int,
        minute: Int,nMinute:Int
    ) {
        val alarmMgr =
            activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(activity, NotifyByDate::class.java)
        intent.putExtra("date", arrayListOf(year, month, day))
        intent.putExtra("name", list.find { note -> note.id == id }?.title)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0)
        val time = Calendar.getInstance()
        noteViewModel.updateAlarm(id, year, month+1, day)
        var alarmsDay =
            ((year - nYear) * 360)+ ((month - nMonth) * 30) + (day - nDay)
        val upcomingAlarmsDay = alarmsDay
        alarmsDay= alarmsDay * 24 * 60 * 60 +((((hours*60)+minute)*60)-(((nHour*60)+nMinute)*60))
        Toast.makeText(activity!!, "$upcomingAlarmsDay day left to your alarm", Toast.LENGTH_LONG).show()
        time.timeInMillis = System.currentTimeMillis()
        time.add(Calendar.SECOND,  alarmsDay)
        alarmMgr[AlarmManager.RTC_WAKEUP, time.timeInMillis] = pendingIntent
    }
}