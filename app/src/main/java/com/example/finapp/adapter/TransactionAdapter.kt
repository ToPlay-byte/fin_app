package com.example.finapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.data.entity.TransactionEntity
import com.example.finapp.databinding.ItemTransactionBinding
import com.example.finapp.utils.FormatUtils

class TransactionAdapter(private val onClick: (TransactionEntity) -> Unit) :
    ListAdapter<TransactionEntity, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionEntity) {
            binding.tvCategory.text = transaction.categoryName
            binding.tvNote.text = transaction.note
            binding.tvDate.text = FormatUtils.formatDate(transaction.date)

            val color = if (transaction.type == "INCOME") {
                Color.parseColor("#4CAF50")
            } else {
                Color.parseColor("#F44336")
            }
            binding.tvAmount.setTextColor(color)

            val prefix = if (transaction.type == "INCOME") "+" else "-"
            val originalAmountStr = "${transaction.amount} ${transaction.currency}"
            
            if (transaction.currency != "UAH") {
                val baseAmountStr = FormatUtils.formatCurrency(transaction.amountInBaseCurrency)
                binding.tvAmount.text = "$prefix $originalAmountStr ≈ $baseAmountStr"
            } else {
                binding.tvAmount.text = "$prefix ${FormatUtils.formatCurrency(transaction.amount)}"
            }

            binding.root.setOnClickListener { onClick(transaction) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TransactionEntity>() {
            override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
