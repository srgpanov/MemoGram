package com.srgpanov.memogram.ui.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint

class TextRect(
    val bitmap: Bitmap
) : DrawingRect(bitmap,bitmap.width,bitmap.height) {
    var text = "Some long text for my view"
    val canvas = Canvas(bitmap)
    val textPaint =TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.BLACK
        this.textSize = 50f
    }


    fun drawText(){
        canvas.drawText(text,0f,canvas.height.toFloat(),textPaint)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}