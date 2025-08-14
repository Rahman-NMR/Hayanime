package com.animegatari.hayanime.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.animegatari.hayanime.ui.viewPager.MyAnimeListFragment
import com.animegatari.hayanime.ui.viewPager.MyAnimeListFragment.Companion.NUM_TABS

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return MyAnimeListFragment.newInstance(position)
    }

    override fun getItemCount(): Int = NUM_TABS
}