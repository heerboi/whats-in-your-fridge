package com.example.whatsinyourfridge.ui.additem

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinyourfridge.data.Category
import com.example.whatsinyourfridge.data.CategoryDAO
import com.example.whatsinyourfridge.data.Item
import com.example.whatsinyourfridge.data.ItemDAO
import kotlinx.coroutines.launch
import java.util.Date

class AddItemViewModel(private val itemDao: ItemDAO, private val categoryDAO: CategoryDAO) : ViewModel() {
    val allCategories: LiveData<List<Category>> = categoryDAO.getAll()


    fun addItem(item:Item) {
        viewModelScope.launch {
            itemDao.insertItem(item)
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