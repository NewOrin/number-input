package com.neworin.numberinputview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * author : ZhangFubin
 * time   : 2017/12/11
 * desc   : 带边框TextView
 */
class BorderTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {
    /**
     * 边框线宽
     */
    private var strokeWidth = DEFAULT_STROKE_WIDTH
    /**
     * 边框颜色
     */
    private var strokeColor = Color.TRANSPARENT
    /**
     * 圆角半径
     */
    private var cornerRadius = 4
    /**
     * 画边框所使用画笔对象
     */
    private val mStrokePaint = Paint()
    private var mBgPaint: Paint? = null
    /**
     * 是否填充矩形
     */
    private var isFill = false

    private var rectF: RectF? = null

    override fun onDraw(canvas: Canvas) {
        mStrokePaint.isAntiAlias = true
        mStrokePaint.strokeWidth = strokeWidth.toFloat()
        mStrokePaint.color = strokeColor
        mStrokePaint.style = if (isFill) Paint.Style.FILL else Paint.Style.STROKE
        mStrokePaint.strokeCap = Paint.Cap.ROUND
        //解决canvas.drawRoundRect时，四个圆角线较粗问题
        if (rectF == null) {
            val d = strokeWidth / 2
            rectF = RectF(d.toFloat(), d.toFloat(), (measuredWidth - d).toFloat(), (measuredHeight - d).toFloat())
        }
        if (mBgPaint != null) {
            canvas.drawRoundRect(rectF!!, cornerRadius.toFloat(), cornerRadius.toFloat(), mBgPaint!!)
        }
        canvas.drawRoundRect(rectF!!, cornerRadius.toFloat(), cornerRadius.toFloat(), mStrokePaint)
        super.onDraw(canvas)
    }

    fun setStrokeColor(color: Int) {
        strokeColor = color
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        mBgPaint = Paint()
        mBgPaint!!.isAntiAlias = true
        mBgPaint!!.color = color
        mBgPaint!!.style = Paint.Style.FILL
        invalidate()
    }

    fun setStrokeWidth(width: Int) {
        this.strokeWidth = width
        invalidate()
    }

    fun setCornerRadius(cornerRadius: Int) {
        this.cornerRadius = cornerRadius
        invalidate()
    }

    /**
     * 是否填充矩形
     *
     * @param isFill
     */
    fun setIsFill(isFill: Boolean) {
        this.isFill = isFill
        invalidate()
    }

    companion object {
        /**
         * 默认边框宽度, 0.5dp
         */
        val DEFAULT_STROKE_WIDTH = 1
        /**
         * 默认圆角半径, 2dp
         */
        val DEFAULT_CORNER_RADIUS = 2.0f
    }
}