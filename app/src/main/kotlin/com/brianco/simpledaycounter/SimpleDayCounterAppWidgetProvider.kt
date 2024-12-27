package com.brianco.simpledaycounter

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
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

  // Construct the RemoteViews object
  val views = RemoteViews(context.packageName, R.layout.widget)

  val savedDate = saver.getDate(appWidgetId)
  val dayCount = daysSince(savedDate, System.currentTimeMillis(), TimeZone.getDefault()).run {
    check(this <= Integer.MAX_VALUE)
    toInt()
  }

  views.setTextViewText(R.id.widget_label, saver.getLabel(appWidgetId))
  views.setTextViewText(R.id.widget_counter, dayCount.toString())
  views.setTextViewText(R.id.widget_days, context.resources.getQuantityString(
    R.plurals.widget_days,
    if (dayCount == -1) 1 else dayCount // todo: -1 day or days?
  ))
  views.setInt(R.id.widget_label, "setBackgroundColor", saver.getHeaderColor(appWidgetId))
  views.setInt(android.R.id.background, "setBackgroundColor", saver.getBackgroundColor(appWidgetId))

  // Instruct the widget manager to update the widget
  appWidgetManager.updateAppWidget(appWidgetId, views)
}

class SimpleDayCounterAppWidgetProvider : AppWidgetProvider() {
  private lateinit var widgetDataSaver: WidgetDataSaver

  @SuppressLint("UnsafeProtectedBroadcastReceiver") // The super call checks the action.
  override fun onReceive(context: Context, intent: Intent) {
    val app = context.applicationContext as SimpleDayCounterApplication
    widgetDataSaver = app.widgetDataSaver
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
}
