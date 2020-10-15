package com.valerasetrakov.customtextview.example.drawable.round

import android.content.Context
import com.valerasetrakov.customtextview.R

open class BaseMarkedDrawable(context: Context, leftTopRoundId: Int, rightTopRoundId: Int, rightBottomRoundId: Int, leftBottomRoundId: Int):
    BaseRoundedShapeDrawable(context,
        R.color.colorOfMarkedPhrase, leftTopRoundId, rightTopRoundId, rightBottomRoundId, leftBottomRoundId)