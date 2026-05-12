package com.example.finapp.ui.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finapp.R
import com.example.finapp.data.entity.TransactionEntity
import com.example.finapp.databinding.FragmentAddBinding
import com.example.finapp.utils.FormatUtils
import com.example.finapp.utils.PreferenceUtils
import com.example.finapp.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.util.*

class EditTransactionFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private var selectedDate = Calendar.getInstance().timeInMillis
    private var currentTransaction: TransactionEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getInt("transactionId") ?: -1

        viewLifecycleOwner.lifecycleScope.launch {
            currentTransaction = viewModel.getTransactionById(transactionId)
            currentTransaction?.let { populateFields(it) }
        }

        binding.etDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { updateTransaction() }
        
        binding.btnDelete.apply {
            visibility = View.VISIBLE
            setOnClickListener { showDeleteConfirmation() }
        }

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = if (checkedId == R.id.btnIncome) "INCOME" else "EXPENSE"
                setupCategoryDropdown(type, null)
            }
        }
        
        setupCurrencyDropdown()
    }

    private fun populateFields(transaction: TransactionEntity) {
        binding.etAmount.setText(transaction.amount.toString())
        binding.etNote.setText(transaction.note)
        selectedDate = transaction.date
        binding.etDate.setText(FormatUtils.formatDate(selectedDate))
        binding.autoCurrency.setText(transaction.currency, false)

        if (transaction.type == "INCOME") {
            binding.toggleType.check(R.id.btnIncome)
        } else {
            binding.toggleType.check(R.id.btnExpense)
        }

        setupCategoryDropdown(transaction.type, transaction.categoryName)
    }

    private fun setupCategoryDropdown(type: String, selectedCategory: String?) {
        viewModel.getCategoriesByType(type).observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.autoCategory.setAdapter(adapter)
            if (selectedCategory != null && names.contains(selectedCategory)) {
                binding.autoCategory.setText(selectedCategory, false)
            } else if (names.isNotEmpty()) {
                binding.autoCategory.setText(names[0], false)
            }
        }
    }

    private fun setupCurrencyDropdown() {
        val currencies = listOf("UAH", "USD", "EUR")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencies)
        binding.autoCurrency.setAdapter(adapter)
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

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Видалити операцію?")
            .setMessage("Ви впевнені, що хочете видалити цю операцію? Цю дію неможливо скасувати.")
            .setPositiveButton("Видалити") { _, _ ->
                currentTransaction?.let {
                    viewModel.deleteTransaction(it)
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun updateTransaction() {
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

        val rate = when (currency) {
            "USD" -> PreferenceUtils.getUsdRate(requireContext())
            "EUR" -> PreferenceUtils.getEurRate(requireContext())
            else -> 1.0
        }

        currentTransaction?.let {
            val updated = it.copy(
                amount = amount,
                currency = currency,
                exchangeRate = rate,
                amountInBaseCurrency = amount * rate,
                type = type,
                categoryName = category,
                date = selectedDate,
                note = note
            )
            viewModel.updateTransaction(updated)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
