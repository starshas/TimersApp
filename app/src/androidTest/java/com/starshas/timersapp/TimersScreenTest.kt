package com.starshas.timersapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

class TimersScreenTest {
    private val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule
    val ruleChain: TestRule = RuleChain.outerRule(permissionRule).around(composeTestRule)

    @Test
    fun addTimerSuccessful() {
        val hours = "1"
        composeTestRule
            .onNodeWithText("hours")
            .performTextInput(hours)
        val minutes = "20"
        composeTestRule
            .onNodeWithText("minutes")
            .performTextInput(minutes)
        val seconds = "42"
        composeTestRule
            .onNodeWithText("seconds")
            .performTextInput(seconds)

        composeTestRule
            .onNodeWithText("Start!")
            .performClick()
        composeTestRule
            .onNodeWithText("0$hours:$minutes:$seconds")
            .assertExists("Timer should display the time that was input")
    }
}
