package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TransactionAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.viewmodel.FinanceViewModel

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
            adapter.submitList(transactions.take(5))
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            val incomeVal = income ?: 0.0
            val expenseVal = viewModel.totalExpense.value ?: 0.0
            binding.tvTotalIncome.text = getString(R.string.income, incomeVal.toString())
            binding.tvTotalBalance.text = getString(R.string.balance, (incomeVal - expenseVal).toString())
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            val incomeVal = viewModel.totalIncome.value ?: 0.0
            val expenseVal = expense ?: 0.0
            binding.tvTotalExpense.text = getString(R.string.expense, expenseVal.toString())
            binding.tvTotalBalance.text = getString(R.string.balance, (incomeVal - expenseVal).toString())
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addTransactionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
