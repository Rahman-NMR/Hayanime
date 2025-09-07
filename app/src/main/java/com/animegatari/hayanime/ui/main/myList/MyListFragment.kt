package com.animegatari.hayanime.ui.main.myList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentMyListBinding
import com.animegatari.hayanime.ui.main.myList.viewPager.ViewPagerAdapter
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import com.google.android.material.tabs.TabLayoutMediator

class MyListFragment : Fragment() {
    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager()
        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            toastShort(requireContext(), "TODO go to profile")

            true
        }

        else -> false
    }

    private fun FragmentMyListBinding.viewPager() {
        viewPager.adapter = ViewPagerAdapter(this@MyListFragment)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.watching_status_all_anime)
                1 -> getString(R.string.watching_status_watching)
                2 -> getString(R.string.watching_status_completed)
                3 -> getString(R.string.watching_status_plan_to_watch)
                4 -> getString(R.string.watching_status_on_hold)
                5 -> getString(R.string.watching_status_dropped)
                else -> getString(R.string.label_unknown)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}