package com.animegatari.hayanime.ui.main.myList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentMyListBinding
import com.animegatari.hayanime.ui.main.myList.viewPager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MyListFragment : Fragment() {
    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var myListViewModel: MyListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myListViewModel = ViewModelProvider(this)[MyListViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myListViewModel.text.observe(viewLifecycleOwner) {
//            binding.textSeason.text = it
        }

        binding.viewPager()
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