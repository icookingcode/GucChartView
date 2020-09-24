package com.guc.gview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by Guc on 2020/9/24.
 * Description：仪表盘
 */
class PanelView(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    companion object {
        const val POINTER_TYPE_LINE = 1
        const val POINTER_TYPE_POINTER = 2
    }

    private var mWidth = 0
        set(value) {
            field = value
            centerX = value / 2f
        }
    private var mHeight = 0
        set(value) {
            field = value
            centerY = value / 2f
        }
    private var centerX = 0f
    private var centerY = 0f

    /**
     * 设置百分比 0-100
     */
    var percent = 0
        set(value) {
            field = value
            playAnim()
        }
    private var percentTemp = 0 //绘制动画临时变量

    /**
     * 最大值
     */
    var maxValue = 100
        set(value) {
            field = value
            invalidate()
        }

    /**
     *  文字内容
     */

    var text: String? = ""
        set(value) {
            field = value
            invalidate()
        }
    private var mTickTextPaint: Paint? = null
    private var textBound: Rect? = null
    private var textTike: String? = null

    //刻度宽度
    private var mTickWidth = 20f

    //第二个弧的宽度
    private var secondArcWidth: Int

    //文字矩形的宽
    private val mRectWidth = 0

    //文字矩形的高
    private val mRectHeight = 0
    private val strokeWidth = 3f
    private val strokeWidthInner = 6

    /**
     * 第二条宽弧线是否需要画突出
     */
    var needBulge = false
        set(value) {
            field = value
            playAnim()
        }

    //文字的大小
    var mTextSize: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    //设置文字颜色
    private val mTextColor: Int
    private val pointerType: Int
    private val lineSpace: Int

    /**
     * 设置圆弧颜色
     */
    var arcColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 小圆和指针颜色
     */
    var pointerColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    private var minCircleStrokeWidth = 8f
    private var largeCircleStrokeWidth = 3f
    private var minCircleRadius = 15f
    private var largeCircleRadius = 30f

    //刻度的个数
    private val mTickCount: Int
    private val mTickColor //刻度颜色
            : Int
    private val mInnerLineColor //内部弧线颜色
            : Int
    private val mOuterLineColor //外层弧线颜色
            : Int
    private val mArcUnColor //未达到的颜色
            : Int
    private var animator: ValueAnimator? = null
    private fun init() {
        mTickTextPaint = Paint()
        mTickTextPaint!!.isAntiAlias = true
        mTickTextPaint!!.color = mTickColor
        mTickTextPaint!!.textSize = mTextSize.toFloat()
        mTickTextPaint!!.style = Paint.Style.STROKE
        textBound = Rect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        mWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            dpToPx(200, mContext)
        }
        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            dpToPx(200, mContext)
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = Paint()
        p.isAntiAlias = true
        drawOuterLine(canvas, p)
        drawSecondBoldLine(canvas, p)
        drawThirdArc(canvas, p)
        if (pointerType == POINTER_TYPE_LINE)
            drawInnerCircles(canvas, p)
        drawTicks(canvas, p)
        drawPointer(canvas, p)
    }

    /**
     * 播放显示动画
     */
    fun playAnim() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, percent.toFloat()).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            addUpdateListener {
                percentTemp = (animatedValue as Float).toInt()
                postInvalidate()
            }
            start()
        }
    }

    /**
     * 设置值
     */
    fun setValue(value: Float) {
        if (value > maxValue) return
        percent = (value / maxValue * 100).toInt()
    }

    /**
     * 绘制指针
     */
    private fun drawPointer(canvas: Canvas, p: Paint) {
        p.color = pointerColor
        p.strokeWidth = 6f
        val routeAngle = 250 * percentTemp / 100f - 250 / 2f
        //按照百分比绘制刻度
        canvas.rotate(routeAngle, centerX, centerY)
        when (pointerType) {
            POINTER_TYPE_LINE -> canvas.drawLine(
                centerX,
                lineSpace.toFloat(),
                centerX,
                centerY - minCircleRadius,
                p
            )
            POINTER_TYPE_POINTER -> {
                p.style = Paint.Style.FILL
                val path = Path()
                path.moveTo(centerX, centerY + largeCircleRadius)
                path.lineTo(centerX + minCircleRadius, centerY)
                path.lineTo(centerX, 10f)
                path.lineTo(centerX - minCircleRadius, centerY)
                path.close()
                canvas.drawPath(path, p)
                p.style = Paint.Style.STROKE
            }
        }

        //将画布旋转回来
        canvas.rotate(-routeAngle, centerX, centerY)
    }

    /**
     * 画左侧刻度
     */
    private fun drawLeftTicks(canvas: Canvas, p: Paint, thirdMargin: Float) {
        //旋转的角度
        val rAngle = 250f / mTickCount
        //通过旋转画布 绘制左面的刻度
        for (i in 0 until mTickCount / 2) {
            canvas.rotate(-rAngle, centerX, centerY)
            canvas.drawLine(
                centerX,
                thirdMargin,
                centerX,
                thirdMargin + mTickWidth,
                p
            )
            textTike = (maxValue * (mTickCount / 2 - i - 1) / mTickCount).toString()
            mTickTextPaint!!.getTextBounds(textTike, 0, textTike!!.length, textBound)
            canvas.drawText(
                textTike!!,
                (mWidth - textBound!!.width()) / 2.toFloat(),
                thirdMargin + 1.5f * mTickWidth + textBound!!.height(),
                mTickTextPaint!!
            ) //画刻度
        }
        //现在需要将将画布旋转回来
        canvas.rotate(rAngle * mTickCount / 2, centerX, centerY)
    }

    /**
     * 画右侧刻度
     */
    private fun drawRightTicks(canvas: Canvas, p: Paint, thirdMargin: Float) {
        //旋转的角度
        val rAngle = 250f / mTickCount
        //通过旋转画布 绘制右面的刻度
        for (i in 0 until mTickCount / 2) {
            canvas.rotate(rAngle, centerX, centerY)
            canvas.drawLine(
                mWidth / 2.toFloat(),
                thirdMargin,
                mWidth / 2.toFloat(),
                thirdMargin + mTickWidth,
                p
            )
            textTike = (maxValue * (mTickCount / 2 + i + 1) / mTickCount).toString()
            mTickTextPaint!!.getTextBounds(textTike, 0, textTike!!.length, textBound)
            canvas.drawText(
                textTike!!,
                (mWidth - textBound!!.width()) / 2.toFloat(),
                thirdMargin + 1.5f * mTickWidth + textBound!!.height(),
                mTickTextPaint!!
            ) //画刻度
        }

        //现在需要将将画布旋转回来
        canvas.rotate(-rAngle * mTickCount / 2, centerX, centerY)
    }

    /**
     * 绘制刻度和标签
     */
    private fun drawTicks(canvas: Canvas, p: Paint) {
        val thirdMargin = strokeWidth + lineSpace + secondArcWidth.toFloat()
        p.color = mTickColor
        //绘制第一条最上面的刻度
        p.strokeWidth = strokeWidth
        canvas.drawLine(
            mWidth / 2.toFloat(),
            thirdMargin,
            mWidth / 2.toFloat(),
            thirdMargin + mTickWidth,
            p
        )
        textTike = (maxValue / 2).toString()
        mTickTextPaint!!.getTextBounds(textTike, 0, textTike!!.length, textBound)
        canvas.drawText(
            textTike!!,
            (mWidth - textBound!!.width()) / 2.toFloat(),
            thirdMargin + 1.5f * mTickWidth + textBound!!.height(),
            mTickTextPaint!!
        ) //画刻度
        drawRightTicks(canvas, p, thirdMargin)
        drawLeftTicks(canvas, p, thirdMargin)
    }

    /**
     * 画内部圆
     */
    private fun drawInnerCircles(canvas: Canvas, p: Paint) {
        p.color = arcColor
        //绘制小圆外圈
        p.strokeWidth = largeCircleStrokeWidth
        canvas.drawCircle(centerX, centerY, largeCircleRadius, p)

        //绘制小圆内圈
        p.color = pointerColor
        p.strokeWidth = minCircleStrokeWidth
        canvas.drawCircle(centerX, centerY, minCircleRadius, p)
    }

    /**
     * 画第三条内部圆弧
     */
    private fun drawThirdArc(canvas: Canvas, p: Paint) {
        //内部圆弧
        p.color = mInnerLineColor
        p.strokeWidth = strokeWidthInner.toFloat()
        val thirdMargin = strokeWidth + lineSpace + secondArcWidth.toFloat()
        val thirdRect =
            RectF(thirdMargin, thirdMargin, mWidth - thirdMargin, mHeight - thirdMargin)
        canvas.drawArc(thirdRect, 145f, 250f, false, p)
    }

    /**
     * 画第二条粗线
     */
    private fun drawSecondBoldLine(canvas: Canvas, p: Paint) {
        p.color = arcColor
        p.strokeWidth = secondArcWidth.toFloat()
        val secondRectF = RectF(
            strokeWidth + lineSpace,
            strokeWidth + lineSpace,
            mWidth - strokeWidth - lineSpace,
            mHeight - strokeWidth - lineSpace
        )
        val percent = percentTemp / 100f

        //充满的圆弧的度数    -5是大小弧的偏差
        val fill = 250 * percent

        //空的圆弧的度数
        val empty = 250 - fill

        //画粗弧突出部分左端
        if (needBulge)
            canvas.drawArc(secondRectF, 135f, 11f, false, p)
        //画粗弧的充满部分
        if (fill != 0f) {
            canvas.drawArc(secondRectF, 145f, fill, false, p)
        }
        p.color = mArcUnColor
        //画弧胡的未充满部分
        canvas.drawArc(secondRectF, 145 + fill, empty, false, p)
        //画粗弧突出部分右端
        if (percent == 1f) {
            p.color = arcColor
        }
        if (needBulge)
            canvas.drawArc(secondRectF, 144 + fill + empty, 10f, false, p)
    }

    /**
     * 画最外面的线
     */
    private fun drawOuterLine(canvas: Canvas, p: Paint) {
        p.style = Paint.Style.STROKE
        p.strokeWidth = strokeWidth
        p.color = mOuterLineColor
        canvas.drawArc(
            RectF(
                strokeWidth,
                strokeWidth,
                mWidth - strokeWidth,
                mHeight - strokeWidth
            ),
            145f,
            250f,
            false,
            p
        )
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    init {
        val a =
            mContext.obtainStyledAttributes(attrs, R.styleable.PanelView, defStyleAttr, 0)
        arcColor =
            a.getColor(R.styleable.PanelView_arcColor, Color.parseColor("#5FB1ED"))
        pointerColor = a.getColor(
            R.styleable.PanelView_pointerColor,
            Color.parseColor("#C9DEEE")
        )
        mTickCount = a.getInt(R.styleable.PanelView_tickCount, 10)
        mTextSize =
            a.getDimensionPixelSize(
                R.styleable.PanelView_android_textSize, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 10f,
                    resources.displayMetrics
                ).toInt()
            )
        mTextColor =
            a.getColor(R.styleable.PanelView_android_textColor, Color.BLACK)
        text = a.getString(R.styleable.PanelView_android_text)
        secondArcWidth = a.getInt(R.styleable.PanelView_arcWidth, 50)
        mTickColor = a.getColor(
            R.styleable.PanelView_tickColor,
            Color.parseColor("#696969")
        )
        mInnerLineColor = a.getColor(
            R.styleable.PanelView_innerLineColor,
            Color.parseColor("#696969")
        )
        mOuterLineColor = a.getColor(
            R.styleable.PanelView_outerLineColor,
            Color.parseColor("#959595")
        )
        mArcUnColor = a.getColor(
            R.styleable.PanelView_arcUnColor,
            Color.parseColor("#dfeaef")
        )
        pointerType = a.getInt(R.styleable.PanelView_pointerType, POINTER_TYPE_LINE)
        lineSpace = a.getInt(R.styleable.PanelView_lineSpace, 50)
        a.recycle()
        init()
    }
}

