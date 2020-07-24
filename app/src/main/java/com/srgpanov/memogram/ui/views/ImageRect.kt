package com.srgpanov.memogram.ui.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.withRotation
import com.srgpanov.memogram.other.dpToPx
import kotlin.math.pow
import kotlin.math.sqrt

class ImageRect private constructor(override val id:Long, private val bitmap: Bitmap, rectF: RectF, viewWidth: Int) :
    DrawingRect(rectF, viewWidth) {
    private var diffX = 0f
    private var diffY = 0f
    private var _width = 0f
    private var _height = 0f
    private var minSize = dpToPx(16)
    private val tg = drawRect.height() / drawRect.width()
    private val rectHypotenuse =
        sqrt(drawRect.width().toDouble().pow(2.0) + drawRect.height().toDouble().pow(2.0))
    private val sinA = drawRect.width() / rectHypotenuse


    companion object {
        private val startSize = dpToPx(100).toFloat()

        fun create(id: Long,bitmap: Bitmap, viewWidth: Int, viewHeight: Int): ImageRect {
            Log.d("ImageRect", "create: ${bitmap.height} ${bitmap.width}")
            return ImageRect(id,bitmap, getBitmapRect(bitmap, viewWidth, viewHeight),viewWidth)
        }

        private fun getBitmapRect(bitmap: Bitmap, viewWidth: Int, viewHeight: Int): RectF {
            val viewCenter = PointF(viewWidth / 2f, viewHeight / 2f)
            return if (bitmap.width > bitmap.height) {
                val ratio = bitmap.width.toFloat() / bitmap.height
                RectF(0f, 0f, startSize, startSize / ratio).centerToPoint(viewCenter)
            } else {
                val ratio = bitmap.height.toFloat() / bitmap.width
                RectF(0f, 0f, startSize / ratio, startSize).centerToPoint(viewCenter)
            }

        }
    }

    override fun changeDimensions(touchX: Float, touchY: Float) {
        pointF.x = touchX
        pointF.y = touchY
        pointF = invert(pointF)
        diffX = pointF.x - actualRect.centerX() - rectTouchOffsetX
        diffY = pointF.y - actualRect.centerY() - rectTouchOffsetY
        val distanceFromCenter =
            sqrt(diffX.toDouble().pow(2.0) + diffY.toDouble().pow(2.0)).toFloat()

        _width = (distanceFromCenter * sinA).toFloat()
        _height = _width * tg
        val rectLargerThanMinSize = !(_width < minSize || (_height < minSize))
        if (rectLargerThanMinSize) {
            actualRect.right = actualRect.centerX() + _width
            actualRect.left = actualRect.centerX() - _width
            actualRect.bottom = actualRect.centerY() + _height
            actualRect.top = actualRect.centerY() - _height
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.withRotation(rotation, center.x, center.y) {
            canvas.drawBitmap(bitmap, null, drawRect, null)
        }
    }
}