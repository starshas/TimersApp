package com.starshas.timersapp.domain.usecases

interface DeleteTimerFromCacheUseCase {
    suspend operator fun invoke(id: Long)
}
