package com.srgpanov.memogram.ui.screens.redactor

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.srgpanov.memogram.App
import com.srgpanov.memogram.data.Mem
import com.srgpanov.memogram.other.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RedactorViewModel(mem: Mem) : ViewModel() {
    val takeBitmap =SingleLiveEvent<Bitmap>()

    companion object{
        const val TAG = "RedactorViewModel"
    }
    fun takePicture(uri: Uri,reqWidth:Int,reqHeight: Int) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                Glide.with(App.instance).asBitmap().load(uri)
                    .submit(reqWidth, reqHeight).get()
            }
            if (bitmap != null) {
                Log.d(RedactorFragment.TAG, "setupListeners: $bitmap width ${bitmap.width}")
                takeBitmap.value=bitmap
            } else {
                Log.e(TAG, "takePicture: bitmap null" )
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory(val mem: Mem) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RedactorViewModel::class.java)) {
                return (RedactorViewModel(mem) as T)
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }
}