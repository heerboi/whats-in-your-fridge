package com.example.whatsinyourfridge.ui.gallery

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.SharedViewModel
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentItemListBinding
import com.google.android.material.snackbar.Snackbar


class GalleryFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var adapter: ItemGridAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()

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

        adapter = ItemGridAdapter(emptyList(), onDeleteClicked = { item ->

            this.context?.let { AlertDialog.Builder(it) }
                ?.setTitle("Delete ${item.firstName} Item")
                ?.setMessage("Do you really want to delete this item?")
                ?.setIcon(android.R.drawable.ic_dialog_alert)
                ?.setPositiveButton("Yes"
                ) { dialog, whichButton ->
                    galleryViewModel.deleteItem(item)
                    dialog.dismiss()
                    Snackbar.make(view, "${item.firstName} Deleted", Snackbar.LENGTH_LONG).show()
                }
                ?.setNegativeButton("No", null)?.show()
        })

        setupRecyclerView()

        galleryViewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        sharedViewModel.searchQuery.observe(viewLifecycleOwner) {query ->
            galleryViewModel.filterItems(query)
        }
    }

    private fun setupRecyclerView() {
        val spanCount = 2
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)

        binding.itemsRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.itemsRecyclerView.adapter = adapter
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
