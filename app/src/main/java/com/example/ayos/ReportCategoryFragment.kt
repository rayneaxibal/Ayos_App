package com.example.ayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ayos.databinding.FragmentReportCategoryBinding
import com.google.android.material.chip.Chip

class ReportCategoryFragment : Fragment() {

    private var selectedChip: Chip? = null
    private var _binding: FragmentReportCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCategoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ReportViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allChips = mutableListOf<Chip>()
        val chipGroups = listOf(
            binding.chipGroupInfraGroup,
            binding.chipGroupEnvGroup,
            binding.chipGroupTrafficGroup
        )

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        chipGroups.forEach { group ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i)
                if (chip is Chip) {
                    allChips.add(chip)
                    chip.setOnClickListener { handleSingleSelection(chip, allChips) }
                }
            }
        }

        binding.searchView.isIconified = false
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim()?.lowercase().orEmpty()
                allChips.forEach { chip ->
                    chip.visibility =
                        if (chip.text.toString().lowercase().contains(query)) View.VISIBLE
                        else View.GONE
                }
                return true
            }
        })

        binding.btnCont.setOnClickListener {
            if (selectedChip == null) {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.category = selectedChip?.text.toString()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ReportDescFragment())
                .addToBackStack("ReportDesc")
                .commit()
        }
    }

    private fun handleSingleSelection(selected: Chip, allChips: List<Chip>) {
        selectedChip?.isChecked = false
        selected.isChecked = true
        selectedChip = selected
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}