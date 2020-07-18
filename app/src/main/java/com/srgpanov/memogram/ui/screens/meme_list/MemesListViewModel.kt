package com.srgpanov.memogram.ui.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.memogram.data.Mem
import java.lang.reflect.Constructor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class MemesListViewModel(val memes: MutableList<Mem>) : ViewModel() {


    @Suppress("UNCHECKED_CAST")
    class Factory(private val memes: MutableList<Mem>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MemesListViewModel::class.java)) {
                return (MemesListViewModel(memes) as T)
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }


}