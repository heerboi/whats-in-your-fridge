package com.example.whatsinyourfridge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val uid:Int,
    @ColumnInfo(name = "name") val firstName: String?,
    @ColumnInfo(name = "expiry") val days: Int?
)
