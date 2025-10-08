package com.example.whatsinyourfridge.ui.additem

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentAddItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdditemFragment : Fragment() {
    private lateinit var addItemViewModel: AddItemViewModel
    private var _binding: FragmentAddItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

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
        val itemName: EditText = binding.itemName
        val itemDate: EditText = binding.itemDate
        itemDate.setOnClickListener { showDatePickerDialog() }
        addItemButton.setOnClickListener {
            val name = itemName.text.toString()
            val date: Date = calendar.time
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (date.before(today.time)) {
                itemDate.error = "Date cannot be in the past"
                return@setOnClickListener
            }

            if (name.isNotBlank() && itemDate.text.isNotBlank()) {
                addItemViewModel.addItem(name, date)
                itemName.setText("")
                itemDate.setText("")
                calendar.time = Calendar.getInstance().time
                updateDateInView()
            }

        }
        updateDateInView()
        return root
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener {
            _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        binding.itemDate.setText(sdf.format(calendar.time))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}