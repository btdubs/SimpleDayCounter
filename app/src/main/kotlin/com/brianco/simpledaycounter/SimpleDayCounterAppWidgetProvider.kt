package com.brianco.simpledaycounter

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
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
  val dayCount = dayCount(savedDate)

  val resources = context.resources
  views.setTextViewText(R.id.widget_label, saver.getLabel(appWidgetId))
  views.setTextViewText(R.id.widget_counter, getFormattedDayCount(resources, dayCount))
  views.setTextViewText(R.id.widget_days, getFormattedDays(resources, dayCount))
  views.setInt(R.id.widget_label, "setBackgroundColor", saver.getHeaderColor(appWidgetId))
  views.setInt(android.R.id.background, "setBackgroundColor", saver.getBackgroundColor(appWidgetId))

  appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun dayCount(savedDate: Long): Long {
  return daysSince(savedDate, System.currentTimeMillis(), TimeZone.getDefault()) + 1
}

internal fun getFormattedDayCount(resources: Resources, dayCount: Long): String {
  return resources.getString(R.string.widget_count, dayCount)
}

internal fun getFormattedDays(resources: Resources, dayCount: Long): String {
  var dayCountForFormatting = dayCount.coerceIn(
    Integer.MIN_VALUE.toLong(), Integer.MAX_VALUE.toLong()
  ).toInt()
  if (dayCountForFormatting == -1) {
    dayCountForFormatting = 1 // -1 days, not -1 day. Android plurals don't let us special-case -1.
  }
  return resources.getQuantityString(
    R.plurals.widget_days,
    dayCountForFormatting
  )
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
