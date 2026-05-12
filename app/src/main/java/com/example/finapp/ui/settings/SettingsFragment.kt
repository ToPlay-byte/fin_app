package com.example.finapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.finapp.R
import com.example.finapp.databinding.FragmentSettingsBinding
import com.example.finapp.utils.PreferenceUtils
import com.example.finapp.viewmodel.FinanceViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etUsdRate.setText(PreferenceUtils.getUsdRate(requireContext()).toString())
        binding.etEurRate.setText(PreferenceUtils.getEurRate(requireContext()).toString())

        binding.btnSaveRates.setOnClickListener {
            saveRates()
        }

        binding.btnRegularPayments.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_plansFragment)
        }

        binding.btnClearData.setOnClickListener {
            showClearDataConfirmation()
        }
    }

    private fun saveRates() {
        val usdRate = binding.etUsdRate.text.toString().toDoubleOrNull() ?: 40.0
        val eurRate = binding.etEurRate.text.toString().toDoubleOrNull() ?: 43.0
        
        PreferenceUtils.setUsdRate(requireContext(), usdRate)
        PreferenceUtils.setEurRate(requireContext(), eurRate)
        
        Toast.makeText(requireContext(), "Курси збережено", Toast.LENGTH_SHORT).show()
    }

    private fun showClearDataConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистити всі дані?")
            .setMessage("Ви впевнені, що хочете видалити всі записи? Цю дію неможливо скасувати.")
            .setPositiveButton("Видалити все") { _, _ ->
                viewModel.clearAllData()
                Toast.makeText(requireContext(), "Дані очищено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
