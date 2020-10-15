package com.valerasetrakov.customtextview

import android.content.Context
import android.graphics.Canvas
import android.text.Annotation
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation

open class RendererTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val horizontalPadding = 1

    private val rendererDelegateManager = RendererDelegateManager()

    protected val annotations: Array<Annotation>
        get() = spannableText.getSpans(0, text.length, Annotation::class.java)

    protected val spannableText: Spannable
        get() = text as Spannable

    fun addRendererDelegates(delegates: List<RendererDelegate>) {
        rendererDelegateManager.addDelegates(delegates)
    }

    fun addRendererDelegate(delegate: RendererDelegate) {
        rendererDelegateManager.addDelegate(delegate)
    }

    override fun onDraw(canvas: Canvas?) {
        if (text is Spanned && layout != null) {
            canvas?.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }

    private fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        drawAnnotations(annotations, canvas, text, layout)
    }

    /** Draw style annotations array, as background style */
    protected open fun drawAnnotations(annotations: Array<Annotation>, canvas: Canvas, text: Spanned, layout: Layout) {
        annotations.forEach { annotation ->
            drawAnnotation(annotation, canvas, text, layout)
        }
    }

    /** Determine type of annotation and draw it */
    private fun drawAnnotation(annotation: Annotation, canvas: Canvas, text: Spanned, layout: Layout) {
        val annotationMeasure = measureAnnotation(annotation, text, layout)

        val startLine = annotationMeasure.startLine
        val endLine = annotationMeasure.endLine
        val startOffset = annotationMeasure.startOffset
        val endOffset = annotationMeasure.endOffset

        rendererDelegateManager.draw(annotation, canvas, layout, text, startLine, endLine, startOffset, endOffset)
    }

    /** Measure start, end lines and start, end offsets for background annotation */
    private fun measureAnnotation (annotation: Annotation, text: Spanned, layout: Layout): BackgroundAnnotationMeasure {
        val spanStart = text.getSpanStart(annotation)
        val spanEnd = text.getSpanEnd(annotation)
        val startLine = layout.getLineForOffset(spanStart)
        val endLine = layout.getLineForOffset(spanEnd)
        // start can be on the left or on the right depending on the language direction.
        val startOffset = (layout.getPrimaryHorizontal(spanStart)
                + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
        // end can be on the left or on the right depending on the language direction.
        val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

        return BackgroundAnnotationMeasure(
            startLine = startLine,
            endLine = endLine,
            startOffset = startOffset,
            endOffset = endOffset
        )
    }

    fun setRendererText(text: CharSequence) {
        setText(text, BufferType.SPANNABLE)
    }

    data class BackgroundAnnotationMeasure (
        val startLine: Int,
        val endLine: Int,
        val startOffset: Int,
        val endOffset: Int
    )

}

class RendererDelegateManager(private val renderDelegates: MutableList<RendererDelegate> = mutableListOf()) {

    fun addDelegates(delegates: List<RendererDelegate>) {
        renderDelegates.addAll(delegates)
    }

    fun addDelegate(delegate: RendererDelegate) {
        renderDelegates.add(delegate)
    }

    fun draw(annotation: Annotation, canvas: Canvas, layout: Layout, text: Spanned,
             startLine: Int, endLine: Int, startOffset: Int, endOffset: Int) {
        val annotationFlags = text.getSpanFlags(annotation)
        val validRenderDelegates = renderDelegates.filter { it.isValidAnnotation(annotation, annotationFlags) }
        validRenderDelegates.forEach { it.draw(canvas, layout, startLine, endLine, startOffset, endOffset) }
    }
}

interface RendererDelegate: Renderer, AnnotationDelegate

interface Renderer {
    /**
     * Draw the background that starts at the {@code startOffset} and ends at {@code endOffset}.
     *
     * @param canvas Canvas to draw onto
     * @param layout Layout that contains the text
     * @param startLine the start line for the background
     * @param endLine the end line for the background
     * @param startOffset the character offset that the background should start at
     * @param endOffset the character offset that the background should end at
     */
    fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    )
}

interface AnnotationDelegate {
    fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean
}