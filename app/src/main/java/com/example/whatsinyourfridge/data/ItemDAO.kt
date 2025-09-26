package com.example.whatsinyourfridge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDAO {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insertItem(vararg items: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM Item")
    suspend fun getAllItems(): List<Item>
}