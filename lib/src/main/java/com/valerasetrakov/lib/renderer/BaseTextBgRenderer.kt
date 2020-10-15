package com.valerasetrakov.lib.renderer

import android.text.Layout
import com.valerasetrakov.lib.getLineBottomWithoutPadding
import com.valerasetrakov.lib.getLineTopWithoutPadding
import com.valerasetrakov.lib.renderer.Renderer

/**
 * Base class for single and multi line rounded background renderers.
 *
 * @param horizontalPadding the padding to be applied to left & right of the background
 * @param verticalPadding the padding to be applied to top & bottom of the background
 */
abstract class BaseTextBgRenderer(
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