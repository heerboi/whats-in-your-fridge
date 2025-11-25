package com.example.whatsinyourfridge.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinyourfridge.data.Category
import com.example.whatsinyourfridge.data.CategoryDAO
import com.example.whatsinyourfridge.data.Item
import com.example.whatsinyourfridge.data.ItemDAO
import kotlinx.coroutines.launch

class GalleryViewModel(private val itemDAO: ItemDAO, private val categoryDAO: CategoryDAO) : ViewModel() {

    private val allItemsFromDB: LiveData<List<Item>> = itemDAO.getAllItems()
    val allCategories: LiveData<List<Category>> = categoryDAO.getAll()

    private val _filteredItems = MutableLiveData<List<Item>>()
    val filteredItems: LiveData<List<Item>> get() = _filteredItems

    init {
        allItemsFromDB.observeForever { items ->
            _filteredItems.value = items
        }
    }

    fun filterItems(query:String?) {
        val allItems = allItemsFromDB.value

        if (query.isNullOrBlank()) {
            _filteredItems.value = allItems ?: emptyList()
        } else {
            _filteredItems.value = allItems?.filter { item ->
                item.firstName?.contains(query, ignoreCase = true) ?: false
            } ?: emptyList()

        }
    }
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDAO.delete(item)
        }
    }

    override fun onCleared() {
        super.onCleared()
        allItemsFromDB.removeObserver { }
    }
}