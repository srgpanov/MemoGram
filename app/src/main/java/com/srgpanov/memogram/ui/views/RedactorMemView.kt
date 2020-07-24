package com.srgpanov.memogram.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toRectF
import java.util.*
import java.util.concurrent.atomic.AtomicLong


class RedactorMemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "RedactorMemView"
    }

    var onTextContainerSelectedListener: OnTextContainerSelectedListener? = null

    private var viewWidth = 100
    private var viewHeight = 100

    private var handlerContainer: RedactorContainer? = null

    private val containers: MutableList<RedactorContainer> = LinkedList()
    private val aLong = AtomicLong()
    private val nextId: Long
        get() = aLong.incrementAndGet()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("RedactorMemView", "onSizeChanged: $w $h")
        viewWidth = w
        viewHeight = h
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> performActionDown(event)
            MotionEvent.ACTION_MOVE -> performActionMoveCancelAndUp(event)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> performActionMoveCancelAndUp(event)
            else -> false
        }
    }

    private fun performActionMoveCancelAndUp(event: MotionEvent): Boolean {
        if (handlerContainer != null) {
            handlerContainer?.handleMotionEvent(event)
            invalidate()
        }
        return true
    }

    private fun performActionDown(event: MotionEvent): Boolean {
        for (i in containers.size - 1 downTo 0) {
            if (containers[i].handleMotionEvent(event)) {
                if (checkRemove(i)) return true
                selectContainer(i)
                elevateContainer(i)
                invalidate()
                return true
            } else {
                if (!containers[i].isDrawState) {
                    containers[i].isDrawState = true
                    invalidate()
                }
            }
        }
        onTextContainerSelectedListener?.onNothingSelected()
        return false
    }

    private fun selectContainer(index: Int) {
        handlerContainer = containers[index]
        handlerContainer?.isDrawState = false
        val textRect = handlerContainer?.drawingRect as? TextRect
        if (textRect != null) {
            onTextContainerSelectedListener?.onContainerSelected(textRect.id,textRect.text )
        }else{
            onTextContainerSelectedListener?.onNothingSelected()
        }
    }

    private fun elevateContainer(index: Int) {
        containers.add(containers[index])
        containers.removeAt(index)
    }

    private fun checkRemove(i: Int): Boolean {
        if (containers[i].removeFlag) {
            containers.remove(containers[i])
            invalidate()
            return true
        }
        return false
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        containers.forEach {
            it.draw(canvas)
        }
    }

    fun addImage(bitmap: Bitmap) {
        val mutableBitmap =
            if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Log.d("RedactorMemView", "addImage: $viewWidth $viewHeight ")
        val imageRect = ImageRect.create(nextId, mutableBitmap, viewWidth, viewHeight)
        val redactorContainer = RedactorContainer(imageRect)
        handlerContainer?.isDrawState = true
        handlerContainer = redactorContainer
        containers.add(redactorContainer)
        invalidate()
    }

    fun addText() {
        val rect = Rect(400, 400, 600, 600).toRectF()
        val textRect = TextRect(nextId, rect, viewWidth)
        val redactorContainer = RedactorContainer(textRect)
        handlerContainer?.isDrawState = true
        handlerContainer = redactorContainer
        containers.add(redactorContainer)
        onTextContainerSelectedListener?.onContainerSelected(textRect.id,textRect.text)
        invalidate()
    }

    fun changeText(text: String) {
        val textRect = handlerContainer?.drawingRect
        if (textRect is TextRect) {
            Log.d("RedactorMemView", "changeText: $textRect")
            textRect.text = text
            invalidate()
        } else {
            throw IllegalStateException("selected not textRect")
        }

    }

    interface OnTextContainerSelectedListener {
        fun onContainerSelected(id: Long, text: String)
        fun onNothingSelected()
    }

}