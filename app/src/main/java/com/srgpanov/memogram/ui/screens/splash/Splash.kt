package com.srgpanov.memogram.ui.screens.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope

import com.srgpanov.memogram.R
import com.srgpanov.memogram.ui.MainActivity
import kotlinx.coroutines.delay

/**
 * A simple [Fragment] subclass.
 */
class Splash : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launchWhenResumed {
            (requireActivity() as MainActivity).navController.navigate(R.id.tabFragment)
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}
