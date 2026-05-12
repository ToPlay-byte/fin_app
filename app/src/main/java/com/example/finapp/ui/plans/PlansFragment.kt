package com.example.finapp.ui.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finapp.R
import com.example.finapp.adapter.PlanAdapter
import com.example.finapp.data.entity.TransactionEntity
import com.example.finapp.databinding.FragmentPlansBinding
import com.example.finapp.viewmodel.FinanceViewModel

class PlansFragment : Fragment() {

    private var _binding: FragmentPlansBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()
    private lateinit var adapter: PlanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlanAdapter { plan ->
            showPayConfirmation(plan)
        }

        binding.rvPlans.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlans.adapter = adapter

        viewModel.allPlans.observe(viewLifecycleOwner) { plans ->
            adapter.submitList(plans)
        }

        binding.fabAddPlan.setOnClickListener {
            findNavController().navigate(R.id.action_plansFragment_to_addPlanFragment)
        }
    }

    private fun showPayConfirmation(plan: com.example.finapp.data.entity.PlanEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Створити витрату?")
            .setMessage("Створити витрату за цим регулярним платежем: ${plan.name} (${plan.amount} ${plan.currency})?")
            .setPositiveButton("Так") { _, _ ->
                createTransactionFromPlan(plan)
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun createTransactionFromPlan(plan: com.example.finapp.data.entity.PlanEntity) {
        val transaction = TransactionEntity(
            amount = plan.amount,
            currency = plan.currency,
            exchangeRate = 1.0, // Assuming UAH for simplicity in plans for now
            amountInBaseCurrency = plan.amount, 
            type = "EXPENSE",
            categoryName = plan.categoryName,
            date = System.currentTimeMillis(),
            note = "Плановий платіж: ${plan.name}"
        )
        viewModel.insertTransaction(transaction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
