package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Ayah
import com.example.data.model.Surah
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<Surah>>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumber(number: Int): Surah?

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getVersesForSurah(surahNumber: Int): Flow<List<Ayah>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getVersesListForSurah(surahNumber: Int): List<Ayah>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah?

    // Fast text search across Quran verses (supporting normalized plain text and uthmani)
    @Query("""
        SELECT * FROM verses 
        WHERE textPlain LIKE '%' || :query || '%' 
           OR textUthmani LIKE '%' || :query || '%'
        ORDER BY surahNumber ASC, ayahNumber ASC
        LIMIT 50
    """)
    suspend fun searchVerses(query: String): List<Ayah>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<Surah>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<Ayah>)

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahsCount(): Int

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVersesCount(): Int
}
