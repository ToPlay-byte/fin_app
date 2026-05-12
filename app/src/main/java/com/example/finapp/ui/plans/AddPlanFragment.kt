package com.example.finapp.ui.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.finapp.data.entity.PlanEntity
import com.example.finapp.databinding.FragmentAddPlanBinding
import com.example.finapp.viewmodel.FinanceViewModel

class AddPlanFragment : Fragment() {

    private var _binding: FragmentAddPlanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeriodDropdown()
        setupCategoryDropdown()

        binding.btnSavePlan.setOnClickListener { savePlan() }
    }

    private fun setupPeriodDropdown() {
        val periods = listOf("Щомісяця", "Щотижня")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, periods)
        binding.autoPlanPeriod.setAdapter(adapter)
        binding.autoPlanPeriod.setText("Щомісяця", false)
    }

    private fun setupCategoryDropdown() {
        viewModel.getCategoriesByType("EXPENSE").observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.autoPlanCategory.setAdapter(adapter)
            if (names.isNotEmpty()) {
                binding.autoPlanCategory.setText(names[0], false)
            }
        }
    }

    private fun savePlan() {
        val name = binding.etPlanName.text.toString()
        val amountStr = binding.etPlanAmount.text.toString()
        val period = if (binding.autoPlanPeriod.text.toString() == "Щомісяця") "MONTHLY" else "WEEKLY"
        val category = binding.autoPlanCategory.text.toString()

        if (name.isBlank()) {
            binding.etPlanName.error = "Введіть назву"
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.etPlanAmount.error = "Введіть коректну суму"
            return
        }

        val plan = PlanEntity(
            name = name,
            amount = amount,
            categoryName = category,
            period = period,
            note = ""
        )

        viewModel.insertPlan(plan)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
