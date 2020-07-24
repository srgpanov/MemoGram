package com.srgpanov.memogram.ui.views

import android.graphics.*
import android.graphics.Path.Direction.CW
import com.srgpanov.memogram.other.dpToPx

abstract class DrawingRect(
    protected val actualRect: RectF,
    private val viewWidth: Int
) {
    val drawRect: RectF
        get() = if (isSnappingToCenter) getSnappedCenter(actualRect) else actualRect

    abstract val id:Long

    private var _rotation = 0f

    var isSnappingRotation = true
    private val snapAngle = 5f
    val rotation: Float
        get() = computeRotation()

    var isSnappingToCenter = true
    var drawCenterLine: Boolean = false
    private val snapCenterDistanceX = dpToPx(10)

    //region rectKeyPoints
    val center: PointF = PointF()
        get() {
            field.x = drawRect.centerX()
            field.y = drawRect.centerY()
            return field
        }

    val leftTop: PointF
        get() = getVisiblePoint(PointF(drawRect.left, drawRect.top))

    val rightIop: PointF
        get() = getVisiblePoint(PointF(drawRect.right, drawRect.top))

    val rightBottom: PointF
        get() = getVisiblePoint(PointF(drawRect.right, drawRect.bottom))
    //endregion

    protected var rectTouchOffsetX = 0f
    protected var rectTouchOffsetY = 0f
    private var viewMatrix = Matrix()
        get() {
            field.setRotate(rotation, center.x, center.y)
            return field
        }
    private var invertMatrix = Matrix()
    protected var pointF = PointF()
    private var path = Path()
    private val floatArray = FloatArray(2)
    private val snappedRect = RectF()

    abstract fun draw(canvas: Canvas)


    abstract fun changeDimensions(touchX: Float, touchY: Float)

    fun getPath(): Path {
        path.reset()
        path.addRect(actualRect, CW)
        path.transform(viewMatrix)
        return path
    }

    fun contains(pointF: PointF): Boolean {
        this.pointF = invert(pointF)
        return actualRect.contains(pointF.x, pointF.y)
    }

    fun offset(deltaX: Float, deltaY: Float) {
        actualRect.offset(deltaX, deltaY)
    }

    fun addRotation(angle: Float) {
        _rotation += angle
    }

    fun setTouchOffset(touchX: Float, touchY: Float) {
        pointF.x = touchX
        pointF.y = touchY
        pointF = invert(pointF)
        rectTouchOffsetX = pointF.x - actualRect.right
        rectTouchOffsetY = pointF.y - actualRect.bottom
    }

    protected fun invert(pointF: PointF): PointF {
        invertMatrix.set(viewMatrix)
        invertMatrix.invert(invertMatrix)
        floatArray[0] = pointF.x
        floatArray[1] = pointF.y
        invertMatrix.mapPoints(floatArray)
        pointF.x = floatArray[0]
        pointF.y = floatArray[1]
        return pointF
    }


    private fun getVisiblePoint(pointF: PointF): PointF {
        floatArray[0] = pointF.x
        floatArray[1] = pointF.y
        viewMatrix.mapPoints(floatArray)
        pointF.x = floatArray[0]
        pointF.y = floatArray[1]
        return pointF
    }


    private fun getSnappedCenter(rect: RectF): RectF {
        val centerView = viewWidth / 2f
        val min = centerView - snapCenterDistanceX
        val max = centerView + snapCenterDistanceX
        return if (rect.centerX() in min..max) {
            drawCenterLine = true
            snappedRect.set(rect)
            snappedRect.centerToPoint(PointF(centerView, rect.centerY()))
        } else {
            drawCenterLine = false
            rect
        }
    }

    private fun computeRotation(): Float {
        return if (isSnappingRotation) {
            getSnappedRotation()
        } else {
            _rotation
        }

    }

    private fun getSnappedRotation(): Float {
        val angleDiff90 = (_rotation + 360) % 90
        return when (angleDiff90) {
            in 0f.rangeTo(snapAngle) -> {
                _rotation - angleDiff90
            }
            in (90f - snapAngle).rangeTo(90f) -> {
                _rotation + (90 - angleDiff90)
            }
            else -> _rotation
        }
    }
}
