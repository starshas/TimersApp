package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.decrementedOneSecond
import com.starshas.timersapp.common.utils.TimerTimeUtils.isFinished
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StartTimersManagerImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val useCaseDeleteTimerFromCache: DeleteTimerFromCacheUseCase
) : StartTimersManager {
    private val _timers: MutableStateFlow<List<TimerTime>> = MutableStateFlow(emptyList())
    private var isCountDownActive = false

    override fun getTimersFlow(): StateFlow<List<TimerTime>> {
        return _timers
    }

    override fun addTimer(initialTime: TimerTime) {
        // Excluding zero timers (00:00:00)
        if (!initialTime.isFinished()) {
            _timers.update {
                listOf(initialTime) + it
            }
        }
    }

    override fun removeAllTimers() {
        _timers.update { emptyList() }
    }

    override suspend fun startTimers() {
        if (!isCountDownActive) {
            withContext(dispatcher) {
                isCountDownActive = true
                try {
                    while (_timers.value.isNotEmpty() && isActive) {
                        delay(TIMER_UPDATE_INTERVAL_MILLIS)
                        _timers.update { currentState: List<TimerTime> ->
                            currentState.mapNotNull {
                                // Decrementing the timer by 1 second and removing finished timers
                                return@mapNotNull if (it.isFinished()) {
                                    useCaseDeleteTimerFromCache(it.id)
                                    null
                                } else {
                                    it.decrementedOneSecond()
                                }
                            }
                        }
                    }
                } finally {
                    isCountDownActive = false
                }
            }
        }
    }

    companion object {
        const val TIMER_UPDATE_INTERVAL_MILLIS: Long = 1000L
    }
}
