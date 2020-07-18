package com.srgpanov.memogram.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.srgpanov.memogram.other.dpToPx
import java.util.*


class RedactorMemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "RedactorMemView"
    }

    var memText = "Ideal balance etalon garmony"
    lateinit var bitmap: Bitmap
    var textRect = RectF(20f, 20f, 420f, 220f)
    private var touchX = 0f
    private var touchY = 0f
    private var iconRadius = dpToPx(12)
    private var widthView = 100
    private var heightView = 100

    var deltaX = 0f
    var deltaY = 0f
    var oldX = 0f
    var oldY = 0f
    private var handlerContainer: RedactorContainer? = null

    private val containers: MutableList<RedactorContainer> = LinkedList<RedactorContainer>()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthView = w
        heightView = h
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "onTouchEvent: handlerContainer ${handlerContainer?.drawnState}")
                var needInvalidate= false
                for (i in containers.size - 1 downTo 0) {
                    if (containers[i].handleMotionEvent(event)) {
                        if (containers[i].removeFlag){
                            containers.remove(containers[i])
                            invalidate()
                            return true
                        }
                        handlerContainer = containers[i]
                        handlerContainer?.drawnState = false
                        containers.add(containers[i])
                        containers.removeAt(i)
                        invalidate()
                        return true
                    }else{
                        if (!containers[i].drawnState){
                            needInvalidate=true
                            containers[i].drawnState=true
                        }
                    }
                }
                if (needInvalidate)invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (handlerContainer!=null){
                    handlerContainer?.handleMotionEvent(event)
                    invalidate()
                }
                return true
            }


        }

        return false
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        containers.forEach {
            it.draw(canvas)
        }
        Log.d(TAG, "onDraw: invalidate ")

    }

    fun addImage(bitmap: Bitmap) {
        val mutableBitmap =
            if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val redactorContainer = RedactorContainer(mutableBitmap,widthView,heightView)
        handlerContainer?.drawnState = true
        handlerContainer=redactorContainer
        containers.add(redactorContainer)
        invalidate()
    }
    fun addText(){
        val bitmap = Bitmap.createBitmap(500,300,Bitmap.Config.ARGB_8888)
        val redactorContainer = RedactorContainer(bitmap,widthView,heightView)
        handlerContainer?.drawnState = true
        handlerContainer=redactorContainer
        containers.add(redactorContainer)
        invalidate()
    }


    private fun getBoundsForIcon(pointF: Point): Rect {
        return Rect(
            pointF.x - iconRadius,
            pointF.y - iconRadius,
            pointF.x + iconRadius,
            pointF.y + iconRadius
        )
    }
}