package com.katiearose.sobriety.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class HeroElapsedTest {

    private val utc = ZoneId.of("UTC")

    private fun millis(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0, second: Int = 0): Long =
        LocalDateTime.of(year, month, day, hour, minute, second).atZone(utc).toInstant().toEpochMilli()

    @Test
    fun zeroSeconds() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 0, 0, 0, 0), decomposeElapsed(start, start, utc))
    }

    @Test
    fun fiftyNineSeconds() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 0, 0, 0, 59), decomposeElapsed(start, start + 59_000L, utc))
    }

    @Test
    fun oneMinute() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 0, 0, 1, 0), decomposeElapsed(start, start + 60_000L, utc))
    }

    @Test
    fun fiftyNineMinutesFiftyNineSeconds() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 0, 0, 59, 59), decomposeElapsed(start, start + 3_599_000L, utc))
    }

    @Test
    fun exactlyOneDay() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 1, 0, 0, 0), decomposeElapsed(start, millis(2025, 1, 16), utc))
    }

    @Test
    fun oneDayZeroHoursFiveMinutes() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 1, 0, 5, 0), decomposeElapsed(start, millis(2025, 1, 16, 12, 5), utc))
    }

    @Test
    fun thirtyDaysAcrossMonthBoundary() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 0, 30, 0, 0, 0), decomposeElapsed(start, millis(2025, 2, 14), utc))
    }

    @Test
    fun thirtyOneDaysSameDayOfMonthBecomesOneMonth() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(0, 1, 0, 0, 0, 0), decomposeElapsed(start, millis(2025, 2, 15), utc))
    }

    @Test
    fun exactlyOneMonth() {
        val start = millis(2025, 3, 10)
        assertEquals(HeroElapsed(0, 1, 0, 0, 0, 0), decomposeElapsed(start, millis(2025, 4, 10), utc))
    }

    @Test
    fun exactlyOneYear() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(1, 0, 0, 0, 0, 0), decomposeElapsed(start, millis(2026, 1, 15), utc))
    }

    @Test
    fun oneYearZeroMonthsFiveDays() {
        val start = millis(2025, 1, 15)
        assertEquals(HeroElapsed(1, 0, 5, 0, 0, 0), decomposeElapsed(start, millis(2026, 1, 20), utc))
    }

    @Test
    fun veryLongDuration() {
        val start = millis(2023, 1, 15)
        val end = millis(2026, 3, 20) + 86_399_000L
        assertEquals(HeroElapsed(3, 2, 5, 23, 59, 59), decomposeElapsed(start, end, utc))
    }
}
