package com.katiearose.sobriety.utils

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import com.katiearose.sobriety.R
import java.util.Locale

private const val SMALL_UNIT_SCALE = 0.6f

private data class HeroUnit(val value: Int, val label: String)

private fun visibleDateUnits(elapsed: HeroElapsed): List<HeroUnit> {
    val units = mutableListOf<HeroUnit>()
    if (elapsed.years > 0) {
        units += HeroUnit(elapsed.years, "y")
        units += HeroUnit(elapsed.months, "mo")
        units += HeroUnit(elapsed.days, "d")
    } else if (elapsed.months > 0) {
        units += HeroUnit(elapsed.months, "mo")
        units += HeroUnit(elapsed.days, "d")
    } else if (elapsed.days > 0) {
        units += HeroUnit(elapsed.days, "d")
    }
    return units
}

fun buildHeroText(elapsed: HeroElapsed): CharSequence {
    val builder = SpannableStringBuilder()
    for (unit in visibleDateUnits(elapsed)) {
        val start = builder.length
        builder.append(unit.value.toString()).append(unit.label).append(' ')
        builder.setSpan(
            RelativeSizeSpan(SMALL_UNIT_SCALE),
            start,
            builder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    builder.append(String.format(Locale.US, "%d:%02d", elapsed.hours, elapsed.minutes))
    val secondsStart = builder.length
    builder.append(String.format(Locale.US, "%02d", elapsed.seconds))
    builder.setSpan(
        SuperscriptSpan(),
        secondsStart,
        builder.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    builder.setSpan(
        RelativeSizeSpan(SMALL_UNIT_SCALE),
        secondsStart,
        builder.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return builder
}

private fun unitPhrase(context: Context, value: Int, pluralRes: Int): String =
    context.resources.getQuantityString(pluralRes, value, value).trimEnd(',', ' ')

fun buildHeroA11y(context: Context, elapsed: HeroElapsed): String {
    val phrases = mutableListOf<String>()
    if (elapsed.years > 0) phrases += unitPhrase(context, elapsed.years, R.plurals.years)
    if (elapsed.months > 0) phrases += unitPhrase(context, elapsed.months, R.plurals.months)
    if (elapsed.days > 0) phrases += unitPhrase(context, elapsed.days, R.plurals.days)
    if (elapsed.hours > 0) phrases += unitPhrase(context, elapsed.hours, R.plurals.hours)
    if (elapsed.minutes > 0) phrases += unitPhrase(context, elapsed.minutes, R.plurals.minutes)
    if (elapsed.seconds > 0) phrases += unitPhrase(context, elapsed.seconds, R.plurals.seconds)
    if (phrases.isEmpty()) phrases += unitPhrase(context, 0, R.plurals.seconds)
    val and = context.getString(R.string.and)
    return when (phrases.size) {
        1 -> phrases[0]
        2 -> "${phrases[0]} $and ${phrases[1]}"
        else -> phrases.dropLast(1).joinToString(", ") + ", $and " + phrases.last()
    }
}
