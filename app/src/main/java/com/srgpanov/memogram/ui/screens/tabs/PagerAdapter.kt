package com.srgpanov.memogram.ui.screens.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.srgpanov.memogram.ui.screens.meme_list.MemesAdapter
import com.srgpanov.memogram.ui.screens.meme_list.MemesListFragment

class PagerAdapter(activity:FragmentActivity): FragmentStateAdapter(activity) {
    private val memesCategory=5

    override fun getItemCount(): Int {
        return memesCategory
    }

    override fun createFragment(position: Int): Fragment {
        return MemesListFragment.newInstance()
    }
}