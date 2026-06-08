package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BurdenEntity::class, SoapEntity::class, DailyPrayerEntity::class, StickyNoteEntity::class], version = 4, exportSchema = false)
abstract class HouseDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao

    companion object {
        @Volatile
        private var INSTANCE: HouseDatabase? = null

        fun getDatabase(context: Context): HouseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HouseDatabase::class.java,
                    "the_house_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
