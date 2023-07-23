package dev.hidakatsuya.shoppinglist.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.hidakatsuya.shoppinglist.ShoppingListApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ItemsViewModel(shopplingListApplication().container.itemsRepository)
        }
    }
}

fun CreationExtras.shopplingListApplication(): ShoppingListApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingListApplication)
