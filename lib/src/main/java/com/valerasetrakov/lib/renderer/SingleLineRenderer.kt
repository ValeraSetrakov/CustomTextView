package com.valerasetrakov.lib.renderer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout
import com.valerasetrakov.lib.renderer.BaseTextBgRenderer
import kotlin.math.max
import kotlin.math.min

/**
 * Draws the background for text that starts and ends on the same line.
 *
 * @param horizontalPadding the padding to be applied to left & right of the background
 * @param verticalPadding the padding to be applied to top & bottom of the background
 * @param drawable the drawableMarked used to draw the background
 */
open class SingleLineRenderer(
    horizontalPadding: Int,
    verticalPadding: Int,
    var drawable: Drawable
) : BaseTextBgRenderer(horizontalPadding, verticalPadding) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {
        val lineTop = getLineTop(layout, startLine)
        val lineBottom = getLineBottom(layout, startLine)
        // get min of start/end for left, and max of start/end for right since we don't
        // the language direction
        val left = min(startOffset, endOffset)
        val right = max(startOffset, endOffset)
        drawable.setBounds(left, lineTop, right, lineBottom)
        drawable.draw(canvas)
    }
}