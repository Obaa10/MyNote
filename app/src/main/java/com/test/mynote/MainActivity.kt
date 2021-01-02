package com.test.mynote

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.test.mynote.adapter.SectionsPagerAdapter
import com.test.mynote.ui.ArchivedFragment
import com.test.mynote.ui.NotesFragment
import com.test.mynote.ui.UpcomingAlarms
import eu.long1.spacetablayout.SpaceTabLayout
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Attach the SectionsPagerAdapter to the ViewPager
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = pagerAdapter
        pager.currentItem= 1
        val fragmentList : MutableList<Fragment> = ArrayList()
        fragmentList.add(ArchivedFragment())
        fragmentList.add(NotesFragment())
        fragmentList.add(UpcomingAlarms())

        val tabLayout = findViewById<View>(R.id.tabs) as SpaceTabLayout
        tabLayout.initialize(pager,supportFragmentManager,fragmentList,null)

    }


}