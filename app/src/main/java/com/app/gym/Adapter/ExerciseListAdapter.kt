package com.app.gym.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.gym.Model.ExerciseItem
import com.app.gym.R
import com.app.gym.databinding.ItemExerciseBinding
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.gym.databinding.ItemExerciseCardBinding

class ExerciseListAdapter(
    private val list: ArrayList<ExerciseItem>,
    private val onClick: (ExerciseItem) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvExerciseName.text = item.name
        holder.binding.tvMuscle.text = item.targetMuscles.joinToString(", ")
        holder.binding.tvEquipment.text = item.equipments.joinToString(", ")

//        Glide.with(holder.itemView)
//            .load(item.gifUrl)
//            .error(R.drawable.dumbbell_anim)
//            .into(holder.binding.ivExercise)

        holder.binding.tvPlaceholder.visibility = View.GONE

        Glide.with(holder.itemView)
            .load(item.gifUrl)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {

                    holder.binding.tvPlaceholder.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    holder.binding.tvPlaceholder.visibility = View.GONE
                    return false
                }
            })
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