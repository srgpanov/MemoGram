package com.srgpanov.memogram.ui.views

import android.graphics.*
import android.graphics.Path.Direction.CW
import android.util.Log
import androidx.core.graphics.withRotation
import com.srgpanov.memogram.other.dpToPx
import kotlin.math.pow
import kotlin.math.sqrt

open class DrawingRect(
    drawBitmap: Bitmap,
    widthView: Int,
    heightView: Int
) {
    private val bitmap:Bitmap = drawBitmap
    val rectF = RedactorContainer.getBitmapRect(bitmap,widthView,heightView)
    var rotation = 0f
    var center: PointF
        get() {
            field.x = rectF.centerX()
            field.y = rectF.centerY()
            return field
        }
    var leftTop: PointF
        get() {
            field.x = rectF.left
            field.y = rectF.top
            return getVisiblePoint(field)
        }
    var rightIop: PointF
        get() {
            field.x = rectF.right
            field.y = rectF.top
            return getVisiblePoint(field)
        }
    var leftBottom: PointF
        get() {
            field.x = rectF.left
            field.y = rectF.bottom
            return getVisiblePoint(field)
        }
    var rightBottom: PointF
        get() {
            field.x = rectF.right
            field.y = rectF.bottom
            return getVisiblePoint(field)
        }
    var minSize = dpToPx(16)
    var viewMatrix = Matrix()
        get() {
            field.setRotate(rotation, center.x, center.y)
            return field
        }
        private set
    private var invertMatrix = Matrix()
    private var pointF = PointF()
    private var path = Path()
    private val floatArray = FloatArray(2)
    private var rectTouchOffsetX = 0f
    private var rectTouchOffsetY = 0f
    private var diffX = 0f
    private var diffY = 0f
    private val tg = rectF.height() / rectF.width()
    private val rectHypotenuse =
        sqrt(rectF.width().toDouble().pow(2.0) + rectF.height().toDouble().pow(2.0))
    private val sinA = rectF.width() / rectHypotenuse
    private var _width = 0f
    private var _height = 0f

    init {
        center = PointF(rectF.centerX(), rectF.centerY())
        leftTop = PointF(rectF.left, rectF.top)
        rightIop = PointF(rectF.right, rectF.top)
        leftBottom = PointF(rectF.left, rectF.bottom)
        rightBottom = PointF(rectF.right, rectF.bottom)
        rectF.centerX()
        Log.d("RectMatrix", "sinA $sinA ")
    }


    fun getPath(): Path {
        path.reset()
        path.addRect(rectF, CW)
        path.transform(viewMatrix)
        return path
    }

    fun contains(pointF: PointF): Boolean {
        this.pointF = invert(pointF)
        return rectF.contains(pointF.x, pointF.y)
    }

    fun offset(deltaX: Float, deltaY: Float) {
        rectF.offset(deltaX, deltaY)
    }
    open fun draw ( canvas: Canvas){
        canvas.withRotation(rotation, center.x, center.y) {
            canvas.drawBitmap(bitmap, null, rectF, null)
        }
    }


    fun changeDimensions(touchX: Float, touchY: Float) {
        pointF.x = touchX
        pointF.y = touchY
        pointF = invert(pointF)
        diffX = pointF.x - rectF.centerX()-rectTouchOffsetX
        diffY = pointF.y - rectF.centerY()-rectTouchOffsetY
        val distanceFromCenter =
            sqrt(diffX.toDouble().pow(2.0) + diffY.toDouble().pow(2.0)).toFloat()

        _width = (distanceFromCenter * sinA).toFloat()
        _height = _width * tg
        if (!(_width< minSize || (_height< minSize))){
            rectF.right = rectF.centerX() + _width
            rectF.left = rectF.centerX() - _width
            rectF.bottom = rectF.centerY() + _height
            rectF.top = rectF.centerY() - _height
        }
    }

    fun setTouchOffset(touchX: Float, touchY: Float) {
        pointF.x = touchX
        pointF.y = touchY
        pointF = invert(pointF)
        rectTouchOffsetX = pointF.x - rectF.right
        rectTouchOffsetY = pointF.y - rectF.bottom
    }

    private fun getVisiblePoint(pointF: PointF): PointF {
        floatArray[0] = pointF.x
        floatArray[1] = pointF.y
        viewMatrix.mapPoints(floatArray)
        pointF.x = floatArray[0]
        pointF.y = floatArray[1]
        return pointF
    }

    private fun invert(pointF: PointF): PointF {
        invertMatrix.set(viewMatrix)
        invertMatrix.invert(invertMatrix)
        floatArray[0] = pointF.x
        floatArray[1] = pointF.y
        invertMatrix.mapPoints(floatArray)
        pointF.x = floatArray[0]
        pointF.y = floatArray[1]
        return pointF
    }
}