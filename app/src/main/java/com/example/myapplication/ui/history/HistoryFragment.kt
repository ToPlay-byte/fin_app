package com.example.myapplication.ui.history

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
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.viewmodel.FinanceViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter { transaction ->
            val bundle = Bundle().apply { putInt("transactionId", transaction.id) }
            findNavController().navigate(R.id.action_historyFragment_to_editTransactionFragment, bundle)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        var currentFilter = "ALL"

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            val filtered = if (currentFilter == "ALL") {
                transactions
            } else {
                transactions.filter { it.type == currentFilter }
            }
            adapter.submitList(filtered)
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when (checkedIds.firstOrNull()) {
                R.id.chipIncome -> "INCOME"
                R.id.chipExpense -> "EXPENSE"
                else -> "ALL"
            }
            // Trigger update
            viewModel.allTransactions.value?.let { transactions ->
                val filtered = if (currentFilter == "ALL") {
                    transactions
                } else {
                    transactions.filter { it.type == currentFilter }
                }
                adapter.submitList(filtered)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
