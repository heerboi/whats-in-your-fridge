package com.example.whatsinyourfridge.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinyourfridge.data.Item
import com.example.whatsinyourfridge.data.ItemDAO
import kotlinx.coroutines.launch

class GalleryViewModel(private val itemDAO: ItemDAO) : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()

    val items: LiveData<List<Item>> = itemDAO.getAllItems()

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDAO.delete(item)
        }
    }
}