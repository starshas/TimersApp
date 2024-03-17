package com.starshas.timersapp.presentation.feature.timescreen

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starshas.timersapp.R
import com.starshas.timersapp.common.extensions.toStringOrNull
import com.starshas.timersapp.presentation.feature.timescreen.actions.ScreenTimersActions
import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.format
import com.starshas.timersapp.presentation.feature.timescreen.models.TimersScreenState
import com.starshas.timersapp.presentation.feature.timescreen.viewmodel.TimersViewModel

@Composable
fun TimersScreen() {
    val viewModel: TimersViewModel = hiltViewModel()
    val state: TimersScreenState by viewModel.state.collectAsStateWithLifecycle()

    LifecycleEventHandler(
        onResume = viewModel::restoreTimersRequested,
    )

    Content(
        state = state,
        screenTimersActions = ScreenTimersActions(
            onHoursChange = viewModel::onHoursFieldChange,
            onMinutesChange = viewModel::onMinutesFieldChange,
            onSecondsChange = viewModel::onSecondsFieldChange,
            onStartTimerClick = viewModel::onStartTimerClick
        )
    )
}

@Composable
fun LifecycleEventHandler(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun Content(
    state: TimersScreenState,
    screenTimersActions: ScreenTimersActions
) {
    val listTimers = state.list
    val time = state.time

    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            SetTimerRow(
                time = time,
                actions = screenTimersActions
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.timers_screen_running_timers))
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(listTimers) { timer ->
                    Divider()
                    Timer(timer)
                }
            }

        }
    }
}

@Composable
private fun SetTimerRow(
    time: TimerTime,
    actions: ScreenTimersActions
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = spacedBy(8.dp)) {
        val weight = 1f
        TimeInput(
            text = time.hours.toStringOrNull() ?: "",
            placeholderText = stringResource(R.string.timers_screen_hours),
            modifier = Modifier.weight(weight),
            onValueChange = actions.onHoursChange
        )
        TimeInput(
            text = time.minutes.toStringOrNull() ?: "",
            placeholderText = stringResource(R.string.timers_screen_minutes),
            modifier = Modifier.weight(weight),
            onValueChange = actions.onMinutesChange
        )
        TimeInput(
            text = time.seconds.toStringOrNull() ?: "",
            placeholderText = stringResource(R.string.timers_screen_seconds),
            modifier = Modifier.weight(weight),
            onValueChange = actions.onSecondsChange
        )
        Button(
            onClick = actions.onStartTimerClick,
            modifier = Modifier.weight(weight)
        ) {
            Text(
                text = stringResource(R.string.timers_screen_button_start)
            )
        }
    }
}

@Composable
private fun TimeInput(
    modifier: Modifier = Modifier,
    text: String,
    placeholderText: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = {
            Text(
                text = placeholderText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = 10.sp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun Timer(value: TimerTime) {
    Text(
        text = value.format(),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Preview
@Composable
private fun PreviewTimeInput() {
    TimeInput(
        text = "",
        placeholderText = stringResource(id = R.string.timers_screen_seconds),
        onValueChange = {}
    )
}

@Preview
@Composable
private fun PreviewTimers() {
    Content(
        TimersScreenState(
            time = TimerTime(),
            list = listOf(
                TimerTime(
                    hours = 3,
                    minutes = 23,
                    seconds = 14
                ),
                TimerTime(
                    hours = 0,
                    minutes = 0,
                    seconds = 11
                )
            )
        ),
        ScreenTimersActions(
            onHoursChange = {},
            onMinutesChange = {},
            onSecondsChange = {},
            onStartTimerClick = {}
        )
    )
}

@Preview
@Composable
private fun PreviewTimersEmpty() {
    Content(
        state = TimersScreenState(
            list = emptyList()
        ),
        ScreenTimersActions(
            onHoursChange = {},
            onMinutesChange = {},
            onSecondsChange = {},
            onStartTimerClick = {}
        )
    )
}


@Preview
@Composable
private fun PreviewTimer() {
    Timer(
        TimerTime(
            hours = 3,
            minutes = 23,
            seconds = 14
        )
    )
}

@Preview
@Composable
private fun PreviewNewTimer() {
    SetTimerRow(
        time = TimerTime(),
        ScreenTimersActions(
            onHoursChange = {},
            onMinutesChange = {},
            onSecondsChange = {},
            onStartTimerClick = {}
        )
    )
}
