package com.thuypham.ptithcm.learningrxjava.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thuypham.ptithcm.learningrxjava.R
import com.thuypham.ptithcm.learningrxjava.databinding.ItemMovieBinding
import com.thuypham.ptithcm.learningrxjava.model.Movie

class MovieAdapter(
    private val onItemSelected: ((item: Movie) -> Unit)? = null,
) : ListAdapter<Movie, RecyclerView.ViewHolder>(DiffCallback()) {

    class ImageViewHolderItem(
        private val binding: ItemMovieBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            binding.apply {
                Glide.with(root.context)
                    .load(item.imageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivMedia)

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageViewHolderItem {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolderItem(binding)
            .apply {

                binding.root.setOnClickListener {
                    val filePath = currentList[absoluteAdapterPosition]
                    onItemSelected?.invoke(filePath)
                }
            }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageViewHolderItem).bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem == newItem
    }
}