package com.guc.gview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.properties.Delegates

/**
 * auto&date: Created by Guc on 2017/9/21.
 * function:刻度视图
 */
class ScaleView(
    private val mCxt: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(mCxt, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    var type = SCALE_TYPE_TEMPERATURE
        //刻度尺类型 = 0
        set(value) {
            field = when (value) {
                SCALE_TYPE_TEMPERATURE -> SCALE_TYPE_TEMPERATURE
                SCALE_TYPE_GALVANOMETER -> SCALE_TYPE_GALVANOMETER
                else -> SCALE_TYPE_TEMPERATURE
            }
            invalidate()
        }
    var maxValue = 220  //刻度最大值
        set(value) {
            field = value
            invalidate()
        }
    var minValue = -20 //刻度最小值
        set(value) {
            field = value
            invalidate()
        }
    var value = 0f
        set(value) {
            field = if (value > maxValue) field else value
            invalidate()
        }
    private var mTextSize by Delegates.notNull<Int>() //文字大小 = 0
    private var mBorderWidth = 0
    private var mBorderColor by Delegates.notNull<Int>() //边框的颜色 = 0
    private var mPillarWidth by Delegates.notNull<Int>() //柱子宽度 = 0
    private var mPillarDownColor by Delegates.notNull<Int>()//柱子下边颜色 = 0
    private var mPillarUpColor by Delegates.notNull<Int>()//柱子上边颜色 = 0
    private var mWidth = 0
    private var mHeight = 0
    private val minPillarWidh: Int
    private var mBorderRect: RectF? = null
    private var mPaint: Paint? = null
    private var mTickTextPaint: Paint? = null
    private var textBound: Rect? = null
    private var mTickCount = 0
    private var mpillarHeight = 0
    private val isDrawPoint = true

    companion object {
        //温度计
        const val SCALE_TYPE_TEMPERATURE = 0

        //电流计
        const val SCALE_TYPE_GALVANOMETER = 1
    }

    init {
        minPillarWidh = dp2px(mCxt, 15f)
        initAttrs(mCxt, attrs)
        init()
    }


    private fun initAttrs(
        context: Context,
        attrs: AttributeSet?
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ScaleView)
        type = a.getInt(R.styleable.ScaleView_scaleType, 0) //默认温度计
        mTickCount = a.getInt(R.styleable.ScaleView_tickCount, 10) //刻度的个数
        mTextSize = a.getDimensionPixelSize(
            R.styleable.ScaleView_android_textSize, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12f,
                resources.displayMetrics
            ).toInt()
        )
        mBorderWidth = a.getDimensionPixelSize(
            R.styleable.ScaleView_borderWidth, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2f,
                resources.displayMetrics
            ).toInt()
        )
        mBorderColor = a.getColor(
            R.styleable.ScaleView_borderColor,
            Color.parseColor("#5c626d")
        )
        mPillarDownColor = a.getColor(
            R.styleable.ScaleView_pillarDownColor,
            Color.parseColor("#70b4f0")
        )
        mPillarUpColor = a.getColor(
            R.styleable.ScaleView_pillarUpColor,
            Color.parseColor("#f4fbfe")
        )
        mPillarWidth = a.getDimensionPixelSize(
            R.styleable.ScaleView_pillarWidth, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15f,
                resources.displayMetrics
            ).toInt()
        )
        value = a.getInt(R.styleable.ScaleView_defaultValue, 0).toFloat()
        maxValue = a.getInt(R.styleable.ScaleView_defaultMaxValue, 220)
        minValue = a.getInt(R.styleable.ScaleView_defaultMinValue, -20)
        a.recycle()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mBorderRect = RectF()
        mTickTextPaint = Paint()
        mTickTextPaint!!.isAntiAlias = true
        mTickTextPaint!!.color = mBorderColor
        mTickTextPaint!!.textSize = mTextSize.toFloat()
        mTickTextPaint!!.style = Paint.Style.STROKE
        textBound = Rect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        mPillarWidth = if (mPillarWidth > minPillarWidh) mPillarWidth else minPillarWidh
        mWidth = if (widthMode == MeasureSpec.EXACTLY) {
            if (widthSize > 2 * mPillarWidth) widthSize else 2 * mPillarWidth
        } else {
            2 * mPillarWidth
        }
        mHeight = 4 * mPillarWidth
        mHeight = if (mHeight > heightSize) mHeight else heightSize
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBorder(canvas)
        drawTicker(canvas)
        drawPillar(canvas)
    }

    //画边框
    private fun drawBorder(canvas: Canvas) {
        mPaint!!.color = mBorderColor
        mPaint!!.strokeWidth = mBorderWidth.toFloat()
        mPaint!!.style = Paint.Style.STROKE
        mBorderRect!![mWidth - paddingRight - mPillarWidth - (mBorderWidth / 2).toFloat(), dp2px(
            mCxt,
            5f
        ) + paddingTop.toFloat(), mWidth - paddingRight - (mBorderWidth / 2).toFloat()] =
            mHeight - dp2px(mCxt, 5f) + paddingBottom.toFloat()
        canvas.drawRect(mBorderRect!!, mPaint!!)
    }

    /**
     * /画刻度
     * 由上往下画
     * @param canvas
     */
    private fun drawTicker(canvas: Canvas) {
        val statX = mWidth - paddingRight - mPillarWidth - mBorderWidth / 2
        mpillarHeight = mHeight - paddingTop - paddingBottom - 2 * dp2px(mCxt, 5f)
        var textTick: String
        mPaint!!.color = mBorderColor
        mPaint!!.strokeWidth = dp2px(mCxt, 1f).toFloat()
        mPaint!!.style = Paint.Style.STROKE
        for (i in 0 until mTickCount + 1) {
            canvas.drawLine(
                statX - dp2px(mCxt, 3f).toFloat(),
                (dp2px(mCxt, 5f) + (i * 1.0 / mTickCount * mpillarHeight)).toFloat(),
                statX.toFloat(),
                (dp2px(mCxt, 5f) + (i * 1.0 / mTickCount * mpillarHeight)).toFloat(),
                mPaint!!
            )
            if (i == 0) {
                textTick = maxValue.toString() + ""
                mTickTextPaint!!.getTextBounds(textTick, 0, textTick.length, textBound)
                canvas.drawText(
                    textTick,
                    statX - dp2px(mCxt, 8f) - textBound!!.width().toFloat(),
                    dp2px(mCxt, 5f) + paddingTop + textBound!!.height().toFloat(),
                    mTickTextPaint!!
                ) //最大刻度
            } else if (i == mTickCount) {
                textTick = minValue.toString() + ""
                mTickTextPaint!!.getTextBounds(textTick, 0, textTick.length, textBound)
                canvas.drawText(
                    textTick,
                    statX - dp2px(mCxt, 8f) - textBound!!.width().toFloat(),
                    dp2px(mCxt, 5f) + paddingTop + mpillarHeight.toFloat(),
                    mTickTextPaint!!
                ) //最小刻度
            }
        }
        textTick = when (type) {
            SCALE_TYPE_TEMPERATURE -> "℃"
            SCALE_TYPE_GALVANOMETER -> "A"
            else -> ""
        }
        mTickTextPaint!!.getTextBounds(textTick, 0, textTick.length, textBound)
        canvas.drawText(
            textTick,
            statX - dp2px(mCxt, 10f) - textBound!!.width().toFloat(),
            (mHeight + textBound!!.height()) / 2.toFloat(),
            mTickTextPaint!!
        ) //单位
    }

    /**
     * 画柱子
     * @param canvas
     */
    private fun drawPillar(canvas: Canvas) {
        //画下部
        val curTickY = (mHeight - dp2px(
            mCxt,
            5f
        ) + paddingBottom - (value - minValue) / (maxValue - minValue) * mpillarHeight).toInt()
        val tickX = mWidth - paddingRight - mBorderWidth / 2 - mPillarWidth / 2
        mPaint!!.color = mPillarDownColor
        mPaint!!.strokeWidth = mPillarWidth - mBorderWidth.toFloat()
        mPaint!!.style = Paint.Style.STROKE
        canvas.drawLine(
            tickX.toFloat(),
            mHeight - dp2px(mCxt, 5f) + paddingBottom - mBorderWidth / 2.toFloat(),
            tickX.toFloat(),
            curTickY.toFloat(),
            mPaint!!
        )
        //画上部
        mPaint!!.color = mPillarUpColor
        if (value != maxValue.toFloat()) canvas.drawLine(
            tickX.toFloat(),
            curTickY.toFloat(),
            tickX.toFloat(),
            paddingTop + mBorderWidth / 2 + dp2px(mCxt, 5f).toFloat(),
            mPaint!!
        )
        //是否画指针
        if (isDrawPoint && value != minValue.toFloat()) {
            val startX = mWidth - paddingRight - mPillarWidth - mBorderWidth / 2
            val mPoint = Path()
            mPoint.moveTo(startX - dp2px(mCxt, 3f).toFloat(), curTickY.toFloat())
            mPoint.lineTo(startX - dp2px(mCxt, 7f).toFloat(), curTickY + dp2px(mCxt, 3f).toFloat())
            mPoint.lineTo(startX - dp2px(mCxt, 7f).toFloat(), curTickY - dp2px(mCxt, 3f).toFloat())
            mPoint.close()
            mPaint!!.strokeWidth = 1f
            mPaint!!.style = Paint.Style.FILL
            mPaint!!.color = mBorderColor
            canvas.drawPath(mPoint, mPaint!!)
        }
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}