package com.gabriel_miranda.currencyapp.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun calculateExchangeRate(source: Double, target: Double) : Double = target / source

fun convert(amount: Double, exchangeRate: Double) : Double = amount * exchangeRate

fun displayCurrentDateTime() : String {

    val currentTimeStamp = Clock.System.now()
    val date = currentTimeStamp.toLocalDateTime(TimeZone.currentSystemDefault())

    val dayOfMonth = date.dayOfMonth
    val month = date.month.toString().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    val year = date.year

    val suffix = when {
        dayOfMonth in 11..13 -> "th"
        dayOfMonth % 10 == 1 -> "st"
        dayOfMonth % 10 == 2 -> "nd"
        dayOfMonth % 10 == 3 -> "rd"
        else -> "th"
    }

    return "$dayOfMonth$suffix $month, $year."
}
