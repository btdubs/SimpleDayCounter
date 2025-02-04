package com.brianco.simpledaycounter

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView

class DayCounterWidgetConfigureActivity : Activity() {
  private val selectedHeaderColorSavedStateKey =
    "${DayCounterWidgetConfigureActivity::class.qualifiedName}.selectedHeaderColor"
  private val selectedBackgroundColorSavedStateKey =
    "${DayCounterWidgetConfigureActivity::class.qualifiedName}.selectedBackgroundColor"
  private lateinit var widgetDataSaver: WidgetDataSaver
  private lateinit var datePicker: DatePicker
  private lateinit var previewWidgetContainer: View
  private lateinit var previewLabel: TextView
  private lateinit var previewCounter: TextView
  private lateinit var previewDays: TextView
  private var selectedHeaderColor: Int = 0
  private var selectedBackgroundColor: Int = 0

  public override fun onCreate(savedInstanceState: Bundle?) {
    val app = application as SimpleDayCounterApplication
    widgetDataSaver = app.widgetDataSaver
    super.onCreate(savedInstanceState)
    val appWidgetId = intent.getIntExtra(
      AppWidgetManager.EXTRA_APPWIDGET_ID,
      AppWidgetManager.INVALID_APPWIDGET_ID
    )

    setResult(RESULT_CANCELED)
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      return
    }

    setContentView(R.layout.activity_configuration)
    datePicker = findViewById(R.id.configure_date_picker)
    previewWidgetContainer = findViewById(R.id.widget_preview)
    previewLabel = previewWidgetContainer.findViewById(R.id.widget_label)
    previewCounter = previewWidgetContainer.findViewById(R.id.widget_counter)
    previewDays = previewWidgetContainer.findViewById(R.id.widget_days)
    val label = findViewById<EditText>(R.id.configure_label)
    val headerColorContainer = findViewById<ViewGroup>(R.id.header_color_circle_container)
    val backgroundColorContainer = findViewById<ViewGroup>(R.id.background_color_circle_container)
    val addWidgetButton = findViewById<Button>(R.id.configure_add)

    val headerColors = listOf(
      R.color.header_1,
      R.color.header_2,
      R.color.header_3,
      R.color.header_4,
      R.color.header_5,
      R.color.header_6
    )
    val backgroundColors = listOf(
      R.color.background_1,
      R.color.background_2,
      R.color.background_3,
      R.color.background_4,
      R.color.background_5,
      R.color.background_6
    )
    for (i in headerColors.indices) {
      addColorCircle(getColor(headerColors[i]), headerColorContainer, false)
    }
    for (i in backgroundColors.indices) {
      addColorCircle(getColor(backgroundColors[i]), backgroundColorContainer, true)
    }

    datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
      updateWidgetPreviewDayCounter(dayOfMonth, monthOfYear, year)
    }

    label.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(editable: Editable) {
        previewLabel.text = editable.toString()
      }
    })

    if (savedInstanceState != null) {
      // onRestoreInstanceState will set the previewCounter and previewDays texts after Android
      // restores the DatePicker state.
      // The label EditText's TextWatcher will set the previewLabel TextView text when Android
      // restores the label EditText's text.
      selectedHeaderColor = savedInstanceState.getInt(selectedHeaderColorSavedStateKey)
      selectedBackgroundColor = savedInstanceState.getInt(selectedBackgroundColorSavedStateKey)
    } else if (widgetDataSaver.isWidget(appWidgetId)) {
      dayMonthYear(widgetDataSaver.getDate(appWidgetId)) { dayOfMonth, month, year ->
        datePicker.updateDate(year, month, dayOfMonth)
      }
      // The DatePicker change listener sets the previewCounter and previewDays texts.
      label.setText(widgetDataSaver.getLabel(appWidgetId))
      // The label EditText's TextWatcher sets the previewLabel TextView text.
      selectedHeaderColor = widgetDataSaver.getHeaderColor(appWidgetId)
      selectedBackgroundColor = widgetDataSaver.getBackgroundColor(appWidgetId)
    } else {
      selectedHeaderColor = getColor(headerColors[0])
      selectedBackgroundColor = getColor(backgroundColors[0])
      updateWidgetPreviewDayCounter()
    }
    updateWidgetPreviewColors(selectedHeaderColor, selectedBackgroundColor)

    addWidgetButton.setOnClickListener {
      widgetDataSaver.save(
        appWidgetId,
        dateMidnightUtcMillis(datePicker.dayOfMonth, datePicker.month, datePicker.year),
        label.text.toString(),
        selectedHeaderColor,
        selectedBackgroundColor
      )
      updateAppWidget(
        this,
        getSystemService(AppWidgetManager::class.java),
        appWidgetId,
        widgetDataSaver
      )
      setResult(RESULT_OK)
      finish()
    }
  }

  private fun updateWidgetPreviewColors(headerColor: Int, backgroundColor: Int) {
    previewLabel.setBackgroundColor(headerColor)
    previewWidgetContainer.setBackgroundColor(backgroundColor)
  }

  private fun updateWidgetPreviewDayCounter() {
    updateWidgetPreviewDayCounter(datePicker.dayOfMonth, datePicker.month, datePicker.year)
  }

  private fun updateWidgetPreviewDayCounter(dayOfMonth: Int, month: Int, year: Int) {
    val dayCount = dayCount(dateMidnightUtcMillis(dayOfMonth, month, year))
    val resources = resources
    previewCounter.text = getFormattedDayCount(resources, dayCount)
    previewDays.text = getFormattedDays(resources, dayCount)
  }

  private fun addColorCircle(color: Int, layout: ViewGroup, isBackground: Boolean) {
    val layoutInflater = LayoutInflater.from(this)
    val colorView = layoutInflater.inflate(R.layout.color, layout, false)
    colorView.background = ShapeDrawable(OvalShape()).apply {
      paint.color = color
    }
    colorView.setOnClickListener {
      if (isBackground) {
        selectedBackgroundColor = color
      } else {
        selectedHeaderColor = color
      }
      updateWidgetPreviewColors(selectedHeaderColor, selectedBackgroundColor)
    }
    layout.addView(colorView)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    updateWidgetPreviewDayCounter()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(selectedHeaderColorSavedStateKey, selectedHeaderColor)
    outState.putInt(selectedBackgroundColorSavedStateKey, selectedBackgroundColor)
  }
}
