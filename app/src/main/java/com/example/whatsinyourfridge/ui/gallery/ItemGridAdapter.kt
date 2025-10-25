package com.example.whatsinyourfridge.ui.gallery

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.data.Item
import java.io.File
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

class ItemGridAdapter (private var items: List<Item>, private val onDeleteClicked: (Item) -> Unit) : RecyclerView.Adapter<ItemGridAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_card, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val item = items[position]
        Log.d("ItemGridAdapter", "Binding item: ${item.date}")
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        item.date?.let { expiryUtilDate ->
            val expiryLocalDate = Instant.ofEpochMilli(expiryUtilDate.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val currentTime = LocalDate.now()
            val daysLeft =
                Duration.between(currentTime.atStartOfDay(), expiryLocalDate.atStartOfDay()).toDays()

            val colorRes = when {
                daysLeft <= 0 -> R.color.red
                daysLeft <= 5 -> R.color.yellow
                else -> R.color.green
            }

            holder.gridCardLayout.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    colorRes
                )
            )

            holder.itemDays.text = when {
                daysLeft < 0 -> "Expired ${-daysLeft} days ago"
                daysLeft == 0L -> "Expires today"
                daysLeft == 1L -> "Expires in 1 day"
                else -> "Expires in $daysLeft days"
            }
            holder.itemDays.append(" ${sdf.format(item.date)})")
        } ?: run {
            holder.itemDays.text = "No Expiry Date"
            holder.gridCardLayout.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemName.text = item.firstName
        if (item.imagePath != null) {
            holder.itemImage.setImageURI(Uri.fromFile(File(item.imagePath)))
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.deleteItemButton.setOnClickListener { onDeleteClicked(item)
        true}
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Item>) {
        items=newItems
        notifyDataSetChanged()
    }

    class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemDays: TextView = view.findViewById(R.id.itemDays)
        val itemImage: ImageView = view.findViewById(R.id.imageView2)

        val deleteItemButton: ImageView = view.findViewById(R.id.deleteItemButton)

        val gridCardLayout: View = view.findViewById(R.id.grid_card_layout)

    }

}