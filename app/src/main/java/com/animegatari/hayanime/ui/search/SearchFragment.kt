package com.animegatari.hayanime.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.ui.recyclerview.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.utils.dummy.Dummy
import com.animegatari.hayanime.utils.dummy.DummyAdapter

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadingIndicator.hide()
        binding.fabScrollToTop.hide()

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            binding.loadingIndicator.apply {
                show()
                postDelayed({ hide() }, 11000)
            }
        }

        searchViewModel.text.observe(viewLifecycleOwner) {
//            binding.textSearch.text = it
        }

        binding.setupRecyclerView()
    }

    private fun FragmentSearchBinding.setupRecyclerView() {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            Dummy.calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = DummyAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.hide()
    }
}