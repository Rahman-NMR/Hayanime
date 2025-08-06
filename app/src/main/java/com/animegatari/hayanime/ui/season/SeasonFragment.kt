package com.animegatari.hayanime.ui.season

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.animegatari.hayanime.databinding.FragmentSeasonBinding

class SeasonFragment : Fragment() {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val seasonViewModel = ViewModelProvider(this).get(SeasonViewModel::class.java)
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val textView: TextView = binding.textSeason
        seasonViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}