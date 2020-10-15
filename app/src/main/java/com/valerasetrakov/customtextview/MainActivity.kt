package com.valerasetrakov.customtextview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val annotation = DoubleRendererDelegate.createAnnotation()
        val someText = SpannableStringBuilder.valueOf("Some text\nSome text 2")
        someText.setSpanInclusiveEnd(annotation, 0, 13)
        text_view.addRendererDelegate(MarkedRendererDelegate(context = this))
        text_view.addRendererDelegate(DoubleRendererDelegate(context = this))
        val standardSpan = ForegroundColorSpan(Color.BLUE)
        someText.setSpanInclusiveEnd(standardSpan, 0, 5)
        text_view.setText(someText)
    }
}