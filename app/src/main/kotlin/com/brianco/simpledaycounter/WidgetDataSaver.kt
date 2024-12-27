package com.brianco.simpledaycounter

import android.content.SharedPreferences

internal class WidgetDataSaver(private val sharedPreferences: SharedPreferences) {
  fun save(appWidgetId: Int, date: Long, label: String, headerColor: Int, backgroundColor: Int) {
    require(date >= 0L)
    val dateKey = "$appWidgetId-date"
    val labelKey = "$appWidgetId-label"
    val headerColorKey = "$appWidgetId-headerColor"
    val backgroundColorKey = "$appWidgetId-backgroundColor"
    sharedPreferences.edit()
      .putLong(dateKey, date)
      .putString(labelKey, label)
      .putInt(headerColorKey, headerColor)
      .putInt(backgroundColorKey, backgroundColor)
      .apply()
  }

  fun delete(appWidgetId: Int) {
    val dateKey = "$appWidgetId-date"
    val labelKey = "$appWidgetId-label"
    val headerColorKey = "$appWidgetId-headerColor"
    val backgroundColorKey = "$appWidgetId-backgroundColor"
    sharedPreferences.edit()
      .remove(dateKey)
      .remove(labelKey)
      .remove(headerColorKey)
      .remove(backgroundColorKey)
      .apply()
  }

  fun isWidget(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-date")
  }

  fun hasDate(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-date")
  }

  fun hasLabel(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-label")
  }

  fun hasHeaderColor(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-headerColor")
  }

  fun hasBackgroundColor(appWidgetId: Int): Boolean {
    return sharedPreferences.contains("$appWidgetId-backgroundColor")
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
}
