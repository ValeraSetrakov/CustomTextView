package com.valerasetrakov.lib

import android.graphics.Canvas
import android.text.Annotation
import android.text.Layout
import android.text.Spanned

/**
 * Compositor for [RendererDelegate]
 */
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