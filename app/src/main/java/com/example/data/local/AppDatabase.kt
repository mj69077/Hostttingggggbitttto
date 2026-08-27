package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AthkarItem
import com.example.data.model.Ayah
import com.example.data.model.DailyReadingProgress
import com.example.data.model.DailyTask
import com.example.data.model.Fatwa
import com.example.data.model.KhatmahPlan
import com.example.data.model.QuranIndexItem
import com.example.data.model.Surah
import com.example.data.model.TasbihRecord

@Database(
    entities = [
        Surah::class,
        Ayah::class,
        AthkarItem::class,
        TasbihRecord::class,
        KhatmahPlan::class,
        DailyReadingProgress::class,
        Fatwa::class,
        DailyTask::class,
        QuranIndexItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quranDao(): QuranDao
    abstract fun quranIndexDao(): QuranIndexDao
    abstract fun athkarDao(): AthkarDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun khatmahDao(): KhatmahDao
    abstract fun fatwaDao(): FatwaDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quran_islamic_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
