package com.starshas.timersapp.di

import android.content.Context
import androidx.room.Room
import com.starshas.timersapp.common.constants.DbConstants
import com.starshas.timersapp.data.AlarmScheduler
import com.starshas.timersapp.data.AlarmSchedulerImpl
import com.starshas.timersapp.data.dao.TimerDao
import com.starshas.timersapp.data.db.AppDatabase
import com.starshas.timersapp.data.repository.TimersDbRepository
import com.starshas.timersapp.data.repository.TimerRepositoryImpl
import com.starshas.timersapp.domain.usecases.DeleteTimerFromCacheUseCase
import com.starshas.timersapp.domain.usecases.DeleteTimerFromDbUseCaseImpl
import com.starshas.timersapp.domain.usecases.GetAllTimersUseCase
import com.starshas.timersapp.domain.usecases.GetAllTimersUseCaseImpl
import com.starshas.timersapp.domain.usecases.SaveTimerUseCase
import com.starshas.timersapp.domain.usecases.SaveTimerUseCaseImpl
import com.starshas.timersapp.domain.usecases.ScheduleTimerEndUseCase
import com.starshas.timersapp.domain.usecases.ScheduleTimerEndUseCaseImpl
import com.starshas.timersapp.domain.usecases.StartTimersManager
import com.starshas.timersapp.domain.usecases.StartTimersManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun provideStartTimerUseCase(
        useCaseDeleteTimer: DeleteTimerFromCacheUseCase
    ): StartTimersManager {
        return StartTimersManagerImpl(
            dispatcher = Dispatchers.IO,
            useCaseDeleteTimerFromCache = useCaseDeleteTimer
        )
    }

    @Singleton
    @Provides
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmSchedulerImpl(context)

    @Singleton
    @Provides
    fun provideScheduleTimerEndUseCase(alarmScheduler: AlarmScheduler): ScheduleTimerEndUseCase =
        ScheduleTimerEndUseCaseImpl(alarmScheduler)

    @Singleton
    @Provides
    fun provideSaveTimerUseCase(timerRepository: TimersDbRepository): SaveTimerUseCase =
        SaveTimerUseCaseImpl(timerRepository)

    @Singleton
    @Provides
    fun provideGetAllTimersUseCase(timerRepository: TimersDbRepository): GetAllTimersUseCase =
        GetAllTimersUseCaseImpl(timerRepository)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DbConstants.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTimerDao(appDatabase: AppDatabase): TimerDao {
        return appDatabase.timerDao()
    }

    @Provides
    fun provideTimersRepository(timerDao: TimerDao): TimersDbRepository {
        return TimerRepositoryImpl(timerDao, Dispatchers.IO)
    }

    @Provides
    fun provideDeleteTimerUseCase(repository: TimersDbRepository): DeleteTimerFromCacheUseCase {
        return DeleteTimerFromDbUseCaseImpl(repository)
    }
}
