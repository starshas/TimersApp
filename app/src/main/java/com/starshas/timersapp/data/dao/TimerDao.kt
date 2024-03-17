package com.starshas.timersapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.starshas.timersapp.common.constants.DbConstants.TABLE_NAME_TIMERS
import com.starshas.timersapp.common.constants.DbConstants.TABLE_NAME_TIMERS_COLUMN_ID
import com.starshas.timersapp.data.models.TimerEntity

@Dao
interface TimerDao {
    @Insert
    fun insertTimer(timer: TimerEntity): Long

    @Query("SELECT * FROM $TABLE_NAME_TIMERS")
    fun getAllTimers(): List<TimerEntity>

    @Query(
        "DELETE FROM $TABLE_NAME_TIMERS" +
                " WHERE $TABLE_NAME_TIMERS_COLUMN_ID = :id"
    )
    suspend fun deleteById(id: Long)
}
