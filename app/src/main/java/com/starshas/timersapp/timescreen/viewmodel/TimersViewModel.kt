package com.starshas.timersapp.presentation.feature.timescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starshas.timersapp.common.constants.TimeUtils.HOURS_IN_DAY
import com.starshas.timersapp.common.constants.TimeUtils.MAX_MINUTE_IN_HOUR
import com.starshas.timersapp.common.constants.TimeUtils.MAX_SECOND_IN_MINUTE
import com.starshas.timersapp.common.constants.TimeUtils.MIN_HOUR_IN_DAY
import com.starshas.timersapp.common.constants.TimeUtils.MIN_MINUTE_IN_HOUR
import com.starshas.timersapp.common.constants.TimeUtils.MIN_SECOND_IN_MINUTE
import com.starshas.timersapp.domain.usecases.ScheduleTimerEndUseCase
import com.starshas.timersapp.domain.usecases.StartTimersManager
import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.extensions.toIntOrZero
import com.starshas.timersapp.common.utils.TimerTimeUtils.isFinished
import com.starshas.timersapp.domain.usecases.DeleteTimerFromCacheUseCase
import com.starshas.timersapp.domain.usecases.GetAllTimersUseCase
import com.starshas.timersapp.domain.usecases.SaveTimerUseCase
import com.starshas.timersapp.presentation.feature.timescreen.models.TimersScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimersViewModel @Inject constructor(
    private val useCaseStartTimers: StartTimersManager,
    private val useCaseScheduleTimerEnd: ScheduleTimerEndUseCase,
    private val useCaseSaveTimer: SaveTimerUseCase,
    private val useCaseGetAllTimers: GetAllTimersUseCase,
    private val useCaseDeleteTimerFromCache: DeleteTimerFromCacheUseCase
) : ViewModel() {
    private val inputTime: MutableStateFlow<TimerTime> = MutableStateFlow(TimerTime())
    val state: StateFlow<TimersScreenState> =
        combine(inputTime, useCaseStartTimers.getTimersFlow()) { time, list ->
            TimersScreenState(list = list, time = time)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STATEFLOW_STOP_TIMEOUT_MILLIS),
            initialValue = TimersScreenState()
        )

    fun onHoursFieldChange(value: String) {
        if ((value.toIntOrZero()) in MIN_HOUR_IN_DAY..HOURS_IN_DAY) {
            inputTime.update { it.copy(hours = value.toIntOrNull()) }
        }
    }

    fun onMinutesFieldChange(value: String) {
        if ((value.toIntOrZero()) in MIN_MINUTE_IN_HOUR..MAX_MINUTE_IN_HOUR) {
            inputTime.update { it.copy(minutes = value.toIntOrNull()) }
        }
    }

    fun onSecondsFieldChange(value: String) {
        if ((value.toIntOrZero()) in MIN_SECOND_IN_MINUTE..MAX_SECOND_IN_MINUTE) {
            inputTime.update { it.copy(seconds = value.toIntOrNull()) }
        }
    }

    fun onStartTimerClick() {
        val timer = inputTime.value
        addNewTimerToListAndStart()
        useCaseScheduleTimerEnd(timer = timer)
        saveTimerToCache(timer)
        clearTextFields()
    }

    private fun clearTextFields() {
        inputTime.update { TimerTime() }
    }

    private fun addNewTimerToListAndStart(timerTime: TimerTime = inputTime.value) {
        useCaseStartTimers.addTimer(timerTime)
        viewModelScope.launch {
            useCaseStartTimers.startTimers()
        }
    }

    private fun saveTimerToCache(timerTime: TimerTime) {
        viewModelScope.launch {
            useCaseSaveTimer(
                timerTime = timerTime,
                currentTimeMillis = System.currentTimeMillis()
            )
        }
    }

    private fun removeAllTimers() {
        useCaseStartTimers.removeAllTimers()
    }

    fun restoreTimersRequested() {
        viewModelScope.launch {
            removeAllTimers()
            val timersFromDb = useCaseGetAllTimers(currentTimeMillis = System.currentTimeMillis())
            timersFromDb.forEach { timerTime ->
                if (!timerTime.isFinished()) {
                    addNewTimerToListAndStart(timerTime)
                    useCaseScheduleTimerEnd(timerTime)
                } else {
                    useCaseDeleteTimerFromCache(timerTime.id)
                }
            }
        }
    }

    private companion object {
        const val STATEFLOW_STOP_TIMEOUT_MILLIS: Long = 5000L
    }
}
