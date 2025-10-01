package com.example.whatsinyourfridge.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentGalleryBinding
import com.example.whatsinyourfridge.databinding.FragmentItemListBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var adapter: ItemGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val itemDao = database.itemDao()
        val factory = GenericViewModelFactory{ GalleryViewModel(itemDao) }
        galleryViewModel = ViewModelProvider(this, factory)[GalleryViewModel::class.java]

        adapter = ItemGridAdapter(emptyList())

        binding.itemsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.itemsRecyclerView.adapter = adapter

        galleryViewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}