package com.starshas.timersapp.presentation.feature.timescreen.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.starshas.timersapp.presentation.feature.timescreen.TimersScreen

const val ROUTE_TIMERS_SCREEN = "ROUTE_TIMERS_SCREEN"

fun NavController.navigateToTimersScreen(navOptions: NavOptions? = null) {
    navigate(ROUTE_TIMERS_SCREEN, navOptions)
}

fun NavGraphBuilder.timersScreen() {
    composable(route = ROUTE_TIMERS_SCREEN) {
        TimersScreen()
    }
}
