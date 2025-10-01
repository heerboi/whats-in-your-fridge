package com.example.whatsinyourfridge.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.data.Item

class ItemGridAdapter (private var items: List<Item>) : RecyclerView.Adapter<ItemGridAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.itemName.text = item.firstName
        holder.itemDays.text = "Days: ${item.days}"
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Item>) {
        items=newItems
        notifyDataSetChanged()
    }

    class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemDays: TextView = view.findViewById(R.id.itemDays)
    }

}