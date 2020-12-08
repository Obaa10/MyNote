package com.test.mynote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.test.mynote.ui.NotesFragment


class SectionsPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(
    manager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
//The number of pages in the ViewPager
        return 3
    }

    override fun getItem(position: Int): Fragment {
//The fragment to be displayed on each page
            return NotesFragment()
            /* 1 -> return PizzaFragment()
             2 -> return PastaFragment()
             3 -> return StoresFragment()*/
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0-> "Home screen "
            else->null
        }

    }
}