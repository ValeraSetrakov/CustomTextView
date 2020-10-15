package com.valerasetrakov.customtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.text.Annotation
import android.text.Layout
import android.text.Spannable
import androidx.core.content.res.ResourcesCompat
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

class MarkedDrawable(context: Context) :
    BaseMarkedDrawable(context,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span)

open class BaseMarkedDrawable(context: Context, leftTopRoundId: Int, rightTopRoundId: Int, rightBottomRoundId: Int, leftBottomRoundId: Int):
    BaseRoundedShapeDrawable(context, R.color.colorOfMarkedPhrase, leftTopRoundId, rightTopRoundId, rightBottomRoundId, leftBottomRoundId)

open class BaseRoundedShapeDrawable(context: Context, colorId: Int, leftTopRoundId: Int, rightTopRoundId: Int, rightBottomRoundId: Int, leftBottomRoundId: Int):
    PaintDrawable(ResourcesCompat.getColor(context.resources, colorId, context.theme)) {

    private val leftTopRound = context.resources.getDimension(leftTopRoundId)
    private val rightTopRound = context.resources.getDimension(rightTopRoundId)
    private val rightBottomRound = context.resources.getDimension(rightBottomRoundId)
    private val leftBottomRound = context.resources.getDimension(leftBottomRoundId)

    private val rounds = floatArrayOf(
        leftTopRound, leftTopRound,
        rightTopRound, rightTopRound,
        rightBottomRound, rightBottomRound,
        leftBottomRound, leftBottomRound
    )

    init {
        setCornerRadii(rounds)
    }
}

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

// Extension functions for Layout object

/**
 * Android system default line spacing extra
 */
private const val DEFAULT_LINESPACING_EXTRA = 0f

/**
 * Android system default line spacing multiplier
 */
private const val DEFAULT_LINESPACING_MULTIPLIER = 1f

/**
 * Get the line bottom discarding the line spacing added.
 */
fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val lastLineSpacingNotAdded = Build.VERSION.SDK_INT >= 19
    val isLastLine = line == lineCount - 1

    val lineBottomWithoutSpacing: Int
    val lineSpacingExtra = spacingAdd
    val lineSpacingMultiplier = spacingMultiplier
    val hasLineSpacing = lineSpacingExtra != DEFAULT_LINESPACING_EXTRA
            || lineSpacingMultiplier != DEFAULT_LINESPACING_MULTIPLIER

    if (!hasLineSpacing || isLastLine && lastLineSpacingNotAdded) {
        lineBottomWithoutSpacing = lineBottom
    } else {
        val extra: Float
        if (lineSpacingMultiplier.compareTo(DEFAULT_LINESPACING_MULTIPLIER) != 0) {
            val lineHeight = getLineHeight(line)
            extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier
        } else {
            extra = lineSpacingExtra
        }

        lineBottomWithoutSpacing = (lineBottom - extra).toInt()
    }

    return lineBottomWithoutSpacing
}

/**
 * Get the line height of a line.
 */
fun Layout.getLineHeight(line: Int): Int {
    return getLineTop(line + 1) - getLineTop(line)
}

/**
 * Returns the top of the Layout after removing the extra padding applied by  the Layout.
 */
fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

/**
 * Returns the bottom of the Layout after removing the extra padding applied by the Layout.
 */
fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount - 1) {
        lineBottom -= bottomPadding
    }
    return lineBottom
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
    : RendererDelegate {

    override fun draw(canvas: Canvas, layout: Layout, startLine: Int, endLine: Int, startOffset: Int, endOffset: Int) {
        val renderer = if (startLine == endLine) singleRenderer else multiLineRenderer
        renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
    }
}

class LeftMarkedDrawable(context: Context): BaseMarkedDrawable(context,
    R.dimen.rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.rounded_of_background_of_lesson_span)

class MiddleMarkedDrawable(context: Context):
    BaseMarkedDrawable(context,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span)

class RightMarkedDrawable(context: Context):
    BaseMarkedDrawable(context,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span
    )

fun Spannable.setSpanInclusiveEnd(what: Any, start: Int, end: Int) {
    setSpan(what, start, end + 1)
}

fun Spannable.setSpan(what: Any, start: Int, end: Int) {
    setSpan(what, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
}

class DoubleRendererDelegate(singleRenderer: DoubleSingleLineRenderer, multiLineRenderer: DoubleMultiLineRenderer) :
    StyleRendererDelegate(singleRenderer, multiLineRenderer) {

    constructor(horizontalPadding: Int = 0, verticalPadding: Int = 0, context: Context):
            this(DoubleSingleLineRenderer(horizontalPadding, verticalPadding, context),
                DoubleMultiLineRenderer(horizontalPadding, verticalPadding, context))

    companion object {
        const val MARKED_FAILED_VALUE = "MARKED_FAILED_VALUE"
        fun createAnnotation() =
            Annotation(StyleRendererDelegate.STYLE_ANNOTATION_KEY, MARKED_FAILED_VALUE)
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

class DoubleDrawable(private val firstDrawable: Drawable, private val secondDrawable: Drawable): Drawable() {


    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        val leftFailDrawable = left + 4
        val topFailDrawable = top - 4
        val rightFailDrawable = right + 4
        val bottomFailDrawable = bottom - 4
        firstDrawable.setBounds(leftFailDrawable, topFailDrawable, rightFailDrawable, bottomFailDrawable)

        secondDrawable.setBounds(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        firstDrawable.draw(canvas)
        secondDrawable.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        firstDrawable.alpha = alpha
        secondDrawable.alpha = alpha
    }

    override fun getOpacity(): Int {
        return firstDrawable.opacity
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        firstDrawable.colorFilter = colorFilter
        secondDrawable.colorFilter = colorFilter
    }
}

class DoubleMultiLineRenderer(
    horizontalPadding: Int,
    verticalPadding: Int,
    context: Context
) : MultiLineRenderer(horizontalPadding, verticalPadding,
    DoubleDrawable(LeftFailDrawable(context), LeftMarkedDrawable(context)),
    DoubleDrawable(MiddleFailDrawable(context), MiddleMarkedDrawable(context)),
    DoubleDrawable(RightFailDrawable(context), RightMarkedDrawable(context)))

class LeftFailDrawable(context: Context):
    BaseFailDrawable(context,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span)

open class BaseFailDrawable(context: Context, leftRoundId: Int, topRoundId: Int, rightRoundId: Int, bottomRoundId: Int):
    BaseRoundedShapeDrawable(context, R.color.colorOfFailedPhrase, leftRoundId, topRoundId, rightRoundId, bottomRoundId)

class FailDrawable(context: Context) :
    BaseFailDrawable(context,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span)

class MiddleFailDrawable(context: Context): BaseFailDrawable(context,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span)

class RightFailDrawable(context: Context): BaseFailDrawable(context,
    R.dimen.zero_rounded_of_background_of_lesson_span,
    R.dimen.rounded_of_background_of_lesson_span,
    R.dimen.rounded_of_background_of_lesson_span,
    R.dimen.zero_rounded_of_background_of_lesson_span
)