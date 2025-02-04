package com.brianco.simpledaycounter

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.widget.TextView
import androidx.annotation.ColorInt

internal class SinceOrUntilOptionsController(
  private val sinceOption: TextView,
  private val untilOption: TextView,
  sinceSelected: Boolean,
  @ColorInt selectedColor: Int,
  private val onSelectedChangedListener: OnSelectedChangedListener
) {
  interface OnSelectedChangedListener {
    fun onSelectedChanged(sinceSelected: Boolean)
  }

  var sinceSelected: Boolean = sinceSelected
    private set
  private val sinceOrUntilSelectedBackground = ShapeDrawable(OvalShape()).apply {
    paint.color = selectedColor
  }
  private val sinceOrUntilUnselectedBackground = RippleDrawable(
    ColorStateList.valueOf(selectedColor),
    null,
    ShapeDrawable(OvalShape())
  )
  private val sinceOrUntilSelectedTextColor = sinceOption.context.getColor(
    R.color.since_or_until_selected_text
  )
  private val sinceOrUntilUnselectedTextColor = sinceOption.currentTextColor

  init {
    selectOption(sinceSelected)
    sinceOption.setOnClickListener {
      if (!this.sinceSelected) {
        this.sinceSelected = true
        selectOption(true)
        onSelectedChangedListener.onSelectedChanged(true)
      }
    }
    untilOption.setOnClickListener {
      if (this.sinceSelected) {
        this.sinceSelected = false
        selectOption(false)
        onSelectedChangedListener.onSelectedChanged(false)
      }
    }
  }

  fun setSelectedColor(@ColorInt selectedColor: Int) {
    sinceOrUntilSelectedBackground.paint.color = selectedColor
    sinceOrUntilUnselectedBackground.setColor(ColorStateList.valueOf(selectedColor))
    sinceOption.invalidate()
    untilOption.invalidate()
  }

  private fun selectOption(sinceSelected: Boolean) {
    val selectedOption: TextView
    val unselectedOption: TextView
    if (sinceSelected) {
      selectedOption = sinceOption
      unselectedOption = untilOption
    } else {
      selectedOption = untilOption
      unselectedOption = sinceOption
    }
    selectedOption.background = sinceOrUntilSelectedBackground
    selectedOption.setTextColor(sinceOrUntilSelectedTextColor)
    unselectedOption.background = sinceOrUntilUnselectedBackground
    unselectedOption.setTextColor(sinceOrUntilUnselectedTextColor)
  }
}
