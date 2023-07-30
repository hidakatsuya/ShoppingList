package dev.hidakatsuya.shoppinglist.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hidakatsuya.shoppinglist.data.Item
import dev.hidakatsuya.shoppinglist.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ItemsViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    val itemListUiState: StateFlow<ItemsUiState.ItemList> =
        itemsRepository.all().map { ItemsUiState.ItemList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemsUiState.ItemList()
            )
    var editItemUiState by mutableStateOf(ItemsUiState.EditItem())
        private set

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun newItem() {
        editItemUiState = ItemsUiState.EditItem(isEditing = true)
    }

    fun editItem(item: Item) {
        editItemUiState = ItemsUiState.EditItem(
            isEditing = true,
            details = item.toItemDetails()
        )
    }

    fun updateEditItemUiState(itemDetails: ItemDetails) {
        editItemUiState = ItemsUiState.EditItem(
            isEditing = true,
            details = itemDetails,
            isValid = true
        )
    }

    fun finishItemEditing() {
        editItemUiState = ItemsUiState.EditItem(isEditing = false)
    }

    suspend fun saveItem() {
        if (editItemUiState.details.id == 0) {
            itemsRepository.addItem(editItemUiState.details.toItem())
        } else {
            itemsRepository.updateItem(editItemUiState.details.toItem())
        }
    }

    suspend fun removeItem(item: Item) {
        itemsRepository.removeItem(item)
    }

    suspend fun changeItemBought(item: Item) {
        itemsRepository.updateItem(item.copy(bought = 1))
    }
}

object ItemsUiState {
    data class ItemList(val items: List<Item> = listOf())
    data class EditItem(
        val isEditing: Boolean = false,
        val details: ItemDetails = ItemDetails(),
        val isValid: Boolean = false
    )
}

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val bought: Boolean = false
)

fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name
)

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name
)
