package com.example.finapp.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.finapp.R
import com.example.finapp.databinding.FragmentStatisticsBinding
import com.example.finapp.databinding.ItemStatCategoryBinding
import com.example.finapp.utils.FormatUtils
import com.example.finapp.utils.PdfExporter
import com.example.finapp.viewmodel.FinanceViewModel
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()

    private var currentPeriod = "ALL_TIME"
    private var filteredList: List<com.example.finapp.data.entity.TransactionEntity> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allTransactions.observe(viewLifecycleOwner) {
            updateUI()
        }

        binding.chipGroupStatPeriod.setOnCheckedStateChangeListener { _, checkedIds ->
            currentPeriod = when (checkedIds.firstOrNull()) {
                R.id.chipStatMonth -> "MONTH"
                else -> "ALL_TIME"
            }
            updateUI()
        }

        binding.btnExportPdf.setOnClickListener {
            exportPdf()
        }
    }

    private fun updateUI() {
        var list = viewModel.allTransactions.value ?: emptyList()

        if (currentPeriod == "MONTH") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfMonth = calendar.timeInMillis
            list = list.filter { it.date >= startOfMonth }
        }
        
        filteredList = list

        if (list.isEmpty()) {
            binding.tvEmptyStat.visibility = View.VISIBLE
            binding.llCategoriesContainer.visibility = View.GONE
            binding.labelCatStats.visibility = View.GONE
            binding.tvStatBalance.text = "Баланс: 0.00 ₴"
            binding.tvStatIncome.text = "Доходи: 0.00 ₴"
            binding.tvStatExpense.text = "Витрати: 0.00 ₴"
            binding.tvStatCount.text = "Кількість операцій: 0"
            binding.tvStatLargestCategory.text = "Найбільша категорія: -"
            binding.btnExportPdf.isEnabled = false
            return
        }

        binding.tvEmptyStat.visibility = View.GONE
        binding.llCategoriesContainer.visibility = View.VISIBLE
        binding.labelCatStats.visibility = View.VISIBLE
        binding.btnExportPdf.isEnabled = true

        val income = list.filter { it.type == "INCOME" }.sumOf { it.amountInBaseCurrency }
        val expense = list.filter { it.type == "EXPENSE" }.sumOf { it.amountInBaseCurrency }
        
        binding.tvStatBalance.text = "Баланс: ${FormatUtils.formatCurrency(income - expense)}"
        binding.tvStatIncome.text = "Доходи: ${FormatUtils.formatCurrency(income)}"
        binding.tvStatExpense.text = "Витрати: ${FormatUtils.formatCurrency(expense)}"
        binding.tvStatCount.text = "Кількість операцій: ${list.size}"

        val expenses = list.filter { it.type == "EXPENSE" }
        if (expenses.isNotEmpty()) {
            val total = expenses.sumOf { it.amountInBaseCurrency }
            val grouped = expenses.groupBy { it.categoryName }.mapValues { it.value.sumOf { it.amountInBaseCurrency } }
            val largest = grouped.maxByOrNull { it.value }
            binding.tvStatLargestCategory.text = "Найбільша категорія: ${largest?.key ?: "-"}"

            binding.llCategoriesContainer.removeAllViews()
            grouped.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                val itemBinding = ItemStatCategoryBinding.inflate(layoutInflater, binding.llCategoriesContainer, false)
                itemBinding.tvCategoryName.text = category
                itemBinding.tvCategoryAmount.text = FormatUtils.formatCurrency(amount)
                itemBinding.progressCategory.progress = ((amount / total) * 100).toInt()
                binding.llCategoriesContainer.addView(itemBinding.root)
            }
        } else {
            binding.tvStatLargestCategory.text = "Найбільша категорія: -"
            binding.llCategoriesContainer.removeAllViews()
        }
    }

    private fun exportPdf() {
        val income = filteredList.filter { it.type == "INCOME" }.sumOf { it.amountInBaseCurrency }
        val expense = filteredList.filter { it.type == "EXPENSE" }.sumOf { it.amountInBaseCurrency }
        
        PdfExporter.exportToPdf(
            requireContext(),
            filteredList,
            income - expense,
            income,
            expense
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
