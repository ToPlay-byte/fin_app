package com.example.myapplication.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentStatisticsBinding
import com.example.myapplication.databinding.ItemStatCategoryBinding
import com.example.myapplication.viewmodel.FinanceViewModel
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            val incomeVal = income ?: 0.0
            val expenseVal = viewModel.totalExpense.value ?: 0.0
            binding.tvStatIncome.text = getString(R.string.income, incomeVal.toString())
            binding.tvStatBalance.text = getString(R.string.balance, (incomeVal - expenseVal).toString())
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            val incomeVal = viewModel.totalIncome.value ?: 0.0
            val expenseVal = expense ?: 0.0
            binding.tvStatExpense.text = getString(R.string.expense, expenseVal.toString())
            binding.tvStatBalance.text = getString(R.string.balance, (incomeVal - expenseVal).toString())
        }

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            updateCategoryStats(transactions)
        }
    }

    private fun updateCategoryStats(transactions: List<com.example.myapplication.data.entity.TransactionEntity>) {
        val expenses = transactions.filter { it.type == "EXPENSE" }
        val total = expenses.sumOf { it.amount }
        val grouped = expenses.groupBy { it.categoryName }.mapValues { entry -> entry.value.sumOf { it.amount } }

        binding.llCategoriesContainer.removeAllViews()
        grouped.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
            val itemBinding = ItemStatCategoryBinding.inflate(layoutInflater, binding.llCategoriesContainer, false)
            itemBinding.tvCategoryName.text = category
            itemBinding.tvCategoryAmount.text = "$amount ₴"
            itemBinding.progressCategory.progress = if (total > 0) ((amount / total) * 100).toInt() else 0
            binding.llCategoriesContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
