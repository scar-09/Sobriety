package com.katiearose.sobriety.shared

import kotlinx.datetime.LocalDate
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AddictionSerializerTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(AddictionSerializer)

    private fun decode(raw: String): Addiction =
        json.decodeFromString(serializer, raw).single()

    private fun encode(addiction: Addiction): String =
        json.encodeToString(serializer, listOf(addiction))

    private val date = LocalDate(2026, 10, 18)

    @Test
    fun legacySingleStringNoteBecomesSingleItemList() {
        val legacy = """[{"name":"x","status":"Ongoing","history":{"1000":0},"priority":1,""" +
            """"daily_notes":{"2026-10-18":"2211"},"time_saving":"00:00","savings":{},"milestones":[]}]"""
        val addiction = decode(legacy)
        assertEquals(listOf("2211"), addiction.dailyNotes.getValue(date))
    }

    @Test
    fun currentArrayNotesArePreserved() {
        val current = """[{"name":"x","status":"Ongoing","history":{"1000":0},"priority":1,""" +
            """"daily_notes":{"2026-10-18":["abc","123"]},"time_saving":"00:00","savings":{},"milestones":[]}]"""
        val addiction = decode(current)
        assertEquals(listOf("abc", "123"), addiction.dailyNotes.getValue(date))
    }

    @Test
    fun roundTripPreservesMultipleNotesPerDay() {
        val current = """[{"name":"x","status":"Ongoing","history":{"1000":0},"priority":1,""" +
            """"daily_notes":{"2026-10-18":["abc","123"]},"time_saving":"00:00","savings":{},"milestones":[]}]"""
        val original = decode(current)
        val decoded = decode(encode(original))
        assertEquals(original.dailyNotes, decoded.dailyNotes)
        assertEquals(listOf("abc", "123"), decoded.dailyNotes.getValue(date))
    }

    @Test
    fun legacyNoteRoundTripsIntoNewFormat() {
        val legacy = """[{"name":"x","status":"Ongoing","history":{"1000":0},"priority":1,""" +
            """"daily_notes":{"2026-10-18":"2211"},"time_saving":"00:00","savings":{},"milestones":[]}]"""
        val decoded = decode(encode(decode(legacy)))
        assertEquals(listOf("2211"), decoded.dailyNotes.getValue(date))
    }
}
