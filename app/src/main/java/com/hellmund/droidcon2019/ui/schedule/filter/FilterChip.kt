package com.hellmund.droidcon2019.ui.schedule.filter

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import com.hellmund.droidcon2019.R

class FilterChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Chip(context, attrs, defStyle) {

    init {
        isCheckable = true
        isClickable = true
        isCheckedIconVisible = false
        isCloseIconVisible = false
        setTextColor(Color.WHITE)
        setChipBackgroundColorResource(R.color.selector_chip_background)
    }

    fun disable() {
        isCheckable = false
        isClickable = false
    }

}
