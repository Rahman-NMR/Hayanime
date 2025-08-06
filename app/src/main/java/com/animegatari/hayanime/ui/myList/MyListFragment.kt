package com.animegatari.hayanime.ui.myList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.animegatari.hayanime.databinding.FragmentMyListBinding

class MyListFragment : Fragment() {
    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val myListViewModel = ViewModelProvider(this).get(MyListViewModel::class.java)
        _binding = FragmentMyListBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val textView: TextView = binding.textMyList
        myListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}