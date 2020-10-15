package com.valerasetrakov.customtextview.example.drawable.round.implementation.fail

import android.content.Context
import com.valerasetrakov.customtextview.R
import com.valerasetrakov.customtextview.example.drawable.round.implementation.fail.BaseFailDrawable

class LeftFailDrawable(context: Context):
    BaseFailDrawable(context,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span,
        R.dimen.rounded_of_background_of_lesson_span,
        R.dimen.zero_rounded_of_background_of_lesson_span
    )