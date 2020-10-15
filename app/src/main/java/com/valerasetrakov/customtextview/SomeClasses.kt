package com.valerasetrakov.customtextview

import android.content.Context
import android.graphics.Canvas
import android.text.Annotation
import android.text.Layout
import com.valerasetrakov.customtextview.example.drawable.doubledrawable.DoubleDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.LeftFailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.LeftMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.MarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.MiddleMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.marked.RightMarkedDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.FailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.MiddleFailDrawable
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.RightFailDrawable
import com.valerasetrakov.lib.RendererDelegate
import com.valerasetrakov.lib.renderer.MultiLineRenderer
import com.valerasetrakov.lib.renderer.SingleLineRenderer

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

abstract class BaseRendererDelegate(val singleRenderer: SingleLineRenderer, val multiLineRenderer: MultiLineRenderer)
    : RendererDelegate {

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

