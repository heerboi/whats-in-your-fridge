package com.example.whatsinyourfridge.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsinyourfridge.data.Item
import com.example.whatsinyourfridge.data.ItemDAO

class GalleryViewModel(private val itemDAO: ItemDAO) : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()

    val items: LiveData<List<Item>> = itemDAO.getAllItems()
}