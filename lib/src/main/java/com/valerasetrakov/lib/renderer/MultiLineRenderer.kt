package com.valerasetrakov.lib.renderer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout

/**
 * Draws the background for text that starts and ends on different lines.
 *
 * @param horizontalPadding the padding to be applied to left & right of the background
 * @param verticalPadding the padding to be applied to top & bottom of the background
 * @param drawableLeft the drawableMarked used to draw left edge of the background
 * @param drawableMid the drawableMarked used to draw for whole line
 * @param drawableRight the drawableMarked used to draw right edge of the background
 */
open class MultiLineRenderer(
    horizontalPadding: Int,
    verticalPadding: Int,
    var drawableLeft: Drawable,
    var drawableMid: Drawable,
    var drawableRight: Drawable
) : BaseTextBgRenderer(horizontalPadding, verticalPadding) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {
        // draw the first line
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine) - horizontalPadding
        } else {
            layout.getLineRight(startLine) + horizontalPadding
        }.toInt()

        var lineBottom = getLineBottom(layout, startLine)
        var lineTop = getLineTop(layout, startLine)
        drawStart(canvas, startOffset, lineTop, lineEndOffset, lineBottom)

        // for the lines in the middle draw the mid drawableMarked
        for (line in startLine + 1 until endLine) {
            lineTop = getLineTop(layout, line)
            lineBottom = getLineBottom(layout, line)
            drawableMid.setBounds(
                (layout.getLineLeft(line).toInt() - horizontalPadding),
                lineTop,
                (layout.getLineRight(line).toInt() + horizontalPadding),
                lineBottom
            )
            drawableMid.draw(canvas)
        }

        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine) + horizontalPadding
        } else {
            layout.getLineLeft(startLine) - horizontalPadding
        }.toInt()

        // draw the last line
        lineBottom = getLineBottom(layout, endLine)
        lineTop = getLineTop(layout, endLine)

        drawEnd(canvas, lineStartOffset, lineTop, endOffset, lineBottom)
    }

    /**
     * Draw the first line of a multiline annotation. Handles LTR/RTL.
     *
     * @param canvas Canvas to draw onto
     * @param start start coordinate for the background
     * @param top top coordinate for the background
     * @param end end coordinate for the background
     * @param bottom bottom coordinate for the background
     */
    private fun drawStart(canvas: Canvas, start: Int, top: Int, end: Int, bottom: Int) {
        if (start > end) {
            drawableRight.setBounds(end, top, start, bottom)
            drawableRight.draw(canvas)
        } else {
            drawableLeft.setBounds(start, top, end, bottom)
            drawableLeft.draw(canvas)
        }
    }

    /**
     * Draw the last line of a multiline annotation. Handles LTR/RTL.
     *
     * @param canvas Canvas to draw onto
     * @param start start coordinate for the background
     * @param top top position for the background
     * @param end end coordinate for the background
     * @param bottom bottom coordinate for the background
     */
    private fun drawEnd(canvas: Canvas, start: Int, top: Int, end: Int, bottom: Int) {
        if (start > end) {
            drawableLeft.setBounds(end, top, start, bottom)
            drawableLeft.draw(canvas)
        } else {
            drawableRight.setBounds(start, top, end, bottom)
            drawableRight.draw(canvas)
        }
    }
}