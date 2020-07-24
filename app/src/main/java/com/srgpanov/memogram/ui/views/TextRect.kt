package com.srgpanov.memogram.ui.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.graphics.withClip
import androidx.core.graphics.withRotation
import com.srgpanov.memogram.other.dpToPx


class TextRect(override val id:Long, _rectF: RectF, viewWidth:Int) : DrawingRect(_rectF,viewWidth) {
    var text = "Hi there, I'm a \nblue bubble.    " +
            "Me too!    " +
            "There are a lot\n of bubbles around here. And all of them are blue.    " +
            "And now for something completely different. According to Wikipedia, the origin of this phrase \"is credited " +
            "to Christopher Trace, founding presenter of the children's television programme Blue Peter, who used it (in all seriousness) " +
            "as a link between segments\". " +
            "Interesting, isn't it?    " +
            "Lorem ipsum is so boring.    " +
            "Draw text in a given rectangle and automatically wrap lines.    " +
            "Don't forget to rotate your device."
        set(value) {
            field = value
            refreshText()
        }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        this.color = Color.BLACK
    }

    @ColorInt
    var textColor = Color.BLACK
        set(value) {
            field = value
            textPaint.color = value
        }
    var textHeight = 50f
        set(value) {
            field = value
            textPaint.textSize = value
        }
    var textAlignment: Paint.Align = Paint.Align.LEFT
        set(value) {
            field = value
            textPaint.textAlign = value
        }
    private val maxLines: Int
        get() {
            return (drawRect.height() / textHeight).toInt()
        }
    private var diffX = 0f
    private var diffY = 0f

    private val minSize = dpToPx(16)

    private val textLines = splitTextToLines()
    private var lineCounter = 1


    override fun changeDimensions(touchX: Float, touchY: Float) {
        pointF.x = touchX
        pointF.y = touchY
        pointF = invert(pointF)
        diffX = pointF.x - actualRect.centerX() - rectTouchOffsetX
        diffY = pointF.y - actualRect.centerY() - rectTouchOffsetY
        if (diffX >= minSize) {
            actualRect.right = actualRect.centerX() + diffX
            actualRect.left = actualRect.centerX() - diffX

        }
        if (diffY >= minSize) {
            actualRect.bottom = actualRect.centerY() + diffY
            actualRect.top = actualRect.centerY() - diffY
        }
        refreshText()
    }

    private fun refreshText() {
        textLines.clear()
        textLines.addAll(splitTextToLines())
    }


    override fun draw(canvas: Canvas) {
        Log.d("TextRect", "draw: $drawRect")
        canvas.withRotation(rotation, center.x, center.y) {
            canvas.withClip(drawRect) {
                canvas.drawColor(Color.MAGENTA)
                for ((index, line) in textLines.withIndex()) {
                    if (index >= maxLines) break
                    canvas.drawText(
                        line,
                        drawRect.left,
                        drawRect.top + (textPaint.textSize * lineCounter),
                        textPaint
                    )
                    Log.d("TextRect", "draw: $line")
                    lineCounter++
                }
                lineCounter = 1
            }
        }
    }


    private fun splitTextToLines(): MutableList<String> {
        val list = mutableListOf<String>()
        var lineLength: Int
        var str = text
        while (true) {
            lineLength = str.lineLength()
            val stringLine = str.substring(0, lineLength)
            val indexLastSpace = stringLine.lastIndexOf(' ')
            val indexesLineBreaks = stringLine.indexOfFirst { it == '\n' }
            val lineWithoutBreaks = indexLastSpace == -1 && indexesLineBreaks == -1
            val line = if (lineWithoutBreaks) {
                stringLine
            } else {
                val indexLastBreaks =
                    if (indexesLineBreaks != -1) indexesLineBreaks + 1 else indexLastSpace + 1
                val endLineIndex = if (stringLine == str) stringLine.length else indexLastBreaks
                str.substring(0, endLineIndex)
            }
            list += line
            str = str.substring(line.length).trimStart()
            if (str.isEmpty()) break
        }
        return list
    }


    private fun String.lineLength(): Int {
        return textPaint.breakText(this, true, drawRect.width(), null)
    }

}