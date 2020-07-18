package com.srgpanov.memogram.ui.views

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF

fun RectF.squareFromPoint(point:PointF,side:Int):RectF{
    return RectF(point.x-side/2,point.y-side/2,point.x+side/2,point.y+side/2)
}
fun Rect.squareFromPoint(point: Point, side:Int):Rect{
    return Rect(point.x-side/2,point.y-side/2,point.x+side/2,point.y+side/2)
}