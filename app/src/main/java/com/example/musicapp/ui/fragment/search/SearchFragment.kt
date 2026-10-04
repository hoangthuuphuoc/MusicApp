package com.example.musicapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.FragmentSearchBinding
import com.example.musicapp.service.MusicPlaybackAction
import com.example.musicapp.ui.detail.DetailActivity
import com.example.musicapp.ui.home.adapter.HomeSongAdapter
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var songAdapter: HomeSongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view, savedInstanceState
        )

        setupRecyclerView()

        setupSearch()

        observeState()

        observeEffect()

        viewModel.onEvent(
            SearchUiEvent.LoadAudio
        )
    }

    private fun setupRecyclerView() {

        songAdapter = HomeSongAdapter { audio ->

            viewModel.onEvent(
                SearchUiEvent.ClickAudio(
                    audio
                )
            )
        }

        binding.rvSearch.layoutManager = LinearLayoutManager(
            requireContext()
        )

        binding.rvSearch.adapter = songAdapter
    }

    private fun setupSearch() {

        binding.edtSearch.addTextChangedListener { editable ->

                viewModel.onEvent(
                    SearchUiEvent.Search(
                        editable.toString()
                    )
                )
            }

        binding.tvCancel.setOnClickListener {

                binding.edtSearch.text.clear()

                viewModel.onEvent(
                    SearchUiEvent.ClearSearch
                )
            }
    }

    private fun observeState() {

        viewLifecycleOwner.lifecycleScope.launch {

                viewLifecycleOwner.repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        viewModel.uiState.collect { state ->

                                songAdapter.submitList(
                                        state.resultAudios
                                    )
                            }
                    }
            }
    }

    private fun observeEffect() {

        viewLifecycleOwner.lifecycleScope.launch {

                viewLifecycleOwner.repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        viewModel.uiEffect.collect { effect ->

                                when (effect) {

                                    is SearchUiEffect.OpenDetail -> {

                                        val intent = Intent(
                                            requireContext(), DetailActivity::class.java
                                        ).apply {

                                            putExtra(
                                                MusicPlaybackAction.EXTRA_AUDIO_ID, effect.audioId
                                            )
                                        }

                                        startActivity(
                                            intent
                                        )
                                    }
                                }
                            }
                    }
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}
