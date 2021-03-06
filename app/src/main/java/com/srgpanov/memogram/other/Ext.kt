package com.srgpanov.memogram.other

import android.content.res.Resources
import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.lifecycle.*
import com.srgpanov.memogram.R

import java.math.RoundingMode
import kotlin.reflect.KClass


inline fun <reified T> T.logD(message: String= "TAG $this") = Log.d(getClassSimpleName(T::class),message )
inline fun <reified T> T.logE(message: String= "TAG $this") = Log.e(getClassSimpleName(T::class),message )




fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updatePadding(
            left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
            top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
            right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
            bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
        )

        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                    top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                    right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                    bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
                )
            }
        }

        insets
    }
    requestApplyInsetsWhenAttached()
}
fun View.addSystemWindowInsetAsHeight() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            if (insets.systemWindowInsetTop != 0) {
                height = insets.systemWindowInsetTop
            }
        }

        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
fun dpToPx(dp:Int):Int{
    return (dp*Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun MotionEvent.toPointF(): PointF {
    return PointF(this.x, this.y)
}
fun MotionEvent.toPointF(point: PointF):PointF{
    point.x=this.x
    point.y=this.y
    return point
}
fun MotionEvent.toPoint(): Point {
    return Point(this.x.toInt(), this.y.toInt())
}
fun MotionEvent.toPoint(point: Point):Point{
    point.x=this.x.toInt()
    point.y=this.y.toInt()
    return point
}




inline fun <reified T> T.getClassSimpleName( enclosingClass: KClass<*>?): String =
    if(T::class.java.simpleName.isNotBlank()) {
        T::class.java.simpleName
    }
    else { // Enforce the caller to pass a class to retrieve its simple name
        enclosingClass?.simpleName ?: "Anonymous"
    }