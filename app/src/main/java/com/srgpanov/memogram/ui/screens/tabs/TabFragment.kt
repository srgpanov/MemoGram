package com.srgpanov.memogram.ui.screens.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.srgpanov.memogram.databinding.FragmentTabBinding
import com.srgpanov.memogram.ui.MainActivity

class TabFragment : Fragment() {
    private lateinit var viewModel: TabViewModel
    private var _binding:FragmentTabBinding?=null
    private  val  binding:FragmentTabBinding
        get() = _binding!!
    private var mainActivity:MainActivity?=null

    companion object {
        fun newInstance() = TabFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TabViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        binding.pager.adapter=PagerAdapter(requireActivity())
        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Все"
                1 -> tab.text = "Новые"
                2 -> tab.text = "Популярное"
                3 -> tab.text = "Любимое"
                4 -> tab.text = "Случайный"
            }
        }
        tabLayoutMediator.attach()
    }

    private fun setupToolbar() {
    }


}
