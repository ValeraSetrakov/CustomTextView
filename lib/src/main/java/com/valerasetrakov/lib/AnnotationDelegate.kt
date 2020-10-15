package com.valerasetrakov.lib

import android.text.Annotation

interface AnnotationDelegate {
    fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean
}