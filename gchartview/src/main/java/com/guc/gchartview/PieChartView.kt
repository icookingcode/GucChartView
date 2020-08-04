package com.guc.gchartview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.guc.gchartview.model.PieData
import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * Created by guc on 2020/7/13.
 * Description：自定义PieChart
 */
class PieChartView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    companion object {
        const val STYLE_SOLID = 0
        const val STYLE_HOLLOW = 1
        const val POSITION_INNER = 0
        const val POSITION_OUTER = 1
        const val LINE_STYLE_MATCH = 0
        const val LINE_STYLE_FIXED = 1
    }

    private val minHeight = dp2px(150f)
    private val minWidth = dp2px(300f)
    private lateinit var mPaint: Paint
    private lateinit var mPieData: MutableList<PieData>

    //圆弧半径
    private var mRadius = 0
    private var mRadiusInner = 0

    //圆弧中心点小圆点的圆心半径
    private var mCenterPointRadius = 0
    private var mCenterPointPosition = POSITION_INNER
    private var mCenterPointStyle = STYLE_SOLID
    private var mCenterPointOffset = 0

    //指示线宽度
    private var mLineWidth = 0
    private var mLineStyle = LINE_STYLE_MATCH
    private var mLineLength = 0
    private var mLineOffsetX = 0

    //圆弧开始绘制的角度
    private var mStartAngle = 0f
    private var mBgColor = 0
    private var mInnerCircleColor = 0
    private var mCenterX = 0 //圆心x坐标
    private var mCenterY = 0 //圆心y坐标
    private lateinit var mTextRect: Rect

    //是否展示文字
    private var isShowRateText = true
    private var isDrawCenterText = true
    private var mTextSize4Describe = 0
    private var mSumScore = 0f

    //手势监听
    private val gestureDetector = GestureDetector(context, this)
    private var mStartX: Float = 0f
    private var mStartY: Float = 0f
    var rotateEnable = true

    init {
        initAttrs(attrs, defStyleAttr)
        initPaint()
    }

    fun setData(data: List<PieData>) {
        if (!::mPieData.isInitialized) {
            mPieData = ArrayList()
        } else {
            mPieData.clear()
        }
        mPieData.addAll(data)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            //边沿线和文字所占的长度：(xOffset + yOffset + textRect.width())
            heightSize = minHeight
        } else if (heightMode == MeasureSpec.EXACTLY) {
            if (heightSize < minHeight) {
                heightSize = minHeight
            }
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = minWidth
        } else {
            if (widthSize < minWidth) {
                widthSize = minWidth
            }
        }
        //保存测量结果
        mCenterX = widthSize / 2
        mCenterY = heightSize / 2
        setMeasuredDimension(widthSize, heightSize)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBgColor)
        mPaint.style = Paint.Style.FILL
        if (::mPieData.isInitialized && mPieData.size > 0) { //开始绘制
            calculateRateList(mPieData)
            var data: PieData
            //1.绘制圆饼
            val rectF = RectF(
                (mCenterX - mRadius).toFloat(),
                (mCenterY - mRadius).toFloat(),
                (mCenterX + mRadius).toFloat(),
                (mCenterY + mRadius).toFloat()
            )
            val rectF2 = RectF(
                (mCenterX - mRadius - mCenterPointOffset).toFloat(),
                (mCenterY - mRadius - mCenterPointOffset).toFloat(),
                (mCenterX + mRadius + mCenterPointOffset).toFloat(),
                (mCenterY + mRadius + mCenterPointOffset).toFloat()
            )
            for (i in mPieData.indices) {
                data = mPieData[i]
                mPaint.color = data.colorLine
                val sweepAngle = data.proportion * 360
                canvas.drawArc(rectF, mStartAngle, sweepAngle, true, mPaint)
                dealPoint(rectF2, mStartAngle, data.proportion * 360 / 2, data)
                mStartAngle += sweepAngle
            }
            //(2)处理每块圆饼弧的中心点，绘制折线，显示对应的文字
            if (isShowRateText) {
                drawableIndicateAndDescribe(canvas, mPieData)
            }
            //(3)绘制内部中空的圆
            mPaint.color = mInnerCircleColor
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(
                mCenterX.toFloat(),
                mCenterY.toFloat(),
                mRadiusInner.toFloat(),
                mPaint
            )
            if (isDrawCenterText) {
                mPaint.color = Color.BLACK
                mPaint.textSize = mTextSize4Describe.toFloat()
                val sum = String.format("%d", mSumScore.toInt())
                mPaint.getTextBounds(sum, 0, sum.length, mTextRect)
                canvas.drawText(
                    sum,
                    (mCenterX - mTextRect.width() / 2).toFloat(),
                    (mCenterY + mTextRect.height() / 2).toFloat(),
                    mPaint
                )
            }
        } else { //暂无数据
            mPaint.color = Color.BLACK
            mPaint.textSize = mTextSize4Describe.toFloat()
            val describe = "暂无数据"
            mPaint.getTextBounds(describe, 0, describe.length, mTextRect)
            canvas.drawText(
                describe,
                (mCenterX - mTextRect.width() / 2).toFloat(),
                (mCenterY + mTextRect.height() / 2).toFloat(),
                mPaint
            )
        }
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val array: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PieChartView, defStyleAttr, 0)
        mBgColor = array.getColor(R.styleable.PieChartView_bgColor, Color.WHITE)
        mInnerCircleColor = array.getColor(
            R.styleable.PieChartView_innerCircleColor,
            Color.WHITE
        )
        mRadius = array.getDimension(R.styleable.PieChartView_radius, dp2px(70f).toFloat()).toInt()
        mRadiusInner =
            array.getDimension(R.styleable.PieChartView_radius, dp2px(40f).toFloat()).toInt()
        mCenterPointRadius =
            array.getDimension(R.styleable.PieChartView_radiusCenterPoint, dp2px(2f).toFloat())
                .toInt()
        mCenterPointPosition =
            array.getInt(R.styleable.PieChartView_centerPointPosition, POSITION_INNER)
        mCenterPointStyle = array.getInt(R.styleable.PieChartView_centerPointStyle, STYLE_SOLID)
        mCenterPointOffset =
            array.getDimension(R.styleable.PieChartView_centerPointOffset, dp2px(5f).toFloat())
                .toInt()
        mTextSize4Describe =
            array.getDimension(R.styleable.PieChartView_textSize4Describe, 32f).toInt()
        mLineWidth =
            array.getDimension(R.styleable.PieChartView_lineWith, dp2px(2f).toFloat()).toInt()
        mLineLength =
            array.getDimension(R.styleable.PieChartView_lineWith, dp2px(30f).toFloat()).toInt()
        mLineOffsetX =
            array.getDimension(R.styleable.PieChartView_lineOffsetX, dp2px(10f).toFloat()).toInt()
        mLineStyle = array.getInt(R.styleable.PieChartView_lineStyle, LINE_STYLE_MATCH)
        isDrawCenterText = array.getBoolean(R.styleable.PieChartView_isDrawCenterText, true)
        mStartAngle = array.getFloat(R.styleable.PieChartView_startAngle, 0f)
        rotateEnable = array.getBoolean(R.styleable.PieChartView_rotateEnable, true)
        array.recycle()
    }

    private fun initPaint() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.isAntiAlias = true
        mTextRect = Rect()
    }

    /**
     * 处理每块圆饼弧的中心点，绘制折线，显示对应的文字
     *
     * @param canvas    画布
     * @param mPieDatas 数据
     */
    @Suppress("CanBeVal")
    private fun drawableIndicateAndDescribe(
        canvas: Canvas,
        mPieDatas: List<PieData>
    ) {
        var leftNum = 0
        var rightNum = 0
        //变量声明为val后排序失效
        var leftPieData: MutableList<PieData> = ArrayList() //左侧数据
        var rightPieData: MutableList<PieData> = ArrayList() //右侧数据
        for (pieData in mPieDatas) {
            if (pieData.centerPoint.x < mCenterX) {
                leftNum++
                pieData.isRight = false
                leftPieData.add(pieData)
            } else {
                rightNum++
                pieData.isRight = true
                rightPieData.add(pieData)
            }
        }
        //变量声明为val后排序失效
        leftPieData.sort()
        rightPieData.sort()
        val perRightHeight =
            if (rightNum != 0) 2 * (mRadius + 2 * mCenterPointRadius) / rightNum else 2 * (mRadius + 2 * mCenterPointRadius) //右侧间距
        val perLeftHeight =
            if (leftNum != 0) 2 * (mRadius + 2 * mCenterPointRadius) / leftNum else 2 * (mRadius + 2 * mCenterPointRadius) //左侧间距
        var pieData: PieData
        var path: Path
        //画左侧指示点
        for (i in leftPieData.indices) {
            pieData = leftPieData[i]
            mPaint.strokeWidth = mLineWidth.toFloat()
            //画指示点
            mPaint.color = pieData.colorLine
            mPaint.style =
                if (mCenterPointStyle == STYLE_SOLID) Paint.Style.FILL else Paint.Style.STROKE
            canvas.drawCircle(
                if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.x.toFloat() else getLineEndX(
                    false
                ),
                if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.y.toFloat() else (perLeftHeight * i + perLeftHeight / 2).toFloat(),
                mCenterPointRadius.toFloat(),
                mPaint
            )
            //划线
            mPaint.color = pieData.colorLine
            mPaint.style = Paint.Style.STROKE
            path = Path()
            path.moveTo(pieData.centerPoint.x.toFloat(), pieData.centerPoint.y.toFloat())
            path.lineTo(
                (mCenterX - mRadius - mLineOffsetX).toFloat(),
                (perLeftHeight * i + perLeftHeight / 2).toFloat()
            )
            path.lineTo(getLineEndX(false), (perLeftHeight * i + perLeftHeight / 2).toFloat())
            canvas.drawPath(path, mPaint)

            //修复空心
            if (mCenterPointStyle == STYLE_HOLLOW) {
                mPaint.color = mBgColor
                mPaint.style = Paint.Style.FILL
                canvas.drawCircle(
                    if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.x.toFloat() else getLineEndX(
                        false
                    ),
                    if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.y.toFloat() else (perLeftHeight * i + perLeftHeight / 2).toFloat(),
                    mCenterPointRadius - mPaint.strokeWidth / 2,
                    mPaint
                )
            }
            //写字
            mPaint.style = Paint.Style.FILL
            mPaint.strokeWidth = dp2px(0.75f).toFloat()
            mPaint.color = pieData.colorDescribe
            mPaint.textSize = mTextSize4Describe.toFloat()
            val describe = pieData.describe
            mPaint.getTextBounds(describe, 0, describe!!.length, mTextRect)
            canvas.drawText(
                describe,
                dp2px(10f).toFloat(),
                (perLeftHeight * i + perLeftHeight / 2 + mTextRect.height()).toFloat(),
                mPaint
            )
            val score =
                String.format("%d(%.1f%%)", pieData.score.toInt(), pieData.proportion * 100)
            mPaint.color = pieData.colorScore
            mPaint.getTextBounds(score, 0, score.length, mTextRect)
            canvas.drawText(
                score,
                dp2px(10f).toFloat(),
                (perLeftHeight * i + perLeftHeight / 2 - 3 * mLineWidth / 2).toFloat(),
                mPaint
            )
        }
        //画右侧指示点
        for (i in rightPieData.indices) {
            pieData = rightPieData[i]
            mPaint.strokeWidth = mLineWidth.toFloat()
            //画指示点
            mPaint.color = pieData.colorLine
            mPaint.style =
                if (mCenterPointStyle == STYLE_SOLID) Paint.Style.FILL else Paint.Style.STROKE
            canvas.drawCircle(
                if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.x.toFloat() else getLineEndX(
                    true
                ),
                if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.y.toFloat() else (perRightHeight * i + perRightHeight / 2).toFloat(),
                mCenterPointRadius.toFloat(),
                mPaint
            )
            //画线
            mPaint.color = pieData.colorLine
            mPaint.style = Paint.Style.STROKE
            path = Path()
            path.moveTo(pieData.centerPoint.x.toFloat(), pieData.centerPoint.y.toFloat())
            path.lineTo(
                (mCenterX + mRadius + mLineOffsetX).toFloat(),
                (perRightHeight * i + perRightHeight / 2).toFloat()
            )
            path.lineTo(
                getLineEndX(true),
                (perRightHeight * i + perRightHeight / 2).toFloat()
            )
            canvas.drawPath(path, mPaint)
            //修复空心
            if (mCenterPointStyle == STYLE_HOLLOW) {
                mPaint.color = mBgColor
                mPaint.style = Paint.Style.FILL
                canvas.drawCircle(
                    if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.x.toFloat() else getLineEndX(
                        true
                    ),
                    if (mCenterPointPosition == POSITION_INNER) pieData.centerPoint.y.toFloat() else (perRightHeight * i + perRightHeight / 2).toFloat(),
                    mCenterPointRadius - mPaint.strokeWidth / 2,
                    mPaint
                )
            }
            //写字
            mPaint.style = Paint.Style.FILL
            mPaint.strokeWidth = dp2px(0.75f).toFloat()
            mPaint.color = pieData.colorDescribe
            mPaint.textSize = mTextSize4Describe.toFloat()
            val describe = pieData.describe
            mPaint.getTextBounds(describe, 0, describe!!.length, mTextRect)
            canvas.drawText(
                describe,
                (2 * mCenterX - dp2px(10f) - mTextRect.width()).toFloat(),
                (perRightHeight * i + perRightHeight / 2 + mTextRect.height()).toFloat(),
                mPaint
            )
            val score =
                String.format("%d(%.1f%%)", pieData.score.toInt(), pieData.proportion * 100)
            mPaint.color = pieData.colorScore
            mPaint.getTextBounds(score, 0, score.length, mTextRect)
            canvas.drawText(
                score,
                (2 * mCenterX - dp2px(10f) - mTextRect.width()).toFloat(),
                (perRightHeight * i + perRightHeight / 2 - 3 * mLineWidth / 2).toFloat(),
                mPaint
            )
        }
    }

    /**
     * 获取横线结尾x坐标
     */
    private fun getLineEndX(isRight: Boolean): Float {
        return if (isRight) {
            if (mLineStyle == LINE_STYLE_MATCH) {
                2 * mCenterX - dp2px(10f).toFloat()
            } else {
                (mCenterX + mRadius + mLineOffsetX + mLineLength).toFloat()
            }
        } else {
            if (mLineStyle == LINE_STYLE_MATCH) {
                dp2px(10f).toFloat()
            } else {
                (mCenterX - mRadius - mLineOffsetX - mLineLength).toFloat()
            }
        }
    }

    //处理获取每段弧中点坐标
    private fun dealPoint(
        rectF: RectF,
        startAngle: Float,
        endAngle: Float,
        data: PieData
    ) {
        val path = Path()
        //通过Path类画一个90度（180—270）的内切圆弧路径
        path.addArc(rectF, startAngle, endAngle)
        val measure = PathMeasure(path, false)
        val cords = floatArrayOf(0f, 0f)
        //利用PathMeasure分别测量出各个点的坐标值coords
        val divisor = 1
        measure.getPosTan(measure.length / divisor, cords, null)
        val x = cords[0]
        val y = cords[1]
        val point = Point(x.roundToInt(), y.roundToInt())
        data.centerPoint = point
    }

    /**
     * 计算占比
     *
     * @param mPieData
     */
    private fun calculateRateList(mPieData: List<PieData>) {
        var sum = 0f
        for (i in mPieData.indices) {
            sum += mPieData[i].score
        }
        mSumScore = sum
        for (i in mPieData.indices) {
            mPieData[i].proportion = mPieData[i].score / sum
        }
    }

    private fun dp2px(dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 获取格式化的保留两位数的数
     */
    private fun getFormatPercentRate(dataValue: Float): String {
        val decimalFormat = DecimalFormat(".00") //构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(dataValue)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (rotateEnable) {
            this.gestureDetector.onTouchEvent(event)
            true
        } else super.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        mStartX = e?.x ?: 0f
        mStartY = e?.y ?: 0f
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        mStartAngle += if (mStartX - mCenterX >= 0f) {//右侧
            -distanceY / height * 180
        } else {//左侧
            distanceY / height * 180
        }
        invalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }

}