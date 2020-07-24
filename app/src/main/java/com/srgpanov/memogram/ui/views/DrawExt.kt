package com.srgpanov.memogram.ui.views

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun squareFromPoint(point:PointF,side:Int):RectF{
    return RectF(point.x-side/2,point.y-side/2,point.x+side/2,point.y+side/2)
}
fun squareFromPoint(point: Point, side:Int):Rect{
    return Rect(point.x-side/2,point.y-side/2,point.x+side/2,point.y+side/2)
}

fun Rect.centerToPoint (point: Point): Rect {
    val halfWidth = width()/2
    val halfHeight = height()/2
    this.offsetTo(point.x - halfWidth,point.y-halfHeight)
    return this
}
fun RectF.centerToPoint (point: PointF):RectF{
    val halfWidth = width()/2
    val halfHeight = height()/2
    this.offsetTo(point.x - halfWidth,point.y-halfHeight)
    return this
}
fun Context.colorRes(@ColorRes color:Int):Int{
    return ContextCompat.getColor(this,color)
}