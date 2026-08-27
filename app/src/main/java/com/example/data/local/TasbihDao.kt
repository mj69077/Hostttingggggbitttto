package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TasbihRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {

    @Query("SELECT * FROM tasbih_records ORDER BY timestamp DESC LIMIT 100")
    fun getAllTasbihRecords(): Flow<List<TasbihRecord>>

    @Query("SELECT * FROM tasbih_records ORDER BY timestamp DESC LIMIT 100")
    suspend fun getTasbihRecordsList(): List<TasbihRecord>

    @Query("SELECT SUM(count) FROM tasbih_records WHERE dayKey = :dayKey")
    fun getTodayTasbihSum(dayKey: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbihRecord(record: TasbihRecord)

    @Query("DELETE FROM tasbih_records")
    suspend fun clearAll()
}
