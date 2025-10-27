package com.animegatari.hayanime.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.databinding.FragmentAnimeDetailBinding
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onLoading
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeDetailFragment : Fragment() {
    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    private val animeDetailViewModel: AnimeDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeToolbar()
        observeUIState()
    }

    private fun initializeToolbar() = with(binding.toolBar) {
        setNavigationOnClickListener { dismiss() }
        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_share -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, animeDetailViewModel.getAnimeUrl())
                    }
                    startActivity(Intent.createChooser(shareIntent, null))
                    true
                }

                R.id.menu_item_open_browser -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = animeDetailViewModel.getAnimeUrl().toUri()
                    }
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun updateUI(anime: AnimeDetail?) = with(binding) {
        toolBar.title = anime?.title
    }

    private fun observeUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                animeDetailViewModel.animeDetail.collect { response ->
                    response.onSuccess { anime ->
                        loadingVisibilityView(indicator = false, layout = true)
                        updateUI(anime)
                    }.onError {
                        loadingVisibilityView(indicator = false, layout = false)
                        showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
                        dismiss()
                    }.onLoading { loadingVisibilityView(indicator = true, layout = false) }
                }
            }
        }
    }

    private fun loadingVisibilityView(indicator: Boolean, layout: Boolean) {
        binding.progressBar.isVisible = indicator
        binding.appBar.isVisible = layout
    }

    private fun dismiss() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}