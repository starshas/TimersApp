package com.starshas.timersapp.common.extensions

fun Int?.toStringOrNull(): String? = this?.toString()
fun String?.toIntOrZero(): Int = this?.toIntOrNull() ?: 0
val Int?.intOrZero get() = this ?: 0
