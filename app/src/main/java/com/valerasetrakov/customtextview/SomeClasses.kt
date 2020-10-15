package com.valerasetrakov.customtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Annotation
import android.text.Layout
import android.text.Spannable
import com.valerasetrakov.customtextview.example.drawable.doubledrawable.DoubleDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.LeftFailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.LeftMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.MarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.MiddleMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.RightMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.FailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.MiddleFailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.RightFailDrawable
import com.valerasetrakov.lib.Renderer
import kotlin.math.max
import kotlin.math.min

class MarkedRendererDelegate(singleRenderer: MarkedSingleLineRenderer, multiLineRenderer: MarkedMultiLineRenderer) :
    StyleRendererDelegate(singleRenderer, multiLineRenderer) {

    constructor(horizontalPadding: Int = 0, verticalPadding: Int = 0, context: Context):
            this(MarkedSingleLineRenderer(horizontalPadding, verticalPadding, context),
                MarkedMultiLineRenderer(horizontalPadding, verticalPadding, context))

    companion object {
        const val MARKED_VALUE = "MARKED_KEY"
        fun createAnnotation() =
            Annotation(STYLE_ANNOTATION_KEY, MARKED_VALUE)
    }

    override fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean {
        if (super.isValidAnnotation(annotation, flags)) {
            val annotationValue = annotation.value
            if (annotationValue == MARKED_VALUE)
                return true
        }
        return false
    }
}

class MarkedSingleLineRenderer(horizontalPadding: Int, verticalPadding: Int, context: Context) :
    SingleLineRenderer(horizontalPadding, verticalPadding, MarkedDrawable(context))

class MarkedMultiLineRenderer(
    horizontalPadding: Int,
    verticalPadding: Int,
    context: Context
) : MultiLineRenderer(horizontalPadding, verticalPadding, LeftMarkedDrawable(context), MiddleMarkedDrawable(context), RightMarkedDrawable(context))

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
) : TextRoundedBgRenderer(horizontalPadding, verticalPadding) {

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

/**
 * Base class for single and multi line rounded background renderers.
 *
 * @param horizontalPadding the padding to be applied to left & right of the background
 * @param verticalPadding the padding to be applied to top & bottom of the background
 */
abstract class TextRoundedBgRenderer(
    val horizontalPadding: Int,
    val verticalPadding: Int
) : Renderer {

    /**
     * Get the top offset of the line and add padding into account so that there is a gap between
     * top of the background and top of the text.
     *
     * @param layout Layout object that contains the text
     * @param line line number
     */
    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line) - verticalPadding
    }

    /**
     * Get the bottom offset of the line and add padding into account so that there is a gap between
     * bottom of the background and bottom of the text.
     *
     * @param layout Layout object that contains the text
     * @param line line number
     */
    protected fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line) + verticalPadding
    }
}

abstract class StyleRendererDelegate(singleRenderer: SingleLineRenderer, multiLineRenderer: MultiLineRenderer) :
    BaseRendererDelegate(singleRenderer, multiLineRenderer) {

    companion object {
        const val STYLE_ANNOTATION_KEY = "STYLE_ANNOTATION_KEY"
    }

    override fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean {
        val annotationKey = annotation.key
        return annotationKey == STYLE_ANNOTATION_KEY
    }

}

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
) : TextRoundedBgRenderer(horizontalPadding, verticalPadding) {

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

abstract class BaseRendererDelegate(val singleRenderer: SingleLineRenderer, val multiLineRenderer: MultiLineRenderer)
    : com.valerasetrakov.lib.RendererDelegate {

    override fun draw(canvas: Canvas, layout: Layout, startLine: Int, endLine: Int, startOffset: Int, endOffset: Int) {
        val renderer = if (startLine == endLine) singleRenderer else multiLineRenderer
        renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
    }
}

class DoubleRendererDelegate(singleRenderer: DoubleSingleLineRenderer, multiLineRenderer: DoubleMultiLineRenderer) :
    StyleRendererDelegate(singleRenderer, multiLineRenderer) {

    constructor(horizontalPadding: Int = 0, verticalPadding: Int = 0, context: Context):
            this(DoubleSingleLineRenderer(horizontalPadding, verticalPadding, context),
                DoubleMultiLineRenderer(horizontalPadding, verticalPadding, context))

    companion object {
        const val MARKED_FAILED_VALUE = "MARKED_FAILED_VALUE"
        fun createAnnotation() =
            Annotation(STYLE_ANNOTATION_KEY, MARKED_FAILED_VALUE)
    }

    override fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean {
        if (super.isValidAnnotation(annotation, flags)) {
            val annotationValue = annotation.value
            if (annotationValue == MARKED_FAILED_VALUE)
                return true
        }
        return false
    }
}

class DoubleSingleLineRenderer(horizontalPadding: Int, verticalPadding: Int, context: Context) :
    SingleLineRenderer(horizontalPadding, verticalPadding, DoubleDrawable(FailDrawable(context), MarkedDrawable(context)))

class DoubleMultiLineRenderer(
    horizontalPadding: Int,
    verticalPadding: Int,
    context: Context
) : MultiLineRenderer(horizontalPadding, verticalPadding,
    DoubleDrawable(LeftFailDrawable(context), LeftMarkedDrawable(context)),
    DoubleDrawable(MiddleFailDrawable(context), MiddleMarkedDrawable(context)),
    DoubleDrawable(RightFailDrawable(context), RightMarkedDrawable(context))
)

