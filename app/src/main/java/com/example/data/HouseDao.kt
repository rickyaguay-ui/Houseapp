package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.data.StickyNoteEntity

@Dao
interface HouseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBurden(burden: BurdenEntity)

    @Query("SELECT * FROM burdens ORDER BY timestamp DESC")
    fun getAllBurdens(): Flow<List<BurdenEntity>>

    @Query("DELETE FROM burdens WHERE id = :id")
    suspend fun deleteBurden(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoap(soap: SoapEntity)

    @Query("SELECT * FROM soap_reflections ORDER BY timestamp DESC")
    fun getAllSoapReflections(): Flow<List<SoapEntity>>

    @Query("DELETE FROM soap_reflections WHERE id = :id")
    suspend fun deleteSoap(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyPrayer(dailyPrayer: DailyPrayerEntity)

    @Query("SELECT * FROM daily_prayers ORDER BY timestamp DESC")
    fun getAllDailyPrayers(): Flow<List<DailyPrayerEntity>>

    @Query("DELETE FROM daily_prayers WHERE id = :id")
    suspend fun deleteDailyPrayer(id: Long)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStickyNote(stickyNote: StickyNoteEntity)

    @Query("SELECT * FROM sticky_notes ORDER BY timestamp DESC")
    fun getAllStickyNotes(): Flow<List<StickyNoteEntity>>

    @Query("DELETE FROM sticky_notes WHERE id = :id")
    suspend fun deleteStickyNote(id: Long)
}
