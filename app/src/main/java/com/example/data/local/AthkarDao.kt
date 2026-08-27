package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AthkarItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AthkarDao {

    @Query("SELECT * FROM athkar_items WHERE categoryId = :categoryId ORDER BY id ASC")
    fun getAthkarByCategory(categoryId: String): Flow<List<AthkarItem>>

    @Query("SELECT * FROM athkar_items WHERE categoryId = :categoryId ORDER BY id ASC")
    suspend fun getAthkarListByCategory(categoryId: String): List<AthkarItem>

    @Query("UPDATE athkar_items SET currentCount = :count, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateProgress(id: Int, count: Int, isCompleted: Boolean)

    @Query("UPDATE athkar_items SET currentCount = 0, isCompleted = 0 WHERE categoryId = :categoryId")
    suspend fun resetCategory(categoryId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthkar(items: List<AthkarItem>)

    @Query("SELECT COUNT(*) FROM athkar_items")
    suspend fun getAthkarCount(): Int
}
