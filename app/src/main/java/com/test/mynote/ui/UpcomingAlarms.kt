package com.test.mynote.ui

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.NotifyByDate
import com.test.mynote.R
import com.test.mynote.adapter.UpcomingAlarmsAdapter
import com.test.mynote.database.Note
import com.test.mynote.viewmodel.NoteViewModel
import com.test.mynote.viewmodel.NoteViewModelFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class UpcomingAlarms : Fragment() {
    var mDate = MutableLiveData<ArrayList<Int>>(arrayListOf(0, 0, 0, 0))
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
        noteViewModel.getAllAlarms().observe(this) {
            archiveNoteAdapter.submitList(it)
            list= it as ArrayList<Note>
        }


        noteViewModel.editAlarm.observe(this) {
            if (it[0] >= 0) {
                noteViewModel.removeAlarm.value = true
                val c = Calendar.getInstance()
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)
                val datetime = DatePickerDialog(
                    activity!!,
                    DatePickerDialog.OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                        year = years
                        month = monthOfYear
                        day = dayOfMonth
                        addAlarm(it[1],years,it[3],month,it[5],day,noteViewModel,it[0])
                    }, year, month, day
                )
                datetime.show()
            }
        }

        return view
    }

  /*  private fun getTime(date: ArrayList<Int>) {
        val listItems = arrayOf("Two days", "One day", "One hour", "Custom time")
        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle("Notify me..")
        val mDates = arrayListOf<Int>(0, 0, 0, 0)
        mBuilder.setSingleChoiceItems(listItems, -1,
            DialogInterface.OnClickListener { dialogInterface, i ->
                when (i) {
                    0 -> {
                        mDates[2] = date[2] - 2
                        mDates[0] = date[0]
                        mDates[1] = date[1]
                    }
                    1 -> {
                        mDates[2] = date[2] - 1
                        mDates[0] = date[0]
                        mDates[1] = date[1]
                    }
                    2 -> {
                        mDates[3] = 1
                        mDates[2] = date[2]
                        mDates[0] = date[0]
                        mDates[1] = date[1]
                    }
                    3 -> {
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)
                        val datetime = DatePickerDialog(
                            activity!!,
                            DatePickerDialog.OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                                mDates[0] = years
                                mDates[1] = monthOfYear+1
                                mDates[2] = dayOfMonth
                                mDate.value = mDates
                            }, year, month, day
                        )
                        datetime.show()
                    }
                }
                mDate.value = mDates
                dialogInterface.dismiss()
            })
        val mDialog = mBuilder.create()
        mDialog.show()
    }
*/

    private fun addAlarm(year: Int, nYear: Int, month: Int, nMonth: Int, day: Int, nDay: Int
                         , noteViewModel:NoteViewModel, id:Int) {
        val alarmMgr =
            activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(activity, NotifyByDate::class.java)
        intent.putExtra("date", arrayListOf(year,month,day))
        intent.putExtra("name", list.find { note -> note.id==id }?.title)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0)
        val time = Calendar.getInstance()
        if(Date(year,month,day) < (Date(nYear,nMonth,nDay))){
            Toast.makeText(activity,"Error",Toast.LENGTH_LONG).show()
        }
        else {
            noteViewModel.updateAlarm(id,nYear,nMonth,nDay)
            val alramsDay =
                (year-nYear)*360+((month-nMonth)*30).absoluteValue + day - nDay
            println("*****************" + alramsDay)
            println("*****************" +"$year $month $day $nYear $nMonth $nDay")
            time.timeInMillis = System.currentTimeMillis()
            time.add(Calendar.SECOND, (alramsDay * 24 * 60 * 60))
            alarmMgr[AlarmManager.RTC_WAKEUP, time.timeInMillis] = pendingIntent
        }
        }
}