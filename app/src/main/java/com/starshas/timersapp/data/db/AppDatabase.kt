package com.starshas.timersapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.starshas.timersapp.data.dao.TimerDao
import com.starshas.timersapp.data.models.TimerEntity

@Database(entities = [TimerEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
}
