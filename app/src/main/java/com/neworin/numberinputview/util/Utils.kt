package com.neworin.numberinputview.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast

/**
 * author : ZhangFubin
 * time   : 2018/04/12
 * desc   :
 */

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.dp2px(dpValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * 获取屏幕的宽度（单位：px）
 *
 * @return 屏幕宽
 */
fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.x
}

/**
 * 获取屏幕的高度（单位：px）
 *
 * @return 屏幕高
 */
fun getScreenHeight(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.y
}

