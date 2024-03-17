package com.starshas.timersapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.starshas.timersapp.presentation.feature.timescreen.route.ROUTE_TIMERS_SCREEN
import com.starshas.timersapp.presentation.feature.timescreen.route.timersScreen
import com.starshas.timersapp.theme.TimersAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        setContent {
            val navController = rememberNavController()
            TimersAppTheme {
                NavHost(navController = navController, startDestination = ROUTE_TIMERS_SCREEN) {
                    timersScreen()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, permission)

            if (shouldShowRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(
                    /* context = */ this,
                    /* text = */ getString(R.string.toast_turn_on_notifications_in_settings),
                    /* duration = */ Toast.LENGTH_SHORT
                ).show()
            } else {
                if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                }
            }
        }
    }

    private fun shouldShowRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            /* activity = */ this,
            /* permission = */ permission
        )
    }
}
