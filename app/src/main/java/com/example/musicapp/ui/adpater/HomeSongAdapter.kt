package com.example.musicapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.model.Audio
import com.example.musicapp.databinding.ItemSongBinding

class HomeSongAdapter(
    private val onItemClick: (Audio) -> Unit
) : ListAdapter<Audio, HomeSongAdapter.AudioViewHolder>(
    DiffCallback
) {

    inner class AudioViewHolder(
        private val binding: ItemSongBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            audio: Audio
        ) {

            binding.tvSongName.text = audio.title

            binding.tvSongArtist.text = audio.artist

            Glide.with(
                binding.ivSong
            ).load(
                    audio.artworkUri
                ).placeholder(
                    R.drawable.img_fragment
                ).error(
                    R.drawable.img_fragment
                ).into(
                    binding.ivSong
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

        val binding = ItemSongBinding.inflate(
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