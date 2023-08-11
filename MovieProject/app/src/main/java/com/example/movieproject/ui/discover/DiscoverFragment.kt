package com.example.movieproject.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movieproject.R
import com.example.movieproject.databinding.FragmentDiscoverBinding
import com.example.movieproject.ui.favorites.DiscoveredFragment
import com.example.movieproject.ui.moviedetail.DetailMovieFragment
import com.example.movieproject.utils.BundleKeys
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val viewModel: DiscoverViewModel by viewModels(ownerProducer = { this })
    private var chipGroup : MutableList<String> = mutableListOf()
    private lateinit var dialogView: View
    private lateinit var chipGroupDialog: ChipGroup
    private var selectedChips: MutableList<String> = mutableListOf()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.genre_dialog_layout, null)

        chipGroupDialog = dialogView.findViewById(R.id.chipGroupDialog)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
    }

    private fun listenViewModel() {

        viewModel.apply {
            genreOptionList.observe(viewLifecycleOwner) {
                chipGroup = it

            }
            binding.chipButton.setOnClickListener { showChipSelectionDialog() }
            binding.discoverButton.setOnClickListener {
                onDiscoverButtonClicked()
            }

        }



    }

    private fun onDiscoverButtonClicked() {
        var ids: MutableList<Int> = mutableListOf()
        viewModel.liveDataGenreList.observe(viewLifecycleOwner){
            ids = it.filterValues { it in selectedChips }.keys.toMutableList()
        }
        val bundle = Bundle().apply {
            putIntegerArrayList(BundleKeys.REQUEST_DISCOVER, ArrayList(ids))
        }

        val destinationFragment = DiscoveredFragment()
        destinationFragment.arguments = bundle
        findNavController().navigate(R.id.discoveredFragment, bundle) // R.id.action_detail
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createChip(text: String, isCheckable: Boolean): Chip {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCheckable = isCheckable
        return chip
    }

    private fun showChipSelectionDialog() {

        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.genre_dialog_layout, null)
        chipGroupDialog = dialogView.findViewById(R.id.chipGroupDialog)
        val chipButton: Chip = binding.chipButton
        chipGroupDialog.removeAllViews() // Clear previous chips
        val chipGroupView: ChipGroup = binding.chipGroup
        for (title in chipGroup) {
            val chip = createChip(title, true)
            if(title in selectedChips) chip.isChecked = true
            if (chip !== chipButton) {
                chipGroupDialog.addView(chip)
            }
        }

        // Build and show the dialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Options")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                selectedChips = mutableListOf()
                for (index in 0 until chipGroupDialog.childCount) {
                    val chip = chipGroupDialog.getChildAt(index) as Chip
                    if (chip.isChecked) {
                        selectedChips.add(chip.text.toString())
                    }
                }

                // Update the selected chips in your UI
                updateSelectedChipsUI()
            }
            .setNegativeButton("Cancel", null)
            .create() // Create the dialog instance

        alertDialog.show() // Show the dialog
    }

    private fun updateSelectedChipsUI() {
        // Add selected chips to your UI, e.g., ChipGroup
        val chipGroupView: ChipGroup = binding.chipGroup
        val chipButton: Chip = binding.chipButton
        chipGroupView.removeAllViews()
        chipGroupView.addView(chipButton)
        for (chipText in selectedChips) {
            val chip = Chip(requireContext())
            chip.text = chipText
            chipGroupView.addView(chip)
        }
    }
}