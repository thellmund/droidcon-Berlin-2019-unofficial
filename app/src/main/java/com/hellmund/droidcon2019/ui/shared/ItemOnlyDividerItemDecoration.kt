package com.hellmund.droidcon2019.ui.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.droidcon2019.R

class ItemOnlyDividerItemDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {

    private var divider: Drawable

    init {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val timeTextView: TextView? = child.findViewById(R.id.timeTextView)
            if (timeTextView != null) {
                // This is a header item
                continue
            }

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

}