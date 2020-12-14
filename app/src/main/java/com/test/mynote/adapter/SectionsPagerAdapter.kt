package com.test.mynote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.test.mynote.ui.ArchivedFragment
import com.test.mynote.ui.NotesFragment
import com.test.mynote.ui.UpcomingAlarms


class SectionsPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(
    manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
//The fragment to be displayed on each page
        return when (position) {
            1 -> NotesFragment()
            0 -> ArchivedFragment()
            2 -> UpcomingAlarms()
            else -> NotesFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            1 -> "My Notes"
            0 -> "Archived notes"
            2 -> "Upcoming alarms"
            else -> null
        }

    }
}