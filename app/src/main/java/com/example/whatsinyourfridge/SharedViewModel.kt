package com.example.whatsinyourfridge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsinyourfridge.data.Item

class SharedViewModel : ViewModel() {
    // This will hold the current search query from the SearchView.
    val searchQuery = MutableLiveData<String>()

    // User clicked Item
    val selectedItem = MutableLiveData<Item>()
}