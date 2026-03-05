package com.example.ramazontaqvim.utils

fun Long.toHms(): Triple<Long, Long, Long> {
    val s = this / 1000
    return Triple(s / 3600, (s % 3600) / 60, s % 60)
}

fun Triple<Long, Long, Long>.format() =
    "%02d:%02d:%02d".format(first, second, third)
