package com.starshas.timersapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.timerTimeFromTotalMillis
import com.starshas.timersapp.common.utils.TimerTimeUtils.toTotalMillis
import com.starshas.timersapp.data.dao.TimerDao
import com.starshas.timersapp.data.db.AppDatabase
import com.starshas.timersapp.data.repository.TimerRepositoryImpl
import com.starshas.timersapp.data.repository.TimersDbRepository
import com.starshas.timersapp.domain.usecases.DeleteTimerFromCacheUseCase
import com.starshas.timersapp.domain.usecases.DeleteTimerFromDbUseCaseImpl
import com.starshas.timersapp.domain.usecases.GetAllTimersUseCase
import com.starshas.timersapp.domain.usecases.GetAllTimersUseCaseImpl
import com.starshas.timersapp.domain.usecases.SaveTimerUseCase
import com.starshas.timersapp.domain.usecases.SaveTimerUseCaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class UseCasesTests {
    private lateinit var database: AppDatabase
    private lateinit var timerDao: TimerDao
    private lateinit var timerDbRepository: TimersDbRepository
    private lateinit var useCaseDeleteTimer: DeleteTimerFromCacheUseCase
    private lateinit var useCaseGetAllTimers: GetAllTimersUseCase
    private lateinit var useCaseSaveTimer: SaveTimerUseCase

    @get:Rule
    var testCoroutineRule = TestDispatcherRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        timerDao = database.timerDao()
        timerDbRepository = TimerRepositoryImpl(timerDao, testCoroutineRule.dispatcher)
        useCaseDeleteTimer = DeleteTimerFromDbUseCaseImpl(timerDbRepository)
        useCaseGetAllTimers = GetAllTimersUseCaseImpl(timerDbRepository)
        useCaseSaveTimer = SaveTimerUseCaseImpl(timerDbRepository)
    }

    @Test
    fun getAllTimersUseCaseSuccess() = runTest {
        val currentTimeMillis = System.currentTimeMillis()
        val timerTime1 = TimerTime(hours = 1, minutes = 2, seconds = 3)
        val timerTime2 = TimerTime(hours = 4, minutes = 5, seconds = 6)
        val timerTime3 = TimerTime(hours = 7, minutes = 8, seconds = 9)
        val finishTimeMillis1 = currentTimeMillis + timerTime1.toTotalMillis()
        val finishTimeMillis2 = currentTimeMillis + timerTime2.toTotalMillis()
        val finishTimeMillis3 = currentTimeMillis + timerTime3.toTotalMillis()

        useCaseSaveTimer(
            timerTime = timerTime1,
            currentTimeMillis = currentTimeMillis
        )
        useCaseSaveTimer(
            timerTime = TimerTime(hours = 4, minutes = 5, seconds = 6),
            currentTimeMillis = currentTimeMillis
        )
        useCaseSaveTimer(
            timerTime = TimerTime(hours = 7, minutes = 8, seconds = 9),
            currentTimeMillis = currentTimeMillis
        )

        val currentTimeMillis2 = System.currentTimeMillis()
        val allTimers = useCaseGetAllTimers(currentTimeMillis2)
        Assert.assertEquals(allTimers.size, 3)
        val savedTimerTime1 = allTimers[0]
        val savedTimerTime2 = allTimers[1]
        val savedTimerTime3 = allTimers[2]
        Assert.assertEquals(savedTimerTime1.id, 1)
        Assert.assertEquals(savedTimerTime2.id, 2)
        Assert.assertEquals(savedTimerTime3.id, 3)
        Assert.assertTrue(
            savedTimerTime1.let {
                val expectedTime1 = finishTimeMillis1 - currentTimeMillis2
                return@let it == timerTimeFromTotalMillis(
                    id = it.id,
                    totalMillis = expectedTime1
                )
            }
        )
        Assert.assertTrue(
            savedTimerTime2.let {
                val expectedTime2 = finishTimeMillis2 - currentTimeMillis2
                return@let it == timerTimeFromTotalMillis(
                    id = it.id,
                    totalMillis = expectedTime2
                )
            }
        )
        Assert.assertTrue(
            savedTimerTime3.let {
                val expectedTime3 = finishTimeMillis3 - currentTimeMillis2
                return@let it == timerTimeFromTotalMillis(
                    id = it.id,
                    totalMillis = expectedTime3
                )
            }
        )
    }

    @Test
    fun deleteTimersUseCaseSuccess() = runTest {
        val timerTime = TimerTime(minutes = 1)
        val timerTime2 = TimerTime(seconds = 2)
        val timerTime3 = TimerTime(seconds = 3)

        val currentTimeMillis = System.currentTimeMillis()
        useCaseSaveTimer(
            timerTime = timerTime,
            currentTimeMillis = currentTimeMillis
        )
        useCaseSaveTimer(
            timerTime = timerTime2,
            currentTimeMillis = currentTimeMillis
        )
        useCaseSaveTimer(
            timerTime = timerTime3,
            currentTimeMillis = currentTimeMillis
        )

        val allTimersBeforeDeletion = useCaseGetAllTimers(System.currentTimeMillis())
        val timerToDelete = allTimersBeforeDeletion.first()
        useCaseDeleteTimer(timerToDelete.id)

        val allTimersAfterDeletion = useCaseGetAllTimers(System.currentTimeMillis())
        Assert.assertTrue(allTimersAfterDeletion.size == 2)
        Assert.assertTrue(allTimersAfterDeletion.any { it.id != timerToDelete.id })
    }

    @After
    fun tearDown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    inner class TestDispatcherRule(
        val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
