package com.example.finapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.data.entity.PlanEntity
import com.example.finapp.databinding.ItemPlanBinding

class PlanAdapter(private val onPayClick: (PlanEntity) -> Unit) :
    ListAdapter<PlanEntity, PlanAdapter.PlanViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlanViewHolder(private val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: PlanEntity) {
            binding.tvPlanName.text = plan.name
            binding.tvPlanDetails.text = "${plan.amount} ${plan.currency} | ${if(plan.period == "MONTHLY") "Щомісяця" else "Щотижня"}"
            binding.btnPayNow.setOnClickListener { onPayClick(plan) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PlanEntity>() {
            override fun areItemsTheSame(oldItem: PlanEntity, newItem: PlanEntity): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PlanEntity, newItem: PlanEntity): Boolean = oldItem == newItem
        }
    }
}
