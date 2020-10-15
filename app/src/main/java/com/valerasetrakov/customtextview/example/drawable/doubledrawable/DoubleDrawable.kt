package com.valerasetrakov.customtextview.example.drawable.doubledrawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable

/**
 * Drawable for drawing double background
 */
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