// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.hiltPlugin) version "2.49" apply false
    alias(libs.plugins.roomPlugin) version "2.6.1" apply false
}
