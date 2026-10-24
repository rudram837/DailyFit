package com.app.gym.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.gym.Model.ExerciseItem
import com.app.gym.databinding.ItemExerciseCardBinding

class ExerciseAdapter(
    private val list: ArrayList<ExerciseItem>,
    private val onClick: (ExerciseItem) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExerciseCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemExerciseCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvExerciseName.text = item.name
        holder.binding.tvTarget.text = item.targetMuscles.joinToString(", ")

        Glide.with(holder.itemView)
            .load(item.gifUrl)
            .into(holder.binding.ivExercise)

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun submitList(newList: List<ExerciseItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}