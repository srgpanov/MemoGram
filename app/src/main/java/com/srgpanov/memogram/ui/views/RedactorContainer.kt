package com.srgpanov.memogram.ui.views

import android.graphics.*
import android.graphics.Path.Direction.CW
import android.view.MotionEvent
import androidx.core.graphics.toPoint
import androidx.core.graphics.withRotation
import com.srgpanov.memogram.App
import com.srgpanov.memogram.R
import com.srgpanov.memogram.other.dpToPx
import com.srgpanov.memogram.other.toPoint
import com.srgpanov.memogram.other.toPointF

import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt


class RedactorContainer(
    bitmap: Bitmap,
    widthView: Int,
    heightView: Int
) {
    var drawingRect= DrawingRect(bitmap,widthView,heightView)
    var drawnState = false
    var removeFlag = false

    private var touchX = 0f
    private var touchY = 0f
    private var deltaX = 0f
    private var deltaY = 0f

    private var oldX = 0f
    private var oldY = 0f

    private var rotateAngle = 0f
    private var startAngle = 0f

    private var touchState = TouchState.MOVE

    private val blueColor = App.instance.resources.getColor(R.color.primary)
    private val whiteColor = App.instance.resources.getColor(R.color.white)

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blueStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whitePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val magentaPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pathCircle = Path()
    private val pathRect = Path()
    private val removeDrawable = App.instance.getDrawable(R.drawable.ic_cross)
    private val rotateDrawable = App.instance.getDrawable(R.drawable.ic_rotate)
    private val resizeDrawable = App.instance.getDrawable(R.drawable.ic_duplex_arrow)
    private val pathDotted = Path()
    private var iconRadius = dpToPx(12)
    private val iconMatrix = Matrix()
    private val borderOffset = dpToPx(2).toFloat()
    private val iconSize = dpToPx(12)


    companion object {
        fun getBitmapRect(bitmap: Bitmap, width: Int, height: Int): RectF {
            val left = width / 2f - bitmap.width / 2
            val top = height / 2f - bitmap.height / 2
            val right = left + bitmap.width
            val bottom = top + bitmap.height
            return RectF(left, top, right, bottom)
        }
    }

    init {
        greenPaint.color = Color.GREEN
        greenPaint.style = Paint.Style.FILL
        bluePaint.color = blueColor
        bluePaint.style = Paint.Style.FILL
        blueStrokePaint.color = blueColor
        blueStrokePaint.style = Paint.Style.STROKE
        blueStrokePaint.strokeWidth = dpToPx(3).toFloat()
        blueStrokePaint.pathEffect = DashPathEffect(
            floatArrayOf(dpToPx(10).toFloat(), dpToPx(5).toFloat()), 0f
        )
        whitePaint.color = Color.WHITE
        whitePaint.style = Paint.Style.STROKE
        whitePaint.strokeWidth = dpToPx(2) * 0.75f
        setupDottedPath()
        updatePaths()
    }


    fun draw(canvas: Canvas) {
        drawingRect.draw(canvas)
        if (!drawnState) {
            canvas.drawPath(pathDotted, blueStrokePaint)
            canvas.drawPath(pathCircle, bluePaint)
            canvas.withRotation(drawingRect.rotation,drawingRect.leftTop.x,drawingRect.leftTop.y) {
                removeDrawable?.draw(canvas)
            }
            canvas.withRotation(drawingRect.rotation,drawingRect.rightIop.x,drawingRect.rightIop.y) {
                rotateDrawable?.draw(canvas)
            }
            canvas.withRotation(drawingRect.rotation,drawingRect.rightBottom.x,drawingRect.rightBottom.y) {
                resizeDrawable?.draw(canvas)
            }
        }
    }

    fun handleMotionEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        deltaX = touchX - oldX
        deltaY = touchY - oldY
        oldX = touchX
        oldY = touchY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when {
                    isInsideArc(event.toPoint(), drawingRect.rightIop.toPoint()) && !drawnState -> {
                        startAngle = getRotateAngle(touchX, touchY)
                        touchState = TouchState.ROTATE
                        return true
                    }
                    isInsideArc(
                        event.toPoint(),
                        drawingRect.rightBottom.toPoint()
                    ) && !drawnState -> {
                        drawingRect.setTouchOffset(touchX, touchY)
                        touchState = TouchState.RESIZE
                        return true
                    }
                    isInsideArc(event.toPoint(), drawingRect.leftTop.toPoint()) && !drawnState -> {
                        removeFlag = true
                        return true
                    }
                    drawingRect.contains(event.toPointF()) -> {
                        touchState = TouchState.MOVE
                        return true
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (touchState) {
                    TouchState.MOVE -> {
                        drawingRect.offset(deltaX, deltaY)
                    }
                    TouchState.RESIZE -> {
                        drawingRect.changeDimensions(touchX, touchY)
                    }
                    TouchState.ROTATE -> {
                        rotateAngle = getRotateAngle(touchX, touchY) - startAngle
                        drawingRect.rotation += rotateAngle
                        startAngle = getRotateAngle(touchX, touchY)
                    }
                }
                updatePaths()
                return true
            }
        }
        return false
    }


    private fun isInsideArc(touchedPoint: Point, centerPoint: Point): Boolean {
        val distance =
            ((touchedPoint.x - centerPoint.x.toDouble()).pow(2.0) +
                    (touchedPoint.y - centerPoint.y.toDouble()).pow(2.0)).roundToInt()
        return distance < Math.pow(iconRadius.toDouble(), 2.0)
    }

    private fun updatePaths() {
        pathRect.set(drawingRect.getPath())
        pathCircle.reset()
        pathCircle.addCircle(
            drawingRect.rightIop.x,
            drawingRect.rightIop.y,
            iconRadius.toFloat(),
            CW
        )
        pathCircle.addCircle(
            drawingRect.rightBottom.x,
            drawingRect.rightBottom.y,
            iconRadius.toFloat(),
            CW
        )
        pathCircle.addCircle(
            drawingRect.leftTop.x,
            drawingRect.leftTop.y,
            iconRadius.toFloat(),
            CW
        )
        iconMatrix.setTranslate(drawingRect.leftTop.x, drawingRect.leftTop.y)
        iconMatrix.postRotate(drawingRect.rotation, drawingRect.leftTop.x, drawingRect.leftTop.y)
        var point = drawingRect.leftTop.toPoint()
        val rect =Rect()
        removeDrawable?.bounds = rect.squareFromPoint(point,iconSize)
        point=drawingRect.rightIop.toPoint()
        rotateDrawable?.bounds = rect.squareFromPoint(point, (iconSize*1.5f).toInt())
        point=drawingRect.rightBottom.toPoint()
        resizeDrawable?.bounds = rect.squareFromPoint(point,iconSize)

        setupDottedPath()
        iconMatrix.setRotate(drawingRect.rotation, drawingRect.center.x, drawingRect.center.y)
        pathDotted.transform(iconMatrix)

    }


    private fun setupDottedPath() {
        pathDotted.reset()
        val rect = RectF(drawingRect.rectF)
        rect.inset(-borderOffset, -borderOffset)
        pathDotted.moveTo(rect.left, rect.top)
        pathDotted.lineTo(rect.right, rect.top)
        pathDotted.moveTo(rect.right, rect.top)
        pathDotted.lineTo(rect.right, rect.bottom)
        pathDotted.moveTo(rect.right, rect.bottom)
        pathDotted.lineTo(rect.left, rect.bottom)
        pathDotted.moveTo(rect.left, rect.bottom)
        pathDotted.lineTo(rect.left, rect.top)

    }


    private fun getRotateAngle(x: Float, y: Float): Float {
        val point = drawingRect.center
        val point2 = PointF(x, y)
        return calcRotationAngleInDegrees(point, point2)
    }

    private fun calcRotationAngleInDegrees(centerPt: PointF, targetPt: PointF): Float {
        var angle = Math.toDegrees(
            atan2(
                targetPt.y.toDouble() - centerPt.y,
                targetPt.x.toDouble() - centerPt.x
            )
        ).toFloat()

        if (angle < 0) {
            angle += 360f
        }

        return angle
    }


    enum class TouchState {
        MOVE, RESIZE, ROTATE,
    }


}
