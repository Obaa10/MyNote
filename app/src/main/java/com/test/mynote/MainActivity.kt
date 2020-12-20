package com.test.mynote

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.test.mynote.adapter.SectionsPagerAdapter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Attach the SectionsPagerAdapter to the ViewPager
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = pagerAdapter
        //Start activity with notes fragment
        pager.currentItem= 1
        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(pager)
        for (i in 0..2  ) {
            when(i){
                2-> tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_baseline_access_alarm_24)
                0->tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_archive_24)
                1->tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_event_note_24)
            }

        }
    }


}