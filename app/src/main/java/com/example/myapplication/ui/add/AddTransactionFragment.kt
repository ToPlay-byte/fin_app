package com.example.myapplication.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.entity.TransactionEntity
import com.example.myapplication.databinding.FragmentAddBinding
import com.example.myapplication.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private var selectedDate = Calendar.getInstance().timeInMillis
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown("EXPENSE")
        binding.etDate.setText(dateFormat.format(Date(selectedDate)))

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = if (checkedId == R.id.btnIncome) "INCOME" else "EXPENSE"
                setupCategoryDropdown(type)
            }
        }

        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener { saveTransaction() }
    }

    private fun setupCategoryDropdown(type: String) {
        viewModel.getCategoriesByType(type).observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.autoCategory.setAdapter(adapter)
            if (names.isNotEmpty()) {
                binding.autoCategory.setText(names[0], false)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        DatePickerDialog(requireContext(), { _, year, month, day ->
            val selected = Calendar.getInstance()
            selected.set(year, month, day)
            selectedDate = selected.timeInMillis
            binding.etDate.setText(dateFormat.format(selected.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun saveTransaction() {
        val amountStr = binding.etAmount.text.toString()
        val category = binding.autoCategory.text.toString()
        val note = binding.etNote.text.toString()
        val type = if (binding.toggleType.checkedButtonId == R.id.btnIncome) "INCOME" else "EXPENSE"

        if (amountStr.isBlank()) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            return
        }

        val transaction = TransactionEntity(
            amount = amount,
            type = type,
            categoryName = category,
            date = selectedDate,
            note = note
        )

        viewModel.insertTransaction(transaction)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
