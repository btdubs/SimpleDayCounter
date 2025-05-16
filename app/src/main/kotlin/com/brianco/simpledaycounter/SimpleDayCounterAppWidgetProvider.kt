package com.brianco.simpledaycounter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.CalendarContract
import android.view.View
import android.widget.RemoteViews
import java.util.TimeZone

internal fun updateAppWidget(
  context: Context,
  appWidgetManager: AppWidgetManager,
  appWidgetId: Int,
  saver: WidgetDataSaver
) {
  if (!saver.isWidget(appWidgetId)) {
    return // We are still in the initial configuration.
  }

  val views = RemoteViews(context.packageName, R.layout.widget)

  val savedDate = saver.getDate(appWidgetId)
  val sinceSelected = saver.getSinceOrUntil(appWidgetId)
  val nowMillis = System.currentTimeMillis()
  val currentTimeZone = TimeZone.getDefault()
  val dayCount = dayCount(savedDate, sinceSelected, nowMillis, currentTimeZone)

  val resources = context.resources
  views.setTextViewText(
    R.id.widget_label,
    saver.getLabel(appWidgetId)
  )
  views.setViewVisibility(
    R.id.widget_counter_loading,
    View.GONE
  )
  views.setViewVisibility(
    R.id.widget_counter,
    View.VISIBLE
  )
  views.setTextViewText(
    R.id.widget_counter,
    getFormattedDayCount(resources, dayCount)
  )
  views.setTextViewText(
    R.id.widget_days,
    getFormattedDays(resources, dayCount)
  )
  views.setInt(
    R.id.widget_label,
    "setBackgroundColor",
    saver.getHeaderColor(appWidgetId)
  )
  views.setInt(
    android.R.id.background,
    "setBackgroundColor", saver.getBackgroundColor(appWidgetId)
  )

  val pendingIntent =
    PendingIntent.getActivity(
      context,
      0,
      calendarIntent(savedDate, nowMillis, currentTimeZone),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  views.setOnClickPendingIntent(android.R.id.background, pendingIntent)

  appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun dayCount(
  savedDate: Long,
  sinceSelected: Boolean,
  nowMillis: Long,
  currentTimeZone: TimeZone
): Long {
  val daysSince = daysSince(savedDate, nowMillis, currentTimeZone)
  return if (sinceSelected) {
    daysSince + 1
  } else {
    -daysSince
  }
}

internal fun getFormattedDayCount(resources: Resources, dayCount: Long): String {
  return resources.getString(R.string.widget_count, dayCount)
}

internal fun getFormattedDays(resources: Resources, dayCount: Long): CharSequence {
  var dayCountForFormatting = dayCount.coerceIn(
    Integer.MIN_VALUE.toLong(),
    Integer.MAX_VALUE.toLong()
  ).toInt()
  if (dayCountForFormatting == -1) {
    dayCountForFormatting = 1 // -1 day, not -1 days. Android plurals don't let us special-case -1.
  }
  return resources.getQuantityText(R.plurals.widget_days, dayCountForFormatting)
}

private fun calendarIntent(
  dateMidnightUtcMillis: Long,
  nowMillis: Long,
  currentTimeZone: TimeZone
): Intent {
  // The calendar app will add the time zone offset to go to the day we selected.
  val offsetMillis = currentTimeZone.getOffset(nowMillis)
  val startMillis = dateMidnightUtcMillis - offsetMillis
  val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
  ContentUris.appendId(builder, startMillis)
  return Intent(Intent.ACTION_VIEW, builder.build())
}

class SimpleDayCounterAppWidgetProvider : AppWidgetProvider() {
  private lateinit var widgetDataSaver: WidgetDataSaver

  override fun onReceive(context: Context, intent: Intent) {
    val app = context.applicationContext as SimpleDayCounterApplication
    widgetDataSaver = app.widgetDataSaver
    val action = intent.action
    if (action == ACTION_MIDNIGHT_UPDATE ||
      action == Intent.ACTION_TIME_CHANGED ||
      action == Intent.ACTION_TIMEZONE_CHANGED) {
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val appWidgetIds = appWidgetManager.getAppWidgetIds(
        ComponentName(context, SimpleDayCounterAppWidgetProvider::class.java)
      )
      for (appWidgetId in appWidgetIds) {
        updateAppWidget(context, appWidgetManager, appWidgetId, widgetDataSaver)
      }
      setMidnightAlarm(context)
      return
    }
    super.onReceive(context, intent)
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId, widgetDataSaver)
    }
  }

  override fun onDeleted(context: Context, appWidgetIds: IntArray) {
    for (appWidgetId in appWidgetIds) {
      widgetDataSaver.delete(appWidgetId)
    }
  }

  override fun onEnabled(context: Context) {
    setMidnightAlarm(context)
  }

  override fun onDisabled(context: Context) {
    cancelMidnightAlarm(context)
  }
}
