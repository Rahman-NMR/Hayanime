package com.animegatari.hayanime.ui.main.myList.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return MyAnimeListFragment.newInstance(position)
    }

    override fun getItemCount(): Int = MyAnimeListFragment.Companion.NUM_TABS
}