package com.brianco.simpledaycounter

import android.content.SharedPreferences
import androidx.core.content.edit

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
    val dateKey = encodeDateKey(appWidgetId)
    val labelKey = encodeLabelKey(appWidgetId)
    val headerColorKey = encodeHeaderColorKey(appWidgetId)
    val backgroundColorKey = encodeBackgroundColorKey(appWidgetId)
    val sinceOrUntilKey = encodeSinceOrUntilKey(appWidgetId)
    sharedPreferences.edit {
      putLong(dateKey, date)
        .putString(labelKey, label)
        .putInt(headerColorKey, headerColor)
        .putInt(backgroundColorKey, backgroundColor)
        .putBoolean(sinceOrUntilKey, sinceOrUntil)
    }
  }

  fun delete(appWidgetId: Int) {
    val dateKey = encodeDateKey(appWidgetId)
    val labelKey = encodeLabelKey(appWidgetId)
    val headerColorKey = encodeHeaderColorKey(appWidgetId)
    val backgroundColorKey = encodeBackgroundColorKey(appWidgetId)
    val sinceOrUntilKey = encodeSinceOrUntilKey(appWidgetId)
    sharedPreferences.edit {
      remove(dateKey)
        .remove(labelKey)
        .remove(headerColorKey)
        .remove(backgroundColorKey)
        .remove(sinceOrUntilKey)
    }
  }

  fun isWidget(appWidgetId: Int): Boolean {
    return sharedPreferences.contains(encodeDateKey(appWidgetId))
  }

  fun getDate(appWidgetId: Int): Long {
    return getDate(encodeDateKey(appWidgetId))
  }

  fun getLabel(appWidgetId: Int): String {
    return getLabel(encodeLabelKey(appWidgetId))
  }

  fun getHeaderColor(appWidgetId: Int): Int {
    return getHeaderColor(encodeHeaderColorKey(appWidgetId))
  }

  fun getBackgroundColor(appWidgetId: Int): Int {
    return getBackgroundColor(encodeBackgroundColorKey(appWidgetId))
  }

  fun getSinceOrUntil(appWidgetId: Int): Boolean {
    return getSinceOrUntil(encodeSinceOrUntilKey(appWidgetId))
  }

  fun restore(oldAppWidgetIds: IntArray, newAppWidgetIds: IntArray) {
    require(oldAppWidgetIds.size == newAppWidgetIds.size) {
      "${oldAppWidgetIds.size} != ${newAppWidgetIds.size}"
    }

    val dates = ArrayList<Long>(oldAppWidgetIds.size)
    val labels = ArrayList<String>(oldAppWidgetIds.size)
    val headerColors = ArrayList<Int>(oldAppWidgetIds.size)
    val backgroundColors = ArrayList<Int>(oldAppWidgetIds.size)
    val sinceOrUntils = ArrayList<Boolean>(oldAppWidgetIds.size)

    sharedPreferences.edit {
      for (oldAppWidgetId in oldAppWidgetIds) {
        val dateKey = encodeDateKey(oldAppWidgetId)
        val labelKey = encodeLabelKey(oldAppWidgetId)
        val headerColorKey = encodeHeaderColorKey(oldAppWidgetId)
        val backgroundColorKey = encodeBackgroundColorKey(oldAppWidgetId)
        val sinceOrUntilKey = encodeSinceOrUntilKey(oldAppWidgetId)

        dates += getDate(dateKey)
        labels += getLabel(labelKey)
        headerColors += getHeaderColor(headerColorKey)
        backgroundColors += getBackgroundColor(backgroundColorKey)
        sinceOrUntils += getSinceOrUntil(sinceOrUntilKey)

        remove(dateKey)
        remove(labelKey)
        remove(headerColorKey)
        remove(backgroundColorKey)
        remove(sinceOrUntilKey)
      }

      for (i in newAppWidgetIds.indices) {
        val newAppWidgetId = newAppWidgetIds[i]

        val dateKey = encodeDateKey(newAppWidgetId)
        val labelKey = encodeLabelKey(newAppWidgetId)
        val headerColorKey = encodeHeaderColorKey(newAppWidgetId)
        val backgroundColorKey = encodeBackgroundColorKey(newAppWidgetId)
        val sinceOrUntilKey = encodeSinceOrUntilKey(newAppWidgetId)

        putLong(dateKey, dates[i])
        putString(labelKey, labels[i])
        putInt(headerColorKey, headerColors[i])
        putInt(backgroundColorKey, backgroundColors[i])
        putBoolean(sinceOrUntilKey, sinceOrUntils[i])
      }
    }
  }

  private fun getDate(dateKey: String): Long {
    // Fall back to start of the epoch when a user manually mangles his saved data.
    val date = sharedPreferences.getLong(dateKey, 0L)
    return date
  }

  private fun getLabel(labelKey: String): String {
    // Fall back to the empty string when a user manually mangles his saved data.
    val label = sharedPreferences.getString(labelKey, "")!!
    return label
  }

  private fun getHeaderColor(headerColorKey: String): Int {
    // Fall back to transparent when a user manually mangles his saved data.
    val headerColor = sharedPreferences.getInt(headerColorKey, 0)
    return headerColor
  }

  private fun getBackgroundColor(backgroundColorKey: String): Int {
    // Fall back to transparent when a user manually mangles his saved data.
    val backgroundColor = sharedPreferences.getInt(backgroundColorKey, 0)
    return backgroundColor
  }

  private fun getSinceOrUntil(sinceOrUntilKey: String): Boolean {
    // Fall back to true when a user manually mangles his saved data.
    val sinceOrUntil = sharedPreferences.getBoolean(sinceOrUntilKey, true)
    return sinceOrUntil
  }

  private fun encodeDateKey(appWidgetId: Int): String {
    return "$appWidgetId-date"
  }

  private fun encodeLabelKey(appWidgetId: Int): String {
    return "$appWidgetId-label"
  }

  private fun encodeHeaderColorKey(appWidgetId: Int): String {
    return "$appWidgetId-headerColor"
  }

  private fun encodeBackgroundColorKey(appWidgetId: Int): String {
    return "$appWidgetId-backgroundColor"
  }

  private fun encodeSinceOrUntilKey(appWidgetId: Int): String {
    return "$appWidgetId-sinceOrUntil"
  }
}
