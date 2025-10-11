package com.example.whatsinyourfridge.ui.additem

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentAddItemBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AdditemFragment : Fragment() {
    private lateinit var addItemViewModel: AddItemViewModel
    private var _binding: FragmentAddItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        uri?.let {
            binding.imageView3.setImageURI(it)
            selectedImageUri = it
        }
    }

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

        binding.imageView3.setOnClickListener { pickImageLauncher.launch("image/*") }

        val addItemButton: Button = binding.addItemButton
        val itemName: EditText = binding.itemName
        val itemDate: EditText = binding.itemDate
        itemDate.setOnClickListener { showDatePickerDialog() }

        addItemButton.setOnClickListener {
            val name = itemName.text.toString()
            val date: Date = calendar.time
            var imagePath: String? = null
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            selectedImageUri?.let {uri ->
                imagePath = saveImageToInternalStorage(uri)
            }

            if (date.before(today.time)) {
                itemDate.error = "Date cannot be in the past"
                return@setOnClickListener
            }

            if (name.isNotBlank() && itemDate.text.isNotBlank()) {
                addItemViewModel.addItem(name, date, imagePath)
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


    private fun saveImageToInternalStorage(uri: Uri): String? {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)

            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(requireContext().filesDir, fileName)

            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            return file.absolutePath
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
