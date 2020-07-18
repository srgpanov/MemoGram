package com.srgpanov.memogram.ui.screens.meme_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.memogram.data.Mem
import com.srgpanov.memogram.databinding.ItemMemesBinding


class MemesAdapter : RecyclerView.Adapter<MemesAdapter.MemesViewHolder>() {
    private val memes: List<Mem> = mutableListOf(Mem("","",false))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MemesViewHolder(ItemMemesBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MemesViewHolder, position: Int) {
        holder.bind(memes[0])
    }

    override fun getItemCount(): Int = 25

    inner class MemesViewHolder(private val binding: ItemMemesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mem: Mem) {
            binding.tvName.text = bindingAdapterPosition.toString()
        }
    }
}