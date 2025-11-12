package com.univalle.inventorywidget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory.db"
                )
                    .allowMainThreadQueries()    
                    .fallbackToDestructiveMigration()// solo borra la base cuando cambias el esquema en ambiente de Desarrollo no hay problema, en produccion en mejor migrar manualmente.
                    .build()
                    .also { INSTANCE = it }
            }
    }
}