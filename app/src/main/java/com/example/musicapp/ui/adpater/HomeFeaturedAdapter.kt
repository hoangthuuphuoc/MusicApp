package com.example.musicapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.model.Audio
import com.example.musicapp.databinding.ItemAlbumBinding

class HomeFeaturedAdapter(
    private val onItemClick: (Audio) -> Unit
) : ListAdapter<Audio, HomeFeaturedAdapter.AudioViewHolder>(
    DiffCallback
) {

    inner class AudioViewHolder(
        private val binding: ItemAlbumBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            audio: Audio
        ) {

            binding.tvAlbumName.text = audio.title

            binding.tvArtist.text = audio.artist

            Glide.with(
                binding.ivAlbum
            ).load(
                    audio.artworkUri
                ).placeholder(
                    R.drawable.img_fragment
                ).error(
                    R.drawable.img_fragment
                ).into(
                    binding.ivAlbum
                )

            binding.root.setOnClickListener {

                    onItemClick(
                        audio
                    )
                }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AudioViewHolder {

        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )

        return AudioViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: AudioViewHolder, position: Int
    ) {

        holder.bind(
            getItem(
                position
            )
        )
    }

    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<Audio>() {

            override fun areItemsTheSame(
                oldItem: Audio, newItem: Audio
            ): Boolean {

                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Audio, newItem: Audio
            ): Boolean {

                return oldItem == newItem
            }
        }
    }
}