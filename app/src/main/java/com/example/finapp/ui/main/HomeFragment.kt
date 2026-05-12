package com.example.finapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finapp.R
import com.example.finapp.adapter.TransactionAdapter
import com.example.finapp.databinding.FragmentHomeBinding
import com.example.finapp.utils.FormatUtils
import com.example.finapp.viewmodel.FinanceViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter { transaction ->
            val bundle = Bundle().apply { putInt("transactionId", transaction.id) }
            findNavController().navigate(R.id.action_homeFragment_to_editTransactionFragment, bundle)
        }

        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = adapter

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isEmpty()) {
                binding.labelRecent.text = "Операцій поки немає. Додайте першу операцію"
                adapter.submitList(emptyList())
            } else {
                binding.labelRecent.text = "Останні операції"
                adapter.submitList(transactions.take(5))
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            val incomeVal = income ?: 0.0
            binding.tvTotalIncome.text = "Доходи: ${FormatUtils.formatCurrency(incomeVal)}"
            updateBalance()
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            val expenseVal = expense ?: 0.0
            binding.tvTotalExpense.text = "Витрати: ${FormatUtils.formatCurrency(expenseVal)}"
            updateBalance()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addTransactionFragment)
        }
    }

    private fun updateBalance() {
        val income = viewModel.totalIncome.value ?: 0.0
        val expense = viewModel.totalExpense.value ?: 0.0
        binding.tvTotalBalance.text = "Баланс: ${FormatUtils.formatCurrency(income - expense)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
