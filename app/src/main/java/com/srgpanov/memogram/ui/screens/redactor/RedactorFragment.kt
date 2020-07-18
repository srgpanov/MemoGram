package com.srgpanov.memogram.ui.screens.redactor

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.memogram.R
import com.srgpanov.memogram.data.Mem
import com.srgpanov.memogram.databinding.FragmentRedactorBinding


class RedactorFragment : Fragment() {
    private var _binding: FragmentRedactorBinding? = null
    private val binding: FragmentRedactorBinding
        get() = _binding!!
    private lateinit var takePicture: ActivityResultLauncher<Intent>

    private val images = mutableListOf<Bitmap>()

    companion object {
        const val TAG = "RedactorFragment"
    }

    private lateinit var viewModel: RedactorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mem = Mem(name = "", image = "", favorite = false)
        val factory = RedactorViewModel.Factory(mem)
        viewModel = ViewModelProvider(this, factory)[RedactorViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRedactorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.takeBitmap.observe(viewLifecycleOwner, Observer{bitmap->
            if (bitmap != null) {
                binding.memView.addImage(bitmap)
            }
        })
    }

    private fun setupListeners() {
        takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImage = it.data?.getData()
            if (selectedImage != null) {
                viewModel.takePicture(selectedImage,binding.memView.width/2,binding.memView.width/2)
            }else {
                Log.e(TAG, "setupListeners: selectedImage null" )
            }

        }
        binding.btnAddSticker.setOnClickListener {
            val takePictureIntent: Intent = Intent(Intent.ACTION_PICK)
            takePictureIntent.type = "image/*"
            takePicture.launch(takePictureIntent)
        }
    }



    private fun getPath(uri: Uri): String? {
        val cursor: Cursor =
            context?.getContentResolver()?.query(uri, null, null, null, null) ?: return null
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }


}



