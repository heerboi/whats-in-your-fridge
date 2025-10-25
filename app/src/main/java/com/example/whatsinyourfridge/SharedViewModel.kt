package com.example.whatsinyourfridge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // This will hold the current search query from the SearchView.
    val searchQuery = MutableLiveData<String>()
}