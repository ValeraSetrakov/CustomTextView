package com.valerasetrakov.customtextview.example.drawable.round.implementation.fail

import android.content.Context
import com.valerasetrakov.customtextview.R
import com.valerasetrakov.customtextview.example.drawable.round.BaseRoundedShapeDrawable

open class BaseFailDrawable(context: Context, leftRoundId: Int, topRoundId: Int, rightRoundId: Int, bottomRoundId: Int):
    BaseRoundedShapeDrawable(context,
        R.color.colorOfFailedPhrase, leftRoundId, topRoundId, rightRoundId, bottomRoundId)