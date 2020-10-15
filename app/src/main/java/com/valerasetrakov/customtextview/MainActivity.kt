package com.valerasetrakov.customtextview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val annotation = DoubleRendererDelegate.createAnnotation()
        val markedAnnotation = MarkedRendererDelegate.createAnnotation()
        val someText = SpannableStringBuilder.valueOf("Some text\nSome text 2")
        someText.setSpanInclusiveEnd(annotation, 0, 13)
        someText.setSpanInclusiveEnd(markedAnnotation, 0, 5)
        text_view.addRendererDelegate(MarkedRendererDelegate(context = this))
        text_view.addRendererDelegate(DoubleRendererDelegate(context = this))
        val standardSpan = ForegroundColorSpan(Color.BLUE)
        someText.setSpanInclusiveEnd(standardSpan, 0, 5)

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.BLUE, Color.GRAY)
        )
        val singleRenderer =
            SingleLineRenderer(
                0,
                0,
                gradientDrawable
            )

        val multiLineRenderer = MultiLineRenderer(
            0,
            0,
            gradientDrawable,
            gradientDrawable,
            gradientDrawable
        )
        val styleLineRendererDelegate = object: StyleRendererDelegate(
            singleRenderer = singleRenderer,
            multiLineRenderer = multiLineRenderer
        ) {
            override fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean {
                return if (super.isValidAnnotation(annotation, flags)) {
                    val annotationValue = annotation.value
                    annotationValue == "CUSTOM_ANNOTATION_VALUE"
                } else {
                    false
                }
            }
        }

        text_view.addRendererDelegate(styleLineRendererDelegate)

        val gradientAnnotation = Annotation(
            StyleRendererDelegate.STYLE_ANNOTATION_KEY,
            "CUSTOM_ANNOTATION_VALUE"
        )

        someText.setSpanInclusiveEnd(gradientAnnotation, 0, 13)

        text_view.text = someText
    }
}