package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.QuranIndexItem
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranIndexDao {

    @Query("SELECT * FROM quran_indexes ORDER BY type ASC, itemNumber ASC")
    fun getAllIndices(): Flow<List<QuranIndexItem>>

    @Query("SELECT * FROM quran_indexes WHERE type = :type ORDER BY itemNumber ASC")
    fun getIndicesByType(type: String): Flow<List<QuranIndexItem>>

    @Query("SELECT * FROM quran_indexes WHERE type = :type AND topicGroup = :topicGroup ORDER BY itemNumber ASC")
    fun getIndicesByTopic(type: String, topicGroup: String): Flow<List<QuranIndexItem>>

    @Query("""
        SELECT * FROM quran_indexes 
        WHERE titleArabic LIKE '%' || :query || '%' 
           OR subtitleArabic LIKE '%' || :query || '%'
           OR startSurahName LIKE '%' || :query || '%'
           OR topicGroup LIKE '%' || :query || '%'
           OR keywords LIKE '%' || :query || '%'
        ORDER BY itemNumber ASC
    """)
    suspend fun searchIndices(query: String): List<QuranIndexItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuranIndexItem>)

    @Query("SELECT COUNT(*) FROM quran_indexes")
    suspend fun getCount(): Int
}
