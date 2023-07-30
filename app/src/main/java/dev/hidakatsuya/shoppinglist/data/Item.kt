package dev.hidakatsuya.shoppinglist.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    indices = [Index(value = ["bought"])]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val bought: Int = 0
)
