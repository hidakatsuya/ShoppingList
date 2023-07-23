package dev.hidakatsuya.shoppinglist

import android.app.Application
import dev.hidakatsuya.shoppinglist.data.AppContainer
import dev.hidakatsuya.shoppinglist.data.AppDataContainer

class ShoppingListApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
