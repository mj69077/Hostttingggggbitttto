package com.example.data.local

import androidx.room.*
import com.example.data.model.DailyTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM daily_tasks WHERE dateKey = :dateKey ORDER BY id ASC")
    fun getTasksForDate(dateKey: String): Flow<List<DailyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DailyTask>)

    @Update
    suspend fun updateTask(task: DailyTask)

    @Query("UPDATE daily_tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Int, completed: Boolean)

    @Query("SELECT COUNT(*) FROM daily_tasks WHERE dateKey = :dateKey")
    suspend fun getTaskCountForDate(dateKey: String): Int
}
