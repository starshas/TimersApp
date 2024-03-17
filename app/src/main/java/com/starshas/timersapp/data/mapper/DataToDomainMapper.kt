package com.starshas.timersapp.data.mapper

import com.starshas.timersapp.data.models.TimerEntity
import com.starshas.timersapp.domain.models.TimerFinishTime

fun TimerEntity.toDomain(): TimerFinishTime {
    return TimerFinishTime(
        id = id,
        finishTimeMillis = finishTimeMillis
    )
}

fun Long.finishTimeToEntity(): TimerEntity = TimerEntity(finishTimeMillis = this)
