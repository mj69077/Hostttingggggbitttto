package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyReadingProgress
import com.example.data.model.KhatmahPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface KhatmahDao {

    @Query("SELECT * FROM khatmah_plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<KhatmahPlan?>

    @Query("SELECT * FROM khatmah_plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlanSync(): KhatmahPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlan(plan: KhatmahPlan)

    @Query("SELECT * FROM daily_progress ORDER BY dateKey DESC LIMIT 30")
    fun getRecentDailyProgress(): Flow<List<DailyReadingProgress>>

    @Query("SELECT * FROM daily_progress ORDER BY dateKey DESC")
    suspend fun getAllDailyProgressList(): List<DailyReadingProgress>

    @Query("SELECT * FROM daily_progress WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyProgressForDate(dateKey: String): DailyReadingProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyProgress(progress: DailyReadingProgress)

    @Query("UPDATE daily_progress SET pagesRead = pagesRead + :pages WHERE dateKey = :dateKey")
    suspend fun incrementPagesRead(dateKey: String, pages: Int)
}
