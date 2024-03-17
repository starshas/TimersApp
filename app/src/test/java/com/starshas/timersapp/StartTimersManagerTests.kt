package com.starshas.timersapp

import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.toTotalMillis
import com.starshas.timersapp.domain.usecases.DeleteTimerFromCacheUseCase
import com.starshas.timersapp.domain.usecases.StartTimersManagerImpl
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class StartTimersManagerTests {
    private lateinit var startTimersManager: StartTimersManagerImpl
    private val useCaseDeleteTimerFromCache: DeleteTimerFromCacheUseCase = mockk(relaxed = true)

    @get:Rule
    var testCoroutineRule = TestDispatcherRule()

    @Before
    fun setUp() {
        startTimersManager =
            StartTimersManagerImpl(testCoroutineRule.dispatcher, useCaseDeleteTimerFromCache)
    }

    @Test
    fun `addTimer adds non-zero timers`() = runTest() {
        val timer = TimerTime(hours = 1)
        startTimersManager.addTimer(timer)
        assertEquals(1, startTimersManager.getTimersFlow().value.size)
    }

    @Test
    fun `addTimer ignores zero or empty timers`() = runTest {
        val timer1 = TimerTime()
        val timer2 = TimerTime(hours = 0, minutes = 0, seconds = 0)
        startTimersManager.addTimer(timer1)
        startTimersManager.addTimer(timer2)
        assertTrue(startTimersManager.getTimersFlow().value.isEmpty())
    }

    @Test
    fun `removeAllTimers clears all timers`() = runTest {
        startTimersManager.addTimer(TimerTime(minutes = 1))
        startTimersManager.addTimer(TimerTime(minutes = 2))
        startTimersManager.removeAllTimers()
        assertTrue(startTimersManager.getTimersFlow().value.isEmpty())
    }

    @Test
    fun `startTimers decrements timers correctly`() = runTest {
        val initialTimer = TimerTime(hours = 1)
        startTimersManager.addTimer(initialTimer)

        launch {
            startTimersManager.startTimers()
        }
        advanceTimeBy(StartTimersManagerImpl.TIMER_UPDATE_INTERVAL_MILLIS)

        val firstChange: TimerTime? = withTimeoutOrNull(5000) { // Timeout after 5000 milliseconds
            startTimersManager.getTimersFlow()
                .first { it.isNotEmpty() && it.first() != initialTimer }
        }?.first()
        firstChange ?: fail("Timer was not decremented within the expected time")

        val expectedTime = TimerTime(hours = 0, minutes = 59, seconds = 59)
        assertEquals(expectedTime, firstChange)
    }

    @Test
    fun `startTimers removes finished timers`() = runTest {
        val finishedTimer = TimerTime(seconds = 5)
        startTimersManager.addTimer(finishedTimer)

        launch {
            startTimersManager.startTimers()
        }
        advanceTimeBy(finishedTimer.toTotalMillis())

        val isTimersEmpty =
            withTimeoutOrNull(
                StartTimersManagerImpl.TIMER_UPDATE_INTERVAL_MILLIS + 1
            ) {
                startTimersManager.getTimersFlow().first { it.isEmpty() }
                true
            } ?: false

        assertTrue("Timers list did not become empty as expected.", isTimersEmpty)
        coVerify(exactly = 1) { useCaseDeleteTimerFromCache(finishedTimer.id) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    inner class TestDispatcherRule(
        val dispatcher: TestDispatcher = StandardTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
