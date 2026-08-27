package com.example.data.local

import androidx.room.*
import com.example.data.model.Fatwa
import kotlinx.coroutines.flow.Flow

@Dao
interface FatwaDao {
    @Query("SELECT * FROM fatwas ORDER BY id ASC")
    fun getAllFatwas(): Flow<List<Fatwa>>

    @Query("SELECT * FROM fatwas WHERE category = :category ORDER BY id ASC")
    fun getFatwasByCategory(category: String): Flow<List<Fatwa>>

    @Query("SELECT * FROM fatwas WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteFatwas(): Flow<List<Fatwa>>

    @Query("SELECT * FROM fatwas WHERE title LIKE '%' || :query || '%' OR question LIKE '%' || :query || '%' OR answer LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%'")
    fun searchFatwas(query: String): Flow<List<Fatwa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fatwas: List<Fatwa>)

    @Update
    suspend fun update(fatwa: Fatwa)

    @Query("SELECT COUNT(*) FROM fatwas")
    suspend fun getCount(): Int
}
