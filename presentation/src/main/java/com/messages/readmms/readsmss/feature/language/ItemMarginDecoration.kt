package com.messages.readmms.readsmss.feature.language

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemMarginDecoration(private val horizontalMargin: Int, private val verticalMargin: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Set the margins for the item
        outRect.left = horizontalMargin
        outRect.right = horizontalMargin
        outRect.top = verticalMargin
        outRect.bottom = verticalMargin
    }
}

fun Context.dpToPx(dp: Int): Int {
    val density = resources.displayMetrics.density
    return (dp * density).toInt()
}