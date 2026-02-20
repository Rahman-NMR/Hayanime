package com.animegatari.hayanime.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.Picture
import com.animegatari.hayanime.databinding.FragmentAnimePicturesBinding
import com.animegatari.hayanime.databinding.LayoutAnimePictureBinding
import com.animegatari.hayanime.ui.adapter.generic.CleanAdapter
import com.animegatari.hayanime.ui.adapter.generic.GenericDiffUtil
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimePicturesFragment : Fragment() {
    private var _binding: FragmentAnimePicturesBinding? = null
    private val binding get() = _binding!!

    private val animePicturesViewModel: AnimePicturesViewModel by navGraphViewModels(R.id.anime_detail_graph)
    private val picturesAdapter by lazy { animePicsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimePicturesBinding.inflate(inflater, container, false)
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
            adapter = picturesAdapter
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

    private fun observeUIState(pictures: List<Picture?>) = with(binding) {
        val hasPictures = pictures.isEmpty().not()
        picturesRecyclerView.isVisible = hasPictures
        picturesAdapter.submitList(pictures)

        tvInfoMsg.isVisible = hasPictures.not()
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                animePicturesViewModel.pictures.collect { pictures ->
                    observeUIState(pictures)
                }
            }
        }
    }

    private fun dismiss() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}