package com.animegatari.hayanime.ui.main.myList.viewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentMyAnimeListBinding
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.dummy.DummyAdapterList

class MyAnimeListFragment : Fragment() {
    private var _binding: FragmentMyAnimeListBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: MyAnimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION) ?: 0
        val str = "Fragment : ${
            when (position) {
                0 -> getString(R.string.watching_status_all_anime)
                1 -> getString(R.string.watching_status_watching)
                2 -> getString(R.string.watching_status_completed)
                3 -> getString(R.string.watching_status_plan_to_watch)
                4 -> getString(R.string.watching_status_on_hold)
                5 -> getString(R.string.watching_status_dropped)
                else -> getString(R.string.label_unknown)
            }
        }"

        binding.setupRecyclerView()

        binding.loadingIndicator.hide()
        binding.fabScrollToTop.setOnClickListener {
            Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
        }
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            binding.loadingIndicator.apply {
                show()
                postDelayed({ hide() }, 2000)
            }
        }
    }

    private fun FragmentMyAnimeListBinding.setupRecyclerView() {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = DummyAdapterList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POSITION = "position"
        const val NUM_TABS = 6

        fun newInstance(position: Int): MyAnimeListFragment {
            val fragment = MyAnimeListFragment()
            fragment.arguments = Bundle().apply { putInt(ARG_POSITION, position) }
            return fragment
        }
    }
}
