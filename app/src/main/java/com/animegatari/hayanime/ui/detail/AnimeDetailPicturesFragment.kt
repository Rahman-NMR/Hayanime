package com.animegatari.hayanime.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.data.model.Picture
import com.animegatari.hayanime.databinding.FragmentAnimeDetailPicturesBinding
import com.animegatari.hayanime.databinding.LayoutAnimePictureBinding
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onLoading
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.ui.adapter.generic.CleanAdapter
import com.animegatari.hayanime.ui.adapter.generic.GenericDiffUtil
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeDetailPicturesFragment : Fragment() {
    private var _binding: FragmentAnimeDetailPicturesBinding? = null
    private val binding get() = _binding!!

    private val animeDetailViewModel: AnimeDetailViewModel by viewModels()
    private val animePictures by lazy { animePicsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimeDetailPicturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupInteractionListeners()
        observeViewModelStates()
    }

    private fun setupInteractionListeners() = with(binding) {
        toolBar.setNavigationOnClickListener { dismiss() }
    }

    private fun setupRecyclerView() = with(binding) {
        picturesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                calculateSpanCount(requireContext(), 200),
                StaggeredGridLayoutManager.VERTICAL
            )
            adapter = animePictures
        }
    }

    private fun animePicsAdapter(): CleanAdapter<Picture, LayoutAnimePictureBinding> {
        val picturesDiffUtil = GenericDiffUtil<Picture>(
            onAreItemsTheSame = { old, new -> old == new },
            onAreContentsTheSame = { old, new -> old == new }
        )
        return CleanAdapter(
            inflater = { layoutInflater, parent, attach ->
                LayoutAnimePictureBinding.inflate(layoutInflater, parent, attach)
            }, binder = { vBinding, item ->
                Glide.with(requireContext())
                    .load(item.large ?: item.medium)
                    .placeholder(R.drawable.img_placeholder)
                    .fallback(R.drawable.img_fallback)
                    .error(R.drawable.img_error)
                    .into(vBinding.image)
            }, diffCallback = picturesDiffUtil
        )
    }

    private fun observeUIState(anime: AnimeDetail?) = with(binding) {
        val hasPictures = anime?.pictures.isNullOrEmpty().not()
        picturesRecyclerView.isVisible = hasPictures
        animePictures.submitList(anime?.pictures)

        tvInfoMsg.isVisible = hasPictures.not()
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                animeDetailViewModel.animeDetail.collect { response ->
                    response.onSuccess { anime ->
                        loadingVisibilityView(indicator = false, layout = true)
                        observeUIState(anime)
                    }.onError {
                        loadingVisibilityView(indicator = false, layout = false)
                        showToast(requireContext(), getString(R.string.message_failed_load_data))
                        dismiss()
                    }.onLoading { loadingVisibilityView(indicator = true, layout = false) }
                }
            }
        }
    }

    private fun loadingVisibilityView(indicator: Boolean, layout: Boolean) {
        binding.progressBar.isVisible = indicator
        binding.appBar.isVisible = layout
        binding.picturesRecyclerView.isVisible = layout
    }

    private fun dismiss() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}