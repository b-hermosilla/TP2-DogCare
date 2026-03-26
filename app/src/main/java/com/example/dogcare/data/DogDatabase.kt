package com.example.dogcare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DogTask::class], version = 1)
abstract class DogDatabase : RoomDatabase() {

    abstract fun dogTaskDao(): DogTaskDao

    companion object {
        @Volatile
        private var INSTANCE: DogDatabase? = null

        fun getDatabase(context: Context): DogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DogDatabase::class.java,
                    "dog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}