package com.brianco.simpledaycounter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.ZoneId

internal const val ACTION_MIDNIGHT_UPDATE = "com.brianco.simpledaycounter.ACTION_MIDNIGHT_UPDATE"

internal fun setMidnightAlarm(context: Context) {
  val alarmManager = context.getSystemService(AlarmManager::class.java)
  val currentZoneId = ZoneId.systemDefault()
  val nextMidnight = LocalDate.now(currentZoneId)
    .plusDays(1)
    .atStartOfDay(currentZoneId)
    .toInstant()
    .toEpochMilli()
  val intent = Intent(context, SimpleDayCounterAppWidgetProvider::class.java).apply {
    action = ACTION_MIDNIGHT_UPDATE
  }
  val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
  alarmManager.set(AlarmManager.RTC, nextMidnight, pendingIntent)
}

internal fun cancelMidnightAlarm(context: Context) {
  val alarmManager = context.getSystemService(AlarmManager::class.java)
  val intent = Intent(context, SimpleDayCounterAppWidgetProvider::class.java).apply {
    action = ACTION_MIDNIGHT_UPDATE
  }
  val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
  alarmManager.cancel(pendingIntent)
}
