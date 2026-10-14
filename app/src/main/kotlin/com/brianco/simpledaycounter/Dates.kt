package com.brianco.simpledaycounter

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

internal fun daysSince(
  dateMidnightUtcMillis: Long,
  nowMillis: Long,
  currentZoneId: ZoneId
): Long {
  requireMidnightUtcDate(dateMidnightUtcMillis)
  val selectedDate = Instant.ofEpochMilli(dateMidnightUtcMillis)
    .atZone(ZoneOffset.UTC)
    .toLocalDate()
  val today = Instant.ofEpochMilli(nowMillis)
    .atZone(currentZoneId)
    .toLocalDate()
  return ChronoUnit.DAYS.between(selectedDate, today)
}

// month is 0-indexed. Android's DatePicker 0-indexes the months.
internal fun dateMidnightUtcMillis(dayOfMonth: Int, month: Int, year: Int): Long {
  return LocalDate.of(year, month + 1, dayOfMonth)
    .atStartOfDay(ZoneOffset.UTC)
    .toInstant()
    .toEpochMilli()
}

// month is 0-indexed. Android's DatePicker 0-indexes the months.
internal inline fun dayMonthYear(
  dateMidnightUtcMillis: Long,
  result: (dayOfMonth: Int, month: Int, year: Int) -> Unit,
) {
  requireMidnightUtcDate(dateMidnightUtcMillis)
  val date = Instant.ofEpochMilli(dateMidnightUtcMillis)
    .atZone(ZoneOffset.UTC)
    .toLocalDate()
  return result(
    date.dayOfMonth,
    date.monthValue - 1,
    date.year
  )
}

internal fun requireMidnightUtcDate(date: Long) {
  require(isMidnightUtcDate(date)) {
    "$date was not a midnight UTC date."
  }
}

internal fun isMidnightUtcDate(date: Long): Boolean {
  return date % 86_400_000L == 0L
}
