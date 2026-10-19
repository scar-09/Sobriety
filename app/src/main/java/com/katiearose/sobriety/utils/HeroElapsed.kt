package com.katiearose.sobriety.utils

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId

data class HeroElapsed(
    val years: Int,
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)

fun decomposeElapsed(startMillis: Long, endMillis: Long, zone: ZoneId = ZoneId.systemDefault()): HeroElapsed {
    val startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zone)
    val endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endMillis), zone)
    val duration = Duration.between(startDate, endDate)
    val period = Period.between(
        endDate.minusDays(duration.toDaysPart()).toLocalDate(),
        endDate.toLocalDate()
    )
    return HeroElapsed(
        years = period.years,
        months = period.months,
        days = period.days,
        hours = duration.toHoursPart(),
        minutes = duration.toMinutesPart(),
        seconds = duration.toSecondsPart()
    )
}
