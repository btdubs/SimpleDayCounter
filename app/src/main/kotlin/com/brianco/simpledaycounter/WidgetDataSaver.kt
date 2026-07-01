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
    val date = sharedPreferences.getLong(encodeDateKey(appWidgetId), -1L)
    check(date != -1L)
    return date
  }

  fun getLabel(appWidgetId: Int): String {
    val label = sharedPreferences.getString(encodeLabelKey(appWidgetId), null)
    checkNotNull(label)
    return label
  }

  fun getHeaderColor(appWidgetId: Int): Int {
    val headerColor = sharedPreferences.getInt(encodeHeaderColorKey(appWidgetId), 0)
    return headerColor
  }

  fun getBackgroundColor(appWidgetId: Int): Int {
    val backgroundColor = sharedPreferences.getInt(encodeBackgroundColorKey(appWidgetId), 0)
    return backgroundColor
  }

  fun getSinceOrUntil(appWidgetId: Int): Boolean {
    val sinceOrUntil = sharedPreferences.getBoolean(encodeSinceOrUntilKey(appWidgetId), true)
    return sinceOrUntil
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

        val date = sharedPreferences.getLong(dateKey, -1L)
        check(date != -1L)
        dates += date

        val label = sharedPreferences.getString(labelKey, null)
        checkNotNull(label)
        labels += label

        val headerColor = sharedPreferences.getInt(headerColorKey, 0)
        headerColors += headerColor

        val backgroundColor = sharedPreferences.getInt(backgroundColorKey, 0)
        backgroundColors += backgroundColor

        val sinceOrUntil = sharedPreferences.getBoolean(sinceOrUntilKey, true)
        sinceOrUntils += sinceOrUntil

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
