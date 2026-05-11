package com.example.myapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.entity.TransactionEntity
import com.example.myapplication.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

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

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        fun bind(transaction: TransactionEntity) {
            binding.tvCategory.text = transaction.categoryName
            binding.tvNote.text = transaction.note
            binding.tvDate.text = dateFormat.format(Date(transaction.date))

            val amountText = if (transaction.type == "INCOME") {
                binding.tvAmount.setTextColor(Color.parseColor("#4CAF50"))
                "+ ${transaction.amount} ₴"
            } else {
                binding.tvAmount.setTextColor(Color.parseColor("#F44336"))
                "- ${transaction.amount} ₴"
            }
            binding.tvAmount.text = amountText

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
