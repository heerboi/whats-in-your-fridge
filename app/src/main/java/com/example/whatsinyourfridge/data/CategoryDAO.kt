package com.example.whatsinyourfridge.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM Category ORDER BY name ASC")
    fun getAll(): LiveData<List<Category>>

    @Query("SELECT COUNT(*) FROM Item WHERE categoryId = :categoryId")
    suspend fun getItemCountForCategory(categoryId: Int): Int
}
