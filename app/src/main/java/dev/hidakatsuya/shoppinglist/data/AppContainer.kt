package dev.hidakatsuya.shoppinglist.data

import android.content.Context

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        ItemsRepository(ShoppingListDatabase.getDatabase(context).itemDao())
    }
}
