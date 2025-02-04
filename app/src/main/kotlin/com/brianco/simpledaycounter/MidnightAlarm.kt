package com.brianco.simpledaycounter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.TimeZone

internal const val ACTION_MIDNIGHT_UPDATE = "com.brianco.simpledaycounter.ACTION_MIDNIGHT_UPDATE"

internal fun setMidnightAlarm(context: Context) {
  val alarmManager = context.getSystemService(AlarmManager::class.java)
  val nextMidnight = Calendar.getInstance(TimeZone.getDefault())
    .apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    .timeInMillis + 86_400_000L
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
