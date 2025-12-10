 package com.example.whatsinyourfridge.ui.categories

import android.graphics.Canvas
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinyourfridge.GenericViewModelFactory
import com.example.whatsinyourfridge.R
import com.example.whatsinyourfridge.data.AppDatabase
import com.example.whatsinyourfridge.databinding.FragmentCategoryBinding
import com.example.whatsinyourfridge.ui.gallery.ItemGridAdapter
import com.google.android.material.snackbar.Snackbar

 class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
     private val binding get() = _binding!!
     private lateinit var adapter: CategoryGridAdapter

     private lateinit var categoryViewModel: CategoryViewModel


    override fun onCreateView(inflater: LayoutInflater,
                          container: ViewGroup?,
                          savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         val database = AppDatabase.getDatabase(requireContext())
         val itemDao = database.itemDao()
         val categoryDao = database.categoryDao()
         val factory = GenericViewModelFactory{ CategoryViewModel(itemDao, categoryDao) }
         categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
        
         adapter = CategoryGridAdapter(emptyList())
         
         setupRecyclerView()
         attachSwipeToDelete()
         
         categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
             adapter.updateCategories(categories)
         }
     }
     private fun setupRecyclerView() {
         val spanCount = 1
         val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)

         binding.categoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
         binding.categoryRecyclerView.adapter = adapter
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

                 val categoryToDelete = adapter.getCategoryAt(position)

                 this@CategoryFragment.context?.let { AlertDialog.Builder(it) }
                     ?.setTitle("Delete ${categoryToDelete.name} Item")
                     ?.setMessage("Do you really want to delete this item?")
                     ?.setIcon(android.R.drawable.ic_dialog_alert)
                     ?.setPositiveButton("Yes") { dialog, _ ->
                         categoryViewModel.deleteCategory(categoryToDelete)
                         dialog.dismiss()
                         view?.let { v ->
                             Snackbar.make(v, "${categoryToDelete.name} Deleted", Snackbar.LENGTH_LONG).show()
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

         itemTouchHelper.attachToRecyclerView(binding.categoryRecyclerView)
     }
}