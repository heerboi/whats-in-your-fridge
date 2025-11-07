package com.example.whatsinyourfridge.ui.item

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.SharedViewModel
import com.example.whatsinyourfridge.databinding.FragmentItemBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI

class ItemFragment : Fragment() {

    companion object {
        fun newInstance() = ItemFragment()
    }
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            binding.itemName.text = item.firstName

            item.date?.let { expiryDate ->
                val format = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(format, Locale.US)
                if (item.date < Calendar.getInstance().time) {
                    binding.itemExpiry.text = "Expired on ${sdf.format(expiryDate)}! THROW IT AWAY"
                }
                else {
                    binding.itemExpiry.text = "Expiring on ${sdf.format(expiryDate)}"
                }
            } ?: run {
                binding.itemExpiry.text = "No expiry date"
            }

            if (item.imagePath != null) {
                binding.itemImage.setImageURI(Uri.fromFile(File(item.imagePath)))
            } else {
                binding.itemImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}