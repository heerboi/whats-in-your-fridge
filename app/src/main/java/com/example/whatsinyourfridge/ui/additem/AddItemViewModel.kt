package com.example.whatsinyourfridge.ui.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinyourfridge.data.Item
import com.example.whatsinyourfridge.data.ItemDAO
import kotlinx.coroutines.launch

class AddItemViewModel(private val itemDao: ItemDAO) : ViewModel() {

    fun addItem(name: String, date: Int) {
        viewModelScope.launch {
            val newItem = Item(uid=0, firstName = name, days=date)
            itemDao.insertItem(newItem)
        }
    }

//    fun getItems() {
//        viewModelScope.launch {
//            val items = itemDao.getAllItems()
//
//            println("All items:")
//            items.forEach { item ->
//                println("Item: ${item.firstName}, Date: ${item.days}")
//            }
//        }
//    }
}