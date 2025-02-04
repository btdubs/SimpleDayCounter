package com.brianco.simpledaycounter

import android.app.Application

class SimpleDayCounterApplication : Application() {
  internal lateinit var widgetDataSaver: WidgetDataSaver

  override fun onCreate() {
    super.onCreate()
    widgetDataSaver = WidgetDataSaver(getSharedPreferences("widget", MODE_PRIVATE))
  }
}
