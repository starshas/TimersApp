package com.starshas.timersapp.presentation.feature.timescreen.actions

data class ScreenTimersActions(
    val onHoursChange: (String) -> Unit,
    val onMinutesChange: (String) -> Unit,
    val onSecondsChange: (String) -> Unit,
    val onStartTimerClick: () -> Unit,
)
