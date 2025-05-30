package com.vidz.datastore

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.vidz.datastore.dao.CartDao
import com.vidz.datastore.entity.CartEntity

@Database(
    entities = [CartEntity::class],
    version = 1,
    exportSchema = true
)
abstract class BlindBoxDatabase : RoomDatabase() {
    
    abstract fun cartDao(): CartDao
    
    companion object {
        const val DATABASE_NAME = "blindbox_database"
    }
} 