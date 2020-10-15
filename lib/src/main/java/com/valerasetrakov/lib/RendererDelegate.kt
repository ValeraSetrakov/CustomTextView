package com.valerasetrakov.lib

import com.valerasetrakov.lib.renderer.Renderer

/**
 * Determines if the annotation needs to be processed and if so than render it
 */
interface RendererDelegate: Renderer, AnnotationDelegate