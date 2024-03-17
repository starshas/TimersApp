package com.starshas.timersapp.data.repository

import com.starshas.timersapp.data.dao.TimerDao
import com.starshas.timersapp.data.mapper.finishTimeToEntity
import com.starshas.timersapp.data.mapper.toDomain
import com.starshas.timersapp.data.models.TimerEntity
import com.starshas.timersapp.domain.models.TimerFinishTime
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TimerRepositoryImpl @Inject constructor(
    private val timerDao: TimerDao,
    private val coroutineContext: CoroutineContext
) : TimersDbRepository {
    override suspend fun addTimer(finishTime: Long) = withContext(coroutineContext) {
        timerDao.insertTimer(finishTime.finishTimeToEntity())
    }

    override suspend fun getAllTimers(): List<TimerFinishTime> = withContext(coroutineContext) {
        timerDao.getAllTimers().map { timerEntity: TimerEntity ->
            timerEntity.toDomain()
        }
    }

    override suspend fun deleteById(id: Long) = withContext(coroutineContext) {
        timerDao.deleteById(id)
    }
}
