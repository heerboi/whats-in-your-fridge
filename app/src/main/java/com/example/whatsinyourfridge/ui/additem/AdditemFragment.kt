package com.example.whatsinyourfridge.ui.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentAddItemBinding

class AdditemFragment : Fragment() {
    private lateinit var addItemViewModel: AddItemViewModel
    private var _binding: FragmentAddItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val database = AppDatabase.getDatabase(requireContext())
        val itemDao = database.itemDao()
        val factory = GenericViewModelFactory{
            AddItemViewModel(itemDao)}
        addItemViewModel = ViewModelProvider(this, factory)[AddItemViewModel::class.java]

        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val addItemButton: Button = binding.addItemButton
        val itemName: TextView = binding.itemName
        val itemDate: TextView = binding.itemDate
        addItemButton.setOnClickListener {
            val name = itemName.text.toString()
            val date = itemDate.text.toString().toInt()

            addItemViewModel.addItem(name, date)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}