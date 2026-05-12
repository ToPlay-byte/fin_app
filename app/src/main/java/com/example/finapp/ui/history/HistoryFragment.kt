package com.example.finapp.ui.history

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
import com.example.finapp.databinding.FragmentHistoryBinding
import com.example.finapp.viewmodel.FinanceViewModel
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    private var currentType = "ALL"
    private var currentPeriod = "ALL_TIME"

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

        observeTransactions()

        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            currentType = when (checkedIds.firstOrNull()) {
                R.id.chipIncome -> "INCOME"
                R.id.chipExpense -> "EXPENSE"
                else -> "ALL"
            }
            applyFilters()
        }

        binding.chipGroupPeriod.setOnCheckedStateChangeListener { _, checkedIds ->
            currentPeriod = when (checkedIds.firstOrNull()) {
                R.id.chipCurrentMonth -> "MONTH"
                else -> "ALL_TIME"
            }
            applyFilters()
        }
    }

    private fun observeTransactions() {
        viewModel.allTransactions.observe(viewLifecycleOwner) {
            applyFilters()
        }
    }

    private fun applyFilters() {
        var list = viewModel.allTransactions.value ?: emptyList()
        
        // Filter by period
        if (currentPeriod == "MONTH") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfMonth = calendar.timeInMillis
            list = list.filter { it.date >= startOfMonth }
        }

        // Filter by type
        if (currentType != "ALL") {
            list = list.filter { it.type == currentType }
        }

        adapter.submitList(list)
        
        if (list.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.tvEmptyHistory.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.tvEmptyHistory.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
