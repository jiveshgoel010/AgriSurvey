package com.agrisurvey.app.ui.insights

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agrisurvey.app.databinding.ItemStateCardBinding

class StateCardAdapter(
    private val stateList: Array<String>,
    private val onStateClicked: (String) -> Unit
) : RecyclerView.Adapter<StateCardAdapter.StateViewHolder>() {

    inner class StateViewHolder(private val binding: ItemStateCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stateName: String) {
            binding.textViewStateName.text = stateName
            binding.cardState.setOnClickListener {
                onStateClicked(stateName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val binding = ItemStateCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.bind(stateList[position])
    }

    override fun getItemCount(): Int = stateList.size
}
