package com.example.whatsinyourfridge.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinyourfridge.data.Category
import com.example.whatsinyourfridge.data.CategoryDAO
import com.example.whatsinyourfridge.data.ItemDAO
import kotlinx.coroutines.launch

class CategoryViewModel(private val itemDAO: ItemDAO, private val categoryDAO: CategoryDAO) : ViewModel() {
    val allCategories = categoryDAO.getAll()


    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDAO.delete(category)
        }
    }
}