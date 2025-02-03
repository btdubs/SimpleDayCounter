package com.brianco.simpledaycounter

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class DayCounterWidgetConfigureActivity : Activity() {
  private lateinit var widgetDataSaver: WidgetDataSaver
  private lateinit var previewWidgetContainer: View
  private lateinit var previewLabel: TextView
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
    val datePicker = findViewById<DatePicker>(R.id.configure_date_picker)
    previewWidgetContainer = findViewById(R.id.widget_preview)
    previewLabel = previewWidgetContainer.findViewById(R.id.widget_label)
    val previewCounter = previewWidgetContainer.findViewById<TextView>(R.id.widget_counter)
    val previewDays = previewWidgetContainer.findViewById<TextView>(R.id.widget_days)
    val label = findViewById<EditText>(R.id.configure_label)
    val headerColorLinearLayoutView = findViewById<LinearLayout>(R.id.header_color_circle_container)
    val backgroundColorLinearLayoutView = findViewById<LinearLayout>(
      R.id.background_color_circle_container
    )
    val addWidgetButton = findViewById<Button>(R.id.configure_add)

    label.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(editable: Editable) {
        previewLabel.text = editable.toString()
      }
    })
    // val headerColors = listOf(
    //   R.color.header_1,
    //   R.color.header_2,
    //   R.color.header_3,
    //   R.color.header_4,
    //   R.color.header_5
    // )
    val headerColors = listOf(
      "#01579B",
      "#006064",
      "#004D40",
      "#1B5E20",
      "#33691E",
      "#EF6C00"
    )
    headerColors.forEach { colorHex ->
      val color = Color.parseColor(colorHex)
      addColorCircle(color, headerColorLinearLayoutView, false)
    }

    val backgroundColors = listOf(
      "#03A9F4",
      "#00BCD4",
      "#009688",
      "#4CAF50",
      "#8BC34A",
      "#FFA726"
    )
    backgroundColors.forEach { colorHex ->
      val color = Color.parseColor(colorHex)
      addColorCircle(color, backgroundColorLinearLayoutView, true)
    }

    datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
      val dayCount = dayCount(dateMidnightUtcMillis(dayOfMonth, monthOfYear, year))
      previewCounter.text = getFormattedDayCount(resources, dayCount)
      previewDays.text = getFormattedDays(resources, dayCount)
    }

    if (widgetDataSaver.isWidget(appWidgetId)) {
      dayMonthYear(widgetDataSaver.getDate(appWidgetId)) { dayOfMonth, month, year ->
        datePicker.updateDate(year, month, dayOfMonth)
      }
      label.setText(widgetDataSaver.getLabel(appWidgetId))
      selectedHeaderColor = widgetDataSaver.getHeaderColor(appWidgetId)
      selectedBackgroundColor = widgetDataSaver.getBackgroundColor(appWidgetId)
    } else {
      selectedHeaderColor = Color.parseColor(headerColors[0])
      selectedBackgroundColor = Color.parseColor(backgroundColors[0])
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

  private fun addColorCircle(color: Int, layout: LinearLayout, isBackground: Boolean) {
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
}
