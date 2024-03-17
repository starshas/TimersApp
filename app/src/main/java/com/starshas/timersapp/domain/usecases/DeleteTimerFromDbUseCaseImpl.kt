package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.data.repository.TimersDbRepository

class DeleteTimerFromDbUseCaseImpl(
    private val repository: TimersDbRepository
) : DeleteTimerFromCacheUseCase {
    override suspend operator fun invoke(id: Long) {
        repository.deleteById(id = id)
    }
}
