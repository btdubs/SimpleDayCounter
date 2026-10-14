package com.brianco.simpledaycounter

import com.google.common.truth.Truth.assertThat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import org.junit.Assert.fail
import org.junit.Test

class DatesTest {
  @Test fun daysSinceRequiresMidnightUtc() {
    val utcZoneId = ZoneOffset.UTC
    try {
      daysSince(1L, 1L, utcZoneId)
      fail()
    } catch (expected: IllegalArgumentException) {
    }
    try {
      daysSince(1_738_540_800_010L, 1L, utcZoneId)
      fail()
    } catch (expected: IllegalArgumentException) {
    }
  }

  @Test fun countDaysSince() {
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(1_738_540_800_000L, 1_738_630_000_000L, easternZoneId)
    ).isEqualTo(0L)
    assertThat(
      daysSince(1_738_540_800_000L, 1_738_630_000_000L, utcZoneId)
    ).isEqualTo(1L)
    assertThat(
      daysSince(1_738_627_200_000L, 1_738_630_000_000L, easternZoneId)
    ).isEqualTo(-1L)
    assertThat(
      daysSince(1_738_627_200_000L, 1_738_630_000_000L, utcZoneId)
    ).isEqualTo(0L)
  }

  @Test fun daysSincePositive() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(
        format.parse("2025-02-02 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 01:00:00 EST")!!.time,
        easternZoneId
      )
    ).isEqualTo(1L)
    assertThat(
      daysSince(
        format.parse("2025-02-02 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 01:00:00 UTC")!!.time,
        utcZoneId
      )
    ).isEqualTo(1L)
  }

  @Test fun daysSinceZero() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(
        format.parse("2025-02-03 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 21:00:00 EST")!!.time,
        easternZoneId
      )
    ).isEqualTo(0L)
    assertThat(
      daysSince(
        format.parse("2025-02-03 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 21:00:00 UTC")!!.time,
        utcZoneId
      )
    ).isEqualTo(0L)
  }

  @Test fun daysSinceNegative() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(
        format.parse("2025-02-04 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 21:00:00 EST")!!.time,
        easternZoneId
      )
    ).isEqualTo(-1L)
    assertThat(
      daysSince(
        format.parse("2025-02-04 00:00:00 UTC")!!.time,
        format.parse("2025-02-03 21:00:00 UTC")!!.time,
        utcZoneId
      )
    ).isEqualTo(-1L)
  }

  @Test fun daysSinceYear() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(
        format.parse("2026-02-01 00:00:00 UTC")!!.time,
        format.parse("2025-02-01 00:00:00 EST")!!.time,
        easternZoneId
      )
    ).isEqualTo(-365L)
    assertThat(
      daysSince(
        format.parse("2026-02-01 00:00:00 UTC")!!.time,
        format.parse("2025-02-01 00:00:00 UTC")!!.time,
        utcZoneId
      )
    ).isEqualTo(-365L)
  }

  @Test fun daysSinceLeapYear() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
    val easternZoneId = ZoneId.of("America/New_York")
    val utcZoneId = ZoneOffset.UTC
    assertThat(
      daysSince(
        format.parse("2024-02-01 00:00:00 UTC")!!.time,
        format.parse("2025-02-01 00:00:00 EST")!!.time,
        easternZoneId
      )
    ).isEqualTo(366L)
    assertThat(
      daysSince(
        format.parse("2024-02-01 00:00:00 UTC")!!.time,
        format.parse("2025-02-01 00:00:00 UTC")!!.time,
        utcZoneId
      )
    ).isEqualTo(366L)
  }

  @Test fun calendarToMidnightUtc() {
    assertThat(dateMidnightUtcMillis(1, 0, 1970)).isEqualTo(0L)
    assertThat(dateMidnightUtcMillis(2, 0, 1970)).isEqualTo(86_400_000L)
    assertThat(dateMidnightUtcMillis(3, 1, 2025)).isEqualTo(1_738_540_800_000L)
  }

  @Test fun midnightUtcToCalendar() {
    dayMonthYear(0L) { dayOfMonth, month, year ->
      assertThat(dayOfMonth).isEqualTo(1)
      assertThat(month).isEqualTo(0)
      assertThat(year).isEqualTo(1970)
    }
    dayMonthYear(86_400_000L) { dayOfMonth, month, year ->
      assertThat(dayOfMonth).isEqualTo(2)
      assertThat(month).isEqualTo(0)
      assertThat(year).isEqualTo(1970)
    }
    dayMonthYear(1_738_540_800_000L) { dayOfMonth, month, year ->
      assertThat(dayOfMonth).isEqualTo(3)
      assertThat(month).isEqualTo(1)
      assertThat(year).isEqualTo(2025)
    }
  }
}
