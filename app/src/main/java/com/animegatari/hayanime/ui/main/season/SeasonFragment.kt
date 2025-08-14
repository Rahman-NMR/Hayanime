package com.animegatari.hayanime.ui.main.season

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSeasonBinding
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.dummy.Dummy
import com.animegatari.hayanime.ui.utils.dummy.DummyAdapter

class SeasonFragment : Fragment() {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    private lateinit var seasonViewModel: SeasonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        seasonViewModel = ViewModelProvider(this)[SeasonViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)
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

        seasonViewModel.text.observe(viewLifecycleOwner) {
//            binding.textSeason.text = it
        }

        binding.setupRecyclerView()
    }

    private fun FragmentSeasonBinding.setupRecyclerView() {
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
}