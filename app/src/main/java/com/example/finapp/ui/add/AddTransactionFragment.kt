package com.example.finapp.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.finapp.R
import com.example.finapp.data.entity.TransactionEntity
import com.example.finapp.databinding.FragmentAddBinding
import com.example.finapp.utils.FormatUtils
import com.example.finapp.utils.PreferenceUtils
import com.example.finapp.viewmodel.FinanceViewModel
import java.util.*

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private var selectedDate = Calendar.getInstance().timeInMillis

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown("EXPENSE")
        setupCurrencyDropdown()
        
        binding.etDate.setText(FormatUtils.formatDate(selectedDate))

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

    private fun setupCurrencyDropdown() {
        val currencies = listOf("UAH", "USD", "EUR")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencies)
        binding.autoCurrency.setAdapter(adapter)
        binding.autoCurrency.setText("UAH", false)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        DatePickerDialog(requireContext(), { _, year, month, day ->
            val selected = Calendar.getInstance()
            selected.set(year, month, day)
            selectedDate = selected.timeInMillis
            binding.etDate.setText(FormatUtils.formatDate(selectedDate))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun saveTransaction() {
        val amountStr = binding.etAmount.text.toString()
        val currency = binding.autoCurrency.text.toString()
        val category = binding.autoCategory.text.toString()
        val note = binding.etNote.text.toString()
        val type = if (binding.toggleType.checkedButtonId == R.id.btnIncome) "INCOME" else "EXPENSE"

        if (amountStr.isBlank()) {
            binding.etAmount.error = "Введіть суму операції"
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.etAmount.error = "Сума має бути більшою за нуль"
            return
        }

        if (category.isBlank()) {
            Toast.makeText(requireContext(), "Оберіть категорію", Toast.LENGTH_SHORT).show()
            return
        }

        val rate = when (currency) {
            "USD" -> PreferenceUtils.getUsdRate(requireContext())
            "EUR" -> PreferenceUtils.getEurRate(requireContext())
            else -> 1.0
        }

        val transaction = TransactionEntity(
            amount = amount,
            currency = currency,
            exchangeRate = rate,
            amountInBaseCurrency = amount * rate,
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
