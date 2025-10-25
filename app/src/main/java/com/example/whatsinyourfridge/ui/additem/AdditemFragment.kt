package com.example.whatsinyourfridge.ui.additem

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentAddItemBinding
import com.google.android.material.snackbar.Snackbar
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

    private var cameraImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        uri?.let {
            binding.imageView3.setImageURI(it)
            selectedImageUri = it
        }
    }

    private val takePictureLauncher: ActivityResultLauncher<Uri> = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        selectedImageUri = cameraImageUri
        binding.imageView3.setImageURI(selectedImageUri)
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

        binding.imageView3.setOnClickListener { showImageSourceDialog() }

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
                Snackbar.make(root, "Item ${name} added successfully.", Snackbar.LENGTH_LONG).show()
                itemName.setText("")
                itemDate.setText("")
                binding.imageView3.setImageResource(android.R.drawable.ic_menu_camera)
                selectedImageUri = null
                cameraImageUri = null
                calendar.time = Calendar.getInstance().time
            }
            else {
                Snackbar.make(requireView(), "Item name or date cannot be empty", Snackbar.LENGTH_SHORT).show()
            }

        }
        return root
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> dialog.dismiss()
                }
            }.show()
    }

    private fun dispatchTakePictureIntent() {
        val imageFile = createImageFile()

        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

        takePictureLauncher.launch(cameraImageUri)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
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
