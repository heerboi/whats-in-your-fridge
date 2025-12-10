package com.example.whatsinyourfridge.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.data.Category
import com.example.whatsinyourfridge.data.Item

class CategoryGridAdapter(private var categories: List<Category>) : RecyclerView.Adapter<CategoryGridAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryGridAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryGridAdapter.CategoryViewHolder,
        position: Int
    ) {
        val category = categories[position]
        holder.categoryText.text = category.name
        holder.categoryInfo.text = category.info
    }

    override fun getItemCount(): Int {
        return categories.size
    }
    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }


    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val categoryInfo: TextView = view.findViewById(R.id.categoryInfo)
    }
    fun getCategoryAt(position: Int): Category {
        return categories[position]
    }
}
