package com.valerasetrakov.customtextview.example.drawable.round

import android.content.Context
import android.graphics.drawable.PaintDrawable
import androidx.core.content.res.ResourcesCompat

/**
 * Rounded shape drawable
 */
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