package dev.hidakatsuya.shoppinglist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: ShoppingListDatabase? = null

        fun getDatabase(context: Context): ShoppingListDatabase {
            return Instance ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context,
                    ShoppingListDatabase::class.java,
                    "shopping_list_database"
                )
                database
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
