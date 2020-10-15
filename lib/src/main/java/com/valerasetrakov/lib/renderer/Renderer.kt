package com.valerasetrakov.lib.renderer

import android.graphics.Canvas
import android.text.Layout

/**
 * Class for rendering background for [AppCompatTextView]
 */
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