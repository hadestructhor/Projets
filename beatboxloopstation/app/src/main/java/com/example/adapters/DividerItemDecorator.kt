package com.example.adapters

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.opengl.ETC1.getWidth
import android.view.View
import androidx.core.view.ViewCompat


class DividerItemDecorator(private val divider: Drawable?) : RecyclerView.ItemDecoration() {

    val mBounds = Rect()

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerTop = parent.paddingTop
        val dividerBottom = parent.height - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            parent.getLayoutManager()?.getDecoratedBoundsWithMargins(child, mBounds)
            val dividerRight = mBounds.right + Math.round(ViewCompat.getTranslationX(child))
            val dividerLeft = dividerRight - (divider?.intrinsicWidth?:0)
            divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider?.draw(canvas)
        }
    }
}