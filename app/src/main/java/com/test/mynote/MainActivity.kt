package com.test.mynote

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.test.mynote.adapter.SectionsPagerAdapter
import com.test.mynote.viewmodel.NoteViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Attach the SectionsPagerAdapter to the ViewPager
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.setCurrentItem(1)
        pager.adapter = pagerAdapter
        //Attach the ViewPager to the TabLayout

        //Attach the ViewPager to the TabLayout
        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(pager)
    }


}