package com.animegatari.hayanime.ui.main.season

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSeasonBinding
import com.animegatari.hayanime.ui.auth.AuthViewModel
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.dummy.Dummy
import com.animegatari.hayanime.ui.utils.dummy.DummyAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeasonFragment : Fragment() {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    private lateinit var seasonViewModel: SeasonViewModel
    private val authViewModel: AuthViewModel by viewModels()

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
        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_avatar -> {
                    authViewModel.logout()
                    Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()

                    true
                }

                else -> false
            }
        }
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