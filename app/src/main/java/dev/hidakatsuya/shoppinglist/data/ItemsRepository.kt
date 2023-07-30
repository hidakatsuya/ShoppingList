package dev.hidakatsuya.shoppinglist.data

import kotlinx.coroutines.flow.Flow

class ItemsRepository(private val itemDao: ItemDao) {
    fun all(): Flow<List<Item>> = itemDao.getAllNotBought()

    suspend fun addItem(item: Item) = itemDao.insert(item)

    suspend fun updateItem(item: Item) = itemDao.update(item)

    suspend fun removeItem(item: Item) = itemDao.delete(item)
}
