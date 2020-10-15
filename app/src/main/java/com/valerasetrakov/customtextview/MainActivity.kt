package com.valerasetrakov.customtextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val annotation = DoubleRendererDelegate.createAnnotation()
        val someText = SpannableStringBuilder.valueOf("Some text")
        someText.setSpanInclusiveEnd(annotation, 0, 2)
        text_view.addRendererDelegate(MarkedRendererDelegate(context = this))
        text_view.addRendererDelegate(DoubleRendererDelegate(context = this))

        text_view.setRendererText(someText)
    }
}