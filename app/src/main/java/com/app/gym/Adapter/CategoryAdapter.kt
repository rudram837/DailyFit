package com.app.gym.Adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gym.Model.CategoryItem
import com.app.gym.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private val list: ArrayList<CategoryItem>,
    private val onClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(val binding: ItemCategoryChipBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemCategoryChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvCategory.text = item.name.replaceFirstChar {
            it.uppercase()
        }

        holder.binding.root.isSelected = position == selectedPosition

        holder.binding.root.setOnClickListener {

            val old = selectedPosition
            selectedPosition = holder.adapterPosition

            notifyItemChanged(old)
            notifyItemChanged(selectedPosition)

            onClick(item)
        }
    }

    fun submitList(newList: List<CategoryItem>) {

        list.clear()
        list.addAll(newList)

        notifyDataSetChanged()
    }
}