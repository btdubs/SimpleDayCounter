package com.brianco.simpledaycounter

import java.util.Calendar
import java.util.TimeZone

private val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

internal fun daysSince(
  dateMidnightUtcMillis: Long,
  nowMillis: Long,
  currentTimeZone: TimeZone
): Long {
  require(dateMidnightUtcMillis % 86_400_000L == 0L)
  val offsetMillis = currentTimeZone.getOffset(nowMillis)
  val todayMidnightUtcMillis = utcCalendar.apply {
    timeInMillis = nowMillis + offsetMillis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
  }.timeInMillis
  return (todayMidnightUtcMillis - dateMidnightUtcMillis) / 86_400_000L
}

internal fun dateMidnightUtcMillis(dayOfMonth: Int, month: Int, year: Int): Long {
  return utcCalendar.apply {
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
    set(Calendar.MONTH, month)
    set(Calendar.YEAR, year)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
  }.timeInMillis
}

internal inline fun dayMonthYear(
  dateMidnightUtcMillis: Long,
  result: (dayOfMonth: Int, month: Int, year: Int) -> Unit,
) {
  require(dateMidnightUtcMillis % 86_400_000L == 0L)
  utcCalendar.apply {
    timeInMillis = dateMidnightUtcMillis
  }
  result(
    utcCalendar.get(Calendar.DAY_OF_MONTH),
    utcCalendar.get(Calendar.MONTH),
    utcCalendar.get(Calendar.YEAR)
  )
}
