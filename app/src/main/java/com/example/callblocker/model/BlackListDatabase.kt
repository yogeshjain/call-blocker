package com.example.callblocker.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BlockedNumber::class], version = 1, exportSchema = false)
abstract class BlackListDatabase : RoomDatabase() {

    abstract fun blackListDao(): BlackListDao

    companion object {

        @Volatile
        private var INSTANCE: BlackListDatabase? = null

        fun getDatabase(context: Context): BlackListDatabase? {
            if (INSTANCE == null) {
                synchronized(BlackListDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BlackListDatabase::class.java,
                            "database"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}
