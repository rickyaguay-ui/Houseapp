package com.example.data

import kotlinx.coroutines.flow.Flow
import com.example.data.StickyNoteEntity

class HouseRepository(private val houseDao: HouseDao) {
    val allBurdens: Flow<List<BurdenEntity>> = houseDao.getAllBurdens()
    val allSoapReflections: Flow<List<SoapEntity>> = houseDao.getAllSoapReflections()
    val allDailyPrayers: Flow<List<DailyPrayerEntity>> = houseDao.getAllDailyPrayers()
    val allStickyNotes: Flow<List<StickyNoteEntity>> = houseDao.getAllStickyNotes()

    suspend fun insertBurden(burden: BurdenEntity) {
        houseDao.insertBurden(burden)
    }

    suspend fun deleteBurden(id: Long) {
        houseDao.deleteBurden(id)
    }

    suspend fun insertSoap(soap: SoapEntity) {
        houseDao.insertSoap(soap)
    }

    suspend fun deleteSoap(id: Long) {
        houseDao.deleteSoap(id)
    }

    suspend fun insertDailyPrayer(dailyPrayer: DailyPrayerEntity) {
        houseDao.insertDailyPrayer(dailyPrayer)
    }

    suspend fun deleteDailyPrayer(id: Long) {
        houseDao.deleteDailyPrayer(id)
    }

    suspend fun insertStickyNote(stickyNote: StickyNoteEntity) {
        houseDao.insertStickyNote(stickyNote)
    }

    suspend fun deleteStickyNote(id: Long) {
        houseDao.deleteStickyNote(id)
    }
}
