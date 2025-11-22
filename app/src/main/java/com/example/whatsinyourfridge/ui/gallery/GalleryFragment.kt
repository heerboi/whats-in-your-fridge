package com.example.whatsinyourfridge.ui.gallery

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.SharedViewModel
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentItemListBinding
import com.google.android.material.snackbar.Snackbar
import androidx.core.graphics.drawable.toDrawable


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

        adapter = ItemGridAdapter(emptyList(),
            onItemClicked = { item ->
                sharedViewModel.selectedItem.value = item
                findNavController().navigate(R.id.action_nav_gallery_to_itemFragment)
            }
        )

        setupRecyclerView()
        attachSwipeToDelete()

        galleryViewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        sharedViewModel.searchQuery.observe(viewLifecycleOwner) {query ->
            galleryViewModel.filterItems(query)
        }
    }

    private fun setupRecyclerView() {
        val spanCount = 1
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)

        binding.itemsRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.itemsRecyclerView.adapter = adapter
    }

    private fun attachSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val itemToDelete = adapter.getItemAt(position)

                this@GalleryFragment.context?.let { AlertDialog.Builder(it) }
                    ?.setTitle("Delete ${itemToDelete.firstName} Item")
                    ?.setMessage("Do you really want to delete this item?")
                    ?.setIcon(android.R.drawable.ic_dialog_alert)
                    ?.setPositiveButton("Yes") { dialog, _ ->
                        galleryViewModel.deleteItem(itemToDelete)
                        dialog.dismiss()
                        view?.let { v ->
                            Snackbar.make(v, "${itemToDelete.firstName} Deleted", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    ?.setNegativeButton("No") { dialog, _ ->
                        // If user says "No", notify the adapter to redraw the item
                        adapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    ?.setOnCancelListener {
                        // Also handle case where user clicks outside the dialog
                        adapter.notifyItemChanged(position)
                    }
                    ?.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val itemView = viewHolder.itemView
                val background =
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                        .toDrawable()

                if (dX > 0) {
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                }
                else if (dX < 0) {
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }
                else {
                    background.setBounds(0, 0, 0, 0)
                }

                background.draw(c)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        itemTouchHelper.attachToRecyclerView(binding.itemsRecyclerView)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
