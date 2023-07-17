package dev.hidakatsuya.shoppinglist

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import dev.hidakatsuya.shoppinglist.data.Item

class ItemsViewModel : ViewModel() {
    private val _items = getItems().toMutableStateList()
    val items: List<Item>
        get() = _items.filter { !it.completed }

    fun remove(item: Item) {
        _items.remove(item)
    }

    fun complete(item: Item) {
        findById(item.id)?.let { it.completed = true }
    }

    fun changeName(item: Item, newName: String) {
        if (newName.isBlank()) {
            remove(item)
        } else {
            findById(item.id)?.let { it.name = newName }
        }
    }

    private fun findById(id: Int): Item? {
        return _items.find { it.id == id }
    }
}

private fun getItems() = List(30) { i -> Item(i, "Task #$i") }
