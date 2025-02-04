package com.brianco.simpledaycounter

import android.content.SharedPreferences

internal class WidgetDataSaver(private val sharedPreferences: SharedPreferences) {
  fun save(
    appWidgetId: Int,
    date: Long,
    label: String,
    headerColor: Int,
    backgroundColor: Int,
    sinceOrUntil: Boolean
  ) {
    require(date >= 0L)
    val dateKey = "$appWidgetId-date"
    val labelKey = "$appWidgetId-label"
    val headerColorKey = "$appWidgetId-headerColor"
    val backgroundColorKey = "$appWidgetId-backgroundColor"
    val sinceOrUntilKey = "$appWidgetId-sinceOrUntil"
    sharedPreferences.edit()
      .putLong(dateKey, date)
      .putString(labelKey, label)
      .putInt(headerColorKey, headerColor)
      .putInt(backgroundColorKey, backgroundColor)
      .putBoolean(sinceOrUntilKey, sinceOrUntil)
      .apply()
  }

  fun delete(appWidgetId: Int) {
    val dateKey = "$appWidgetId-date"
    val labelKey = "$appWidgetId-label"
    val headerColorKey = "$appWidgetId-headerColor"
    val backgroundColorKey = "$appWidgetId-backgroundColor"
    val sinceOrUntilKey = "$appWidgetId-sinceOrUntil"
    sharedPreferences.edit()
      .remove(dateKey)
      .remove(labelKey)
      .remove(headerColorKey)
      .remove(backgroundColorKey)
      .remove(sinceOrUntilKey)
      .apply()
  }

  fun isWidget(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-date")
  }

  fun getDate(appWidgetId: Int): Long {
    val date = sharedPreferences.getLong("$appWidgetId-date", -1L)
    check(date != -1L)
    return date
  }

  fun getLabel(appWidgetId: Int): String {
    val label = sharedPreferences.getString("$appWidgetId-label", null)
    checkNotNull(label)
    return label
  }

  fun getHeaderColor(appWidgetId: Int): Int {
    val color = sharedPreferences.getInt("$appWidgetId-headerColor", 0)
    return color
  }

  fun getBackgroundColor(appWidgetId: Int): Int {
    val color = sharedPreferences.getInt("$appWidgetId-backgroundColor", 0)
    return color
  }

  fun getSinceOrUntil(appWidgetId: Int): Boolean {
    val sinceOrUntil = sharedPreferences.getBoolean("$appWidgetId-sinceOrUntil", true)
    return sinceOrUntil
  }
}
