package com.srgpanov.memogram.ui.screens.meme_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.srgpanov.memogram.R
import com.srgpanov.memogram.databinding.FragmentMemesListBinding
import com.srgpanov.memogram.other.addSystemWindowInsetToPadding
import com.srgpanov.memogram.ui.MainActivity
import kotlinx.coroutines.delay


class MemesListFragment : Fragment() {
    private lateinit var viewModel: MemesListViewModel
    private var _binding:FragmentMemesListBinding?=null
    private  val  binding:FragmentMemesListBinding
        get() = _binding!!
    private var mainActivity: MainActivity?=null
    private val adapter:MemesAdapter by lazy { MemesAdapter() }
    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance() =
            MemesListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val factory =MemesListViewModel.Factory(mutableListOf())
        viewModel=ViewModelProvider(this,factory)[MemesListViewModel::class.java]
        mainActivity=requireActivity() as? MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentMemesListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.addSystemWindowInsetToPadding(bottom = true)
        binding.list.adapter=adapter
        lifecycleScope.launchWhenResumed {
            mainActivity?.navController?.navigate(R.id.redactorFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}