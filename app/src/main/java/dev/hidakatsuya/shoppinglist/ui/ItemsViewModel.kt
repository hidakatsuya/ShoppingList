package dev.hidakatsuya.shoppinglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hidakatsuya.shoppinglist.data.Item
import dev.hidakatsuya.shoppinglist.data.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _editItemUiState = MutableStateFlow(ItemsUiState.EditItem())
    val editItemUiState: StateFlow<ItemsUiState.EditItem> = _editItemUiState.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun newItem() {
        _editItemUiState.value = ItemsUiState.EditItem(isEditing = true)
    }

    fun editItem(item: Item) {
        _editItemUiState.value = ItemsUiState.EditItem(
            isEditing = true,
            details = item.toItemDetails(),
            isValid = true
        )
    }

    fun updateEditItemUiState(itemDetails: ItemDetails) {
        _editItemUiState.value = ItemsUiState.EditItem(
            isEditing = true,
            details = itemDetails,
            isValid = itemDetails.validate()
        )
    }

    fun finishItemEditing() {
        _editItemUiState.value = ItemsUiState.EditItem(isEditing = false)
    }

    suspend fun saveItem() {
        val uiState = editItemUiState.value

        if (!uiState.isValid) return

        if (uiState.details.id == 0) {
            itemsRepository.addItem(uiState.details.toItem())
        } else {
            itemsRepository.updateItem(uiState.details.toItem())
        }
        finishItemEditing()
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

fun ItemDetails.validate(): Boolean {
    return name.isNotEmpty()
}

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name
)
