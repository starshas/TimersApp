package com.starshas.timersapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils
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
import kotlinx.coroutines.runBlocking
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

class RepositoryTests {
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
    fun `repository addTimer saves data correctly`() = runTest {
        val timerFinishTime = System.currentTimeMillis() + 3600000
        timerDbRepository.addTimer(timerFinishTime)

        val allTimers = timerDbRepository.getAllTimers()
        Assert.assertEquals(1, allTimers.size)
        Assert.assertEquals(timerFinishTime, allTimers.first().finishTimeMillis)
    }

    @Test
    fun `repository getAllTimers returns correct data`() = runTest {
        val timeOfTimersToAdd = listOf(
            System.currentTimeMillis() + 3600000,
            System.currentTimeMillis() + 7200000
        )
        for (item in timeOfTimersToAdd) {
            timerDbRepository.addTimer(item)
        }

        val allTimers = timerDbRepository.getAllTimers()
        Assert.assertEquals(2, allTimers.size)
        assert(allTimers.map { it.finishTimeMillis }.containsAll(timeOfTimersToAdd))
    }

    @Test
    fun `repository deleteById removes correct timer`() = runBlocking {
        val id = timerDbRepository
            .addTimer(System.currentTimeMillis() + 3600000)
        val id2 = timerDbRepository
            .addTimer(System.currentTimeMillis() + 7200000)

        timerDao.deleteById(id)

        val allTimers = timerDbRepository.getAllTimers()
        Assert.assertEquals(1, allTimers.size)
        Assert.assertEquals(id2, allTimers.first().id)
    }

    @Test
    fun `repository test SaveTimerUseCase success`() = runTest {
        val timerTime = TimerTime(hours = 1)
        val currentTimeMillis = System.currentTimeMillis()
        val finishTimeMillis = currentTimeMillis + timerTime.toTotalMillis()
        useCaseSaveTimer(
            timerTime = timerTime,
            currentTimeMillis = currentTimeMillis
        )

        val currentTimeMillis2 = System.currentTimeMillis()
        val allTimers: List<TimerTime> = useCaseGetAllTimers(
            currentTimeMillis2
        )
        Assert.assertEquals(1, allTimers.size)

        val expectedTime = finishTimeMillis - currentTimeMillis2
        Assert.assertTrue(
            allTimers.first() == TimerTimeUtils.timerTimeFromTotalMillis(
                id = allTimers.first().id,
                totalMillis = expectedTime
            )
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    inner class TestDispatcherRule(
        val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ): TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
