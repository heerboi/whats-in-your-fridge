package com.example.whatsinyourfridge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity=Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val uid:Int,
    @ColumnInfo(name = "name") val firstName: String?,
    @ColumnInfo(name = "expiry") val date: Date?,
    @ColumnInfo(name="image_path") val imagePath: String?,
    @ColumnInfo(index=true) var categoryId: Int?
)
