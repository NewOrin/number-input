package com.neworin.numberinputview

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

/**
 * author : ZhangFubin
 * time   : 2018/04/12
 * desc   : 自定义数字输入框
 */
class NumberInputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * DEFAULT VALUES
     */
    private val TEXT_SIZE_DEFAULT = 16
    private val TEXT_COLOR_DEFAULT = ContextCompat.getColor(context, R.color.text_default_color)
    private val TEXT_DIVIDER_DEFAULT = 5
    private val TEXT_WIDTH_DEFAULT = 40
    private val TEXT_DEFAULT_COUNTS = 6

    private val BORDER_WIDTH_DEFAULT = 2
    private val BORDER_COLOR_DEFAULT = ContextCompat.getColor(context, R.color.border_default_color)
    private val BORDER_RADIUS_DEFAULT = 4
    private val IS_FILL_DEFAULT = false

    private lateinit var mTextViews: MutableList<BorderTextView>
    //默认为6个
    private var mTextViewCounts = TEXT_DEFAULT_COUNTS
    private var mTextSize = TEXT_SIZE_DEFAULT
    private var mTextColor = TEXT_COLOR_DEFAULT
    private var mTextWidth = TEXT_WIDTH_DEFAULT
    private var mDividerWidth = TEXT_DIVIDER_DEFAULT
    private var mBorderWidth = BORDER_WIDTH_DEFAULT
    private var mBorderColor = BORDER_COLOR_DEFAULT
    private var mBorderRadius = BORDER_RADIUS_DEFAULT
    private var mIsFill = IS_FILL_DEFAULT

    private lateinit var mLinearLayout: LinearLayout
    private lateinit var mEditText: EditText

    private val mInputSb = StringBuffer()
    private var mInputCompleteListener: InputCompleteListener? = null

    private val PARENT_DEFAULT_WIDTH = context.dp2px((TEXT_WIDTH_DEFAULT + TEXT_DIVIDER_DEFAULT * 2) * (TEXT_DEFAULT_COUNTS).toFloat())
    private val PARENT_DEFAULT_HEIGHT = context.dp2px((TEXT_WIDTH_DEFAULT + TEXT_DIVIDER_DEFAULT * 2).toFloat())

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val displayMetrics = context.resources.displayMetrics
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NumberInputView)
        mTextSize = ta.getInt(R.styleable.NumberInputView_niv_text_size_sp, TEXT_SIZE_DEFAULT)
        mTextColor = ta.getColor(R.styleable.NumberInputView_niv_text_color, TEXT_COLOR_DEFAULT)
        mTextWidth = ta.getDimensionPixelSize(R.styleable.NumberInputView_niv_text_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_WIDTH_DEFAULT.toFloat(), displayMetrics).toInt())
        mDividerWidth = ta.getDimensionPixelSize(R.styleable.NumberInputView_niv_text_divider, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_DIVIDER_DEFAULT.toFloat(), displayMetrics).toInt())
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.NumberInputView_niv_border_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                BORDER_WIDTH_DEFAULT.toFloat(), displayMetrics).toInt())
        mBorderRadius = ta.getDimensionPixelSize(R.styleable.NumberInputView_niv_border_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                BORDER_RADIUS_DEFAULT.toFloat(), displayMetrics).toInt())
        mBorderColor = ta.getColor(R.styleable.NumberInputView_niv_border_color, mBorderColor)
        mIsFill = ta.getBoolean(R.styleable.NumberInputView_niv_is_fill, IS_FILL_DEFAULT)
        mTextViewCounts = ta.getInt(R.styleable.NumberInputView_niv_count, TEXT_DEFAULT_COUNTS)
        if (mTextViewCounts > 10) {
            mTextViewCounts = TEXT_DEFAULT_COUNTS
        }
        ta.recycle()

        initTextView()
        initLinearLayout()
        initEditText()
        val linearParams = LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        linearParams.gravity = Gravity.CENTER
        addView(mLinearLayout, linearParams)
        val editParams = LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        editParams.gravity = Gravity.CENTER
        addView(mEditText, editParams)
        setListener()
    }

    /**
     * 初始化TextView
     */
    private fun initTextView() {
        mTextViews = mutableListOf()
        for (i in 1..mTextViewCounts) {
            val tv = BorderTextView(context)
            tv.let {
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat())
                it.setTextColor(mTextColor)
                it.gravity = Gravity.CENTER
                it.setIsFill(mIsFill)
                it.setStrokeColor(mBorderColor)
                if (mIsFill) {
                    it.setStrokeWidth(0)
                } else {
                    it.setStrokeWidth(mBorderWidth)
                }
                it.setCornerRadius(mBorderRadius)
            }
            mTextViews.add(tv)
        }
    }

    /**
     * 初始化LinearLayout
     */
    private fun initLinearLayout() {
        val tvParams = RelativeLayout.LayoutParams(mTextWidth, mTextWidth)
        tvParams.setMargins(mDividerWidth, 0, mDividerWidth, 0)
        tvParams.addRule(Gravity.CENTER)
        mLinearLayout = LinearLayout(context)
        mLinearLayout.let {
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER
        }
        for (tv in mTextViews) {
            mLinearLayout.addView(tv, tvParams)
        }
    }

    /**
     * 初始化EditText
     */
    private fun initEditText() {
        mEditText = EditText(context)
        mEditText.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.isFocusable = true
            it.isCursorVisible = false
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true) {
                    mEditText.setText("")
                    if (mInputSb.length >= mTextViewCounts) {
                        mInputCompleteListener?.inputComplete(mInputSb.toString())
                        return
                    }
                    if (mInputSb.length < mTextViewCounts) {
                        mInputSb.append(s.toString())
                        mInputSb.forEachIndexed { index, c ->
                            mTextViews[index].text = c.toString()
                        }
                        if (mInputSb.length == mTextViewCounts) {
                            mInputCompleteListener?.inputComplete(mInputSb.toString())
                        }
                    }
                }
            }
        })
        mEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (mInputSb.isNotEmpty()) {
                    mInputSb.delete(mInputSb.length - 1, mInputSb.length)
                    mTextViews[mInputSb.length].text = ""
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(handleWidth(widthMeasureSpec), handleHeight(heightMeasureSpec))
    }

    /**
     * Handle width
     */
    private fun handleWidth(measureSpec: Int): Int {
        val result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        result = when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(PARENT_DEFAULT_WIDTH, specSize)
            MeasureSpec.UNSPECIFIED -> PARENT_DEFAULT_WIDTH
            else -> PARENT_DEFAULT_WIDTH
        }
        return result
    }

    /**
     * Handle height
     */
    private fun handleHeight(measureSpec: Int): Int {
        val result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        result = when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(PARENT_DEFAULT_HEIGHT, specSize)
            MeasureSpec.UNSPECIFIED -> PARENT_DEFAULT_HEIGHT
            else -> PARENT_DEFAULT_HEIGHT
        }
        return result
    }

    fun setInputCompleteListener(inputCompleteListener: InputCompleteListener) {
        this.mInputCompleteListener = inputCompleteListener
    }

    interface InputCompleteListener {
        fun inputComplete(content: String)
    }
}
