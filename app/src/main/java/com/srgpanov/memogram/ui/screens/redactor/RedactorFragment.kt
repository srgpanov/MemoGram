package com.srgpanov.memogram.ui.screens.redactor

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.memogram.R
import com.srgpanov.memogram.data.Mem
import com.srgpanov.memogram.databinding.FragmentRedactorBinding
import com.srgpanov.memogram.ui.views.RedactorMemView


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
            val selectedImage = it.data?.data
            if (selectedImage != null) {
                viewModel.takePicture(selectedImage,binding.memView.width/2,binding.memView.width/2)
            }else {
                Log.e(TAG, "setupListeners: selectedImage null" )
            }

        }
        binding.btnAddSticker.setOnClickListener {
            val takePictureIntent = Intent(Intent.ACTION_PICK)
            takePictureIntent.type = "image/*"
            takePicture.launch(takePictureIntent)
        }
        binding.btnAddText.setOnClickListener {
            binding.memView.addText()
            binding.memView.onTextContainerSelectedListener= object : RedactorMemView.OnTextContainerSelectedListener {
                override fun onContainerSelected(id: Long, text: String) {
                    Log.d("RedactorFragment", "setupListeners: id $id text $text")
                    binding.etSignature.setText(text)
                    binding.etSignature.setSelection(binding.etSignature.text.length)
                    showTextRedactorPanel()
                }

                override fun onNothingSelected() {
                    hideTextRedactorPanel()
                }

            }

        }
        binding.etSignature.doAfterTextChanged {editable: Editable? ->
            binding.memView.changeText(editable.toString())
        }
    }

    private fun showTextRedactorPanel() {
        binding.etSignature.visibility =View.VISIBLE
    }

    private fun hideTextRedactorPanel() {
        binding.etSignature.visibility =View.INVISIBLE
    }
}



