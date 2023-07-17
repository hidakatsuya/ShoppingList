package dev.hidakatsuya.shoppinglist.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Item(
    val id: Int,
    initialName: String = "",
    initialCompleted: Boolean = false
) {
    var name by mutableStateOf(initialName)
    var completed by mutableStateOf(initialCompleted)
}
