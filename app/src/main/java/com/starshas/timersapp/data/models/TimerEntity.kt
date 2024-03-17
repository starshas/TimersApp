package com.starshas.timersapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.starshas.timersapp.common.constants.DbConstants
import com.starshas.timersapp.common.constants.DbConstants.TABLE_NAME_TIMERS_FINISH_TIME_COLUMN

@Entity(tableName = DbConstants.TABLE_NAME_TIMERS)
data class TimerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = TABLE_NAME_TIMERS_FINISH_TIME_COLUMN)
    val finishTimeMillis: Long
)
