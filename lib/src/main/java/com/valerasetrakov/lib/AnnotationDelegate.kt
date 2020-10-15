package com.valerasetrakov.lib

import android.text.Annotation

/**
 * Determines if the annotation needs to be processed
 */
interface AnnotationDelegate {
    fun isValidAnnotation(annotation: Annotation, flags: Int): Boolean
}